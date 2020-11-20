/**
 * Copyright 2017, GeoSolutions Sas.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */


const axios = require('../../../MapStore2/web/client/libs/ajax');

const urlUtil = require('url');
const Rx = require('rxjs');
const {castArray, isNil} = require('lodash');
const {parseString} = require('xml2js');
const {stripPrefix} = require('xml2js/lib/processors');

const {interceptOGCError} = require('../../../MapStore2/web/client/utils/ObservableUtils');
const {getCapabilitiesUrl} = require('../../../MapStore2/web/client/utils/LayersUtils');
const FilterUtils = require('../../../MapStore2/web/client/utils/FilterUtils');
const requestBuilder = require('../../../MapStore2/web/client/utils/ogc/WFS/RequestBuilder');
const {getFeature, query, sortBy, propertyName} = requestBuilder({ wfsVersion: "1.1.0" });

const toDescribeURL = ({ name, search = {}, url, describeFeatureTypeURL} = {}) => {
    const parsed = urlUtil.parse(describeFeatureTypeURL || search.url || url, true);
    return urlUtil.format(
        {
            ...parsed,
            search: undefined, // this allows to merge parameters correctly
            query: {
                ...parsed.query,

                service: "WFS",
                version: "1.1.0",
                typeName: name,
                outputFormat: 'application/json',
                request: "DescribeFeatureType"
            }
        });
};
const toLayerCapabilitiesURL = ({name, search = {}, url} = {}) => {
    const URL = getCapabilitiesUrl({name, url: search && search.url || url });
    const parsed = urlUtil.parse(URL, true);
    return urlUtil.format(
        {
            ...parsed,
            search: undefined, // this allows to merge parameters correctly
            query: {
                ...parsed.query,
                service: "WFS",
                version: "1.1.1",
                request: "GetCapabilities"
            }
        });
};
const Url = require('url');
const { isObject } = require('lodash');

// this is a workaround for https://osgeo-org.atlassian.net/browse/GEOS-7233. can be removed when fixed
const workaroundGEOS7233 = ({ totalFeatures, features, ...rest } = {}, { startIndex } = {}, originalSize) => {
    if (originalSize > totalFeatures && originalSize === startIndex + features.length && totalFeatures === features.length) {
        return {
            ...rest,
            features,
            totalFeatures: originalSize
        };
    }
    return {
        ...rest,
        features,
        totalFeatures
    };

};


const getPagination = (filterObj = {}, options = {}) =>
    filterObj.pagination
    || !isNil(options.startIndex)
    && !isNil(options.maxFeatures)
    && {
        startIndex: options.startIndex,
        maxFeatures: options.maxFeatures
    };
/**
 * Get Features in json format. Intercepts request with 200 errors and workarounds GEOS-7233 if `totalFeatures` is passed
 * @param {string} searchUrl URL of WFS service
 * @param {object} filterObj FilterObject
 * @param {number} totalFeatures optional number to use in case of a previews request, needed to workaround GEOS-7233.
 * @return {Observable} a stream that emits the GeoJSON or an error.
 */
const getJSONFeature = (searchUrl, filterObj, options = {}, layer) => {
    const data = FilterUtils.getWFSFilterData(filterObj, options);

    const urlParsedObj = Url.parse(searchUrl, true);
    let params = isObject(urlParsedObj.query) ? urlParsedObj.query : {};
    params.service = 'WFS';
    params.outputFormat = 'json';
    const queryString = Url.format({
        protocol: urlParsedObj.protocol,
        host: urlParsedObj.host,
        pathname: urlParsedObj.pathname,
        query: params
    });


    if (layer.type === 'vector') {
        return Rx.Observable.defer(() => new Promise((resolve) => {
            if(!layer.originalFeatures || layer.originalFeatures[0].properties.contextDescriptionName !== layer.features[0].properties.contextDescriptionName){
                layer.originalFeatures = layer.features;
            }
            let features =  createFeatureCollection(layer.originalFeatures);
            let featuresFiltered = getFeaturesFiltered(features, filterObj);
            if(featuresFiltered){
                layer.features = featuresFiltered.features;
            }
            resolve(featuresFiltered);
        }));
    }
    return Rx.Observable.defer(() =>
        axios.post(queryString, data, {
            timeout: 60000,
            headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' }
        }))
        .let(interceptOGCError)
        .map((response) => workaroundGEOS7233(response.data, getPagination(filterObj, options), options.totalFeatures));
};

/**
 * Same of `getJSONFeature` but auto-retries possible errors due to no-primary-key issues
 * (when you are using pagination vendor parameters for GeoServer and the primary-key of the table was not set).
 * When this kind of error occurs, auto-retry using the sortOptions passed.
 * present. .
 * @param {string} searchUrl URL of WFS service
 * @param {object} filterObj Filter object
 * @param {object} options params that can contain `totalFeatures` and sort options
 * @return {Observable} a stream that emits the GeoJSON or an error.
 */
const getJSONFeatureWA = (searchUrl, filterObj, { sortOptions = {}, ...options }, layer = {}) =>
    getJSONFeature(searchUrl, filterObj, options, layer)
        .catch(error => {
            if (error.name === "OGCError" && error.code === 'NoApplicableCode') {
                return getJSONFeature(searchUrl, {
                    ...filterObj,
                    sortOptions
                }, options);
            }
            throw error;
        });

/**
 * Same of `getJSONFeatureWA` but accepts the layer as first parameter.
 * Accepts filter as a string or object. In case of string filter manages pagination and
 * sort options from 3rd parameter. This is a little different from normal getJSONFeature,
 * anyway this version is more rational, separating pagination, sorting etc... from filter.
 * TODO: make this more flexible to manage also object filter with a clear default rule.
 * @param {object} layer the layer to search
 * @param {object|string} filter the filter object or string of the filter. To maintain
 * retro compatibility the filter object can contain pagination info, typeName and so on.
 * @param {object} options the options (pagination, totalFeatures and so on ...)
 */
const getLayerJSONFeature = ({ search = {}, url, name } = {}, filter, {sortOptions, propertyName: pn, ...options} = {}) =>
    // TODO: Apply sort workaround for no primary keys
    getJSONFeature(search.url || url,
        filter && typeof filter === 'object' ? {
            ...filter,
            typeName: name || filter.typeName
        } : getFeature(
            query(name,
                [
                    ...( sortOptions ? [sortBy(sortOptions.sortBy, sortOptions.sortOrder)] : []),
                    ...(pn ? [propertyName(pn)] : []),
                    ...(filter ? castArray(filter) : [])
                ]),
            options), // options contains startIndex, maxFeatures and it can be passed as it is
        options)
    // retry using 1st propertyNames property, if present, to workaround primary-key issues
        .catch(error => {
            if (error.name === "OGCError" && error.code === 'NoApplicableCode' && !sortOptions && pn && pn[0]) {
                return getJSONFeature(search.url || url,
                    filter && typeof filter === 'object' ? {
                        ...filter,
                        typeName: name || filter.typeName
                    } : getFeature(
                        query(name,
                            [
                                sortBy(pn[0]),
                                ...(pn ? [propertyName(pn)] : []),
                                ...(filter ? castArray(filter) : [])
                            ]),
                        options), // options contains startIndex, maxFeatures and it can be passed as it is
                    options);
            }
            throw error;
        });

module.exports = {
    getJSONFeature,
    getLayerJSONFeature,
    getJSONFeatureWA,
    describeFeatureType: ({layer}) =>
        Rx.Observable.defer(() =>
            axios.get(toDescribeURL(layer))).let(interceptOGCError),
    getLayerWFSCapabilities: ({layer}) =>
        Rx.Observable.defer( () => axios.get(toLayerCapabilitiesURL(layer)))
            .let(interceptOGCError)
            .switchMap( response => Rx.Observable.bindNodeCallback( (data, callback) => parseString(data, {
                    tagNameProcessors: [stripPrefix],
                    explicitArray: false,
                    mergeAttrs: true
                }, callback))(response.data)
            )
};

const createFeatureCollection = (features) => (
    {
        crs: {type: "name", properties: {name: "urn:ogc:def:crs:EPSG::4326"}},
        numberMatched: features.length,
        numberReturned: features.length,
        timeStamp: "2020-07-20T11:36:20.118Z",
        totalFeatures: features.length,
        type: 'FeatureCollection',
        features: features
    }
)

const getFeaturesFiltered = (features, filterObj) =>{
    if(filterObj.filterFields && filterObj.filterFields.length !== 0) {
        const featuresFiltered = features.features.filter(feature => filterFeatures(feature, filterObj.filterFields));

        features.features = featuresFiltered;
        features.numberMatched = featuresFiltered.length;
        features.numberReturned = featuresFiltered.length;
        features.totalFeatures = featuresFiltered.length;
        return features;
    }
    return features;

}

const filterFeatures = (feature, filterFields) =>{

    for(let i = 0; i< filterFields.length; i++){
        if(feature.properties[filterFields[i].attribute] === undefined ){

            return false;

        }
        if(filterFields[i].type === "string" &&
            !feature.properties[filterFields[i].attribute].toLowerCase().includes(filterFields[i].value.toLowerCase())){

            return false;

        }

        if(filterFields[i].type === "number" && !feature.properties[filterFields[i].attribute].includes(filterFields[i].value)){

            return false;
        }

        if(filterFields[i].type === "date"){

            let dateFeature = new Date(feature.properties[filterFields[i].attribute]);
            let dateFilter = new Date(filterFields[i].value.startDate);

            if(dateFeature.getFullYear() !== dateFilter.getFullYear() ||
                dateFeature.getMonth() !== dateFilter.getMonth() ||
                dateFeature.getDay() !== dateFilter.getDay() ){

                return false;
            }
        }

    }
    return true;
}

