import * as Rx from 'rxjs';
import axios from 'axios';
import {head} from 'lodash';
import assign from 'object-assign';
import {addLayer, updateNode, changeLayerProperties, removeLayer} from '../../../MapStore2/web/client/actions/layers';
import {changeDrawingStatus} from '../../../../MapStore2/web/client/actions/draw';
import {actions, loadedContexts, gotMe, loadInitError, typeViewChanged, loadActionError, viewType} from '../actions/signalement-management-action';
import {signalementLayerSelector} from '../selectors/signalement-management-selector';
import {setLayer, openFeatureGrid, closeFeatureGrid} from '../../../../MapStore2/web/client/actions/featuregrid';

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
                const url = backendURLPrefix + "/task/search/geojson?contextName=" + action.context.name +
                    "&asAdmin=" + (action.viewType == viewType.MY ? "false":"true");

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
            if( signalementsLayer) {
                return Rx.Observable.from(([updateNode('signalements', 'layer', {
                    features : [createNewFeature(action)]
                })]).concat([
                    changeLayerProperties('signalements', {visibility: true})
                ]));
            } else {
                return Rx.Observable.from((
                    [
                    addLayer({
                        type: 'vector',
                        visibility: true,
                        id: 'signalements',
                        name: "Signalements",
                        rowViewer: viewer,
                        hideLoading: true,
                        style: action.style,
                        handleClickOnLayer: true
                    })]
                    ).concat([
                        updateNode('signalements', 'layer', {features : [createNewFeature(action)]}),
                        changeLayerProperties('signalements', {visibility: true})
                ]));
            }
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

export const openTabularViewEpic = (action$, store) => 
    action$.ofType(actions.OPEN_TABULAR_VIEW)
        .switchMap((action) => {
            console.log("sigm epics open");
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === 'signalements'));
            if( signalementsLayer) {
                return Rx.Observable.from(([setLayer('signalements')] ).concat([openFeatureGrid()
                ]));
            } else {
                return Rx.Observable.from(
                    [addLayer({
                        type: 'vector',
                        visibility: true,
                        id: 'signalements',
                        name: "Signalements",
                        rowViewer: viewer,
                        hideLoading: true,
                        style: action.style,
                        handleClickOnLayer: true
                        })]
                    ).concat(
                        [
                        setLayer('signalements'),
                        openFeatureGrid()
                        ]);
            }
        });

export const closeTabularViewEpic = (action$, store) => 
        action$.ofType(actions.CLOSE_TABULAR_VIEW)
            .switchMap((action) => {
                console.log("sigm epics close");
                const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === 'signalements'));
                return Rx.Observable.from((signalementsLayer ? 
                [
                    closeFeatureGrid()
                ] : null));
            });        