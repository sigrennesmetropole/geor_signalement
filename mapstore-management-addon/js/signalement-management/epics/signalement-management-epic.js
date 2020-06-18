import * as Rx from 'rxjs';
import axios from 'axios';
import {head} from 'lodash';
import assign from 'object-assign';
import {addLayer, updateNode, changeLayerProperties, removeLayer} from '../../../MapStore2/web/client/actions/layers';
import {changeDrawingStatus} from '../../../../MapStore2/web/client/actions/draw';
import {actions, loadedContexts, gotMe, loadInitError, typeViewChanged, loadActionError} from '../actions/signalement-management-action';
import {signalementLayerSelector} from '../selectors/signalement-management-selector';

let backendURLPrefix = "http://localhost:8082";

/*export const configureBackendUrl = (value) => {
    console.log("sigm configure backend url:" + value);
    backendURLPrefix = value;
};*/

export const loadContextsEpic = (action$) =>
    action$.ofType(actions.CONTEXTS_LOAD)
        .switchMap((action) => {
            console.log("sigm epics load contexts");
            if (action.contexts) {
                return Rx.Observable.of(loadedContexts(action.contexts)).delay(0);
            }
            console.log("sigm back " + backendURLPrefix);
            const url = backendURLPrefix + "/user/contexts";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedContexts(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.contexts.error", e)));
        });
        
export const loadManagementMeEpic = (action$) =>
    action$.ofType(actions.USER_ME_GET)
        .switchMap((action) => {
            console.log("sigm epics me");
            if (action.user) {
                return Rx.Observable.of(gotMe(action.user)).delay(0);
            }
            const url = backendURLPrefix + "/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.me.error", e)));
        }); 
    
export const loadViewDataEpic = (action$) =>
        action$.ofType(actions.CHANGE_TYPE_VIEW)
            .switchMap((action) => {
                console.log("sigm epics change type view");
                const url = backendURLPrefix + "/task/search/geojson";
                return Rx.Observable.defer(() => axios.get(url))
                    .switchMap((response) => Rx.Observable.of(typeViewChanged(action.viewType, response.data)))
                    .catch(e => Rx.Observable.of(loadActionError("signalement-management.load.searchTask.error", e)));
            });         

export const updateViewDataEpic = (action$, store) => 
    action$.ofType(actions.TYPE_VIEW_CHANGED)
        .switchMap((action) => {
            console.log("sigm epics type view changed");
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === 'signalements'));
            const featureCollection = action.geometry;
            return Rx.Observable.from((signalementsLayer ? [updateNode('signalements', 'layer', {
                features : [createNewFeature(action)]
                //features : signalementLayerSelector(store.getState()).features.concat([createNewFeature(action)])
                //features: signalementLayerSelector(store.getState()).features.map(f => assign({}, f, {
                //    properties: f.properties.id === action.id ? assign({}, f.properties, action.properties, action.fields) : f.properties,
                //    features: f.properties.id === action.id ? featureCollection : f.features,
                //    style: f.properties.id === action.id ? action.style : f.style
                //}))//.concat(action.newFeature ? [createNewFeature(action)] : [])
            })] : [
                addLayer({
                    type: 'vector',
                    visibility: true,
                    id: 'signalements',
                    name: "Signalements",
                    rowViewer: viewer,
                    hideLoading: true,
                    style: action.style,
                    features: [createNewFeature(action)],
                    handleClickOnLayer: true
                })
            ]).concat([
                //changeDrawingStatus("clean", store.getState().signalements.featureType || '', "signalements", [], {}),
                changeLayerProperties('signalements', {visibility: true})
            ]));
        });     
        
const createNewFeature = (action) => {
    console.log("sigm createNewFeature");
    console.log(action);
    return action.data;
    /*return {
        type: "FeatureCollection",
        properties: assign({}, action.properties, action.fields, {id: action.id}),
        features: action.geometry,
        style: assign({}, action.style, {highlight: false})
    };*/
};