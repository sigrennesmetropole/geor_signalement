import * as Rx from 'rxjs';
import axios from 'axios';
import {head} from 'lodash';
import {addLayer, changeLayerProperties, updateNode, browseData,selectNode} from '@mapstore/actions/layers';
import {changeMapInfoState} from "@mapstore/actions/mapInfo";
import {
    actions,
    initSignalementManagementDone,
    displayMapView,
    gotMe,
    gotTask,
    loadActionError,
    loadedContexts,
    loadInitError,
    loadTaskActionError,
    typeViewChanged,
    changeTypeView,
    viewType
} from '../actions/signalement-management-action';
import {closeFeatureGrid, openFeatureGrid, setLayer} from '@mapstore/actions/featuregrid';
import {closeIdentify} from '@mapstore/actions/mapInfo';

const SIGNALEMENT_MANAGEMENT_LAYER_ID = 'signalements';
const SIGNALEMENT_MANAGEMENT_LAYER_NAME = 'Signalements';

let backendURLPrefix = "/signalement";

export const initSignalementManagementEpic = (action$) =>
action$.ofType(actions.INIT_SIGNALEMENT)
    .switchMap((action) => {
        console.log("sig epics init:"+ action.url);
        if( action.url ) {	        	
        	backendURLPrefix = action.url;
        }
        return Rx.Observable.of(initSignalementManagementDone()).delay(0);
    });

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

export const loadTaskEpic = (action$) =>
    action$.ofType(actions.TASK_GET)
        .switchMap((action) => {
            console.log("sigm epics task");

            const url = backendURLPrefix + "/task/" + action.id ;
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.get.task.error", e)));
        });

export const downloadAttachmentEpic = (action$) =>
    action$.ofType(actions.DOWNLOAD_ATTACHMENT)
        .switchMap((action) => {
            console.log("sigm epics download attachment");
            const url = backendURLPrefix + "/reporting/" + action.attachment.uuid + "/download/" + action.attachment.id ;
            window.open(url);
           return Rx.Observable.empty();
        });

export const claimTaskEpic = (action$) =>
    action$.ofType(actions.CLAIM_TASK)
        .switchMap((action) => {
            console.log("sigm epics claim");

            const url = backendURLPrefix + "/task/claim/" + action.id ;
            return Rx.Observable.defer(() => axios.put(url))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.claim.error", e)));
        });

export const updateAndDoActionEpic = (action$) =>
    action$.ofType(actions.UPDATE_DO_ACTION)
        .switchMap((action) => {

            console.log("sigm epics update & do action");

            const urlUpdate = backendURLPrefix + "/task/update" ;
            const urlDoAction = backendURLPrefix + "/task/do/" + action.task.id + "/" + action.actionName;

           return Rx.Observable.fromPromise(axios.put(urlUpdate, action.task)
               .then(() => axios.put(urlDoAction)))
               .switchMap(() =>  {
                   return  Rx.Observable.of( changeTypeView(action.viewType, action.task.asset.contextDescription), closeIdentify())})
               .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.doIt.error", e)));;

        });

export const updateTaskEpic = (action$) =>
    action$.ofType(actions.UPDATE_TASK)
        .switchMap((action) => {
            console.log("sigm epics update task");

            const url = backendURLPrefix + "/task/update" ;
            return Rx.Observable.defer(() => axios.put(url, action.task))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.update.error", e)));
        });

export const loadViewDataEpic = (action$) =>
    action$.ofType(actions.CHANGE_TYPE_VIEW)
        .switchMap((action) => {
            console.log("sigm epics change type view");
            const url = backendURLPrefix + "/task/search/geojson?contextName=" + action.context.name +
                "&asAdmin=" + (action.viewType === viewType.MY ? "false":"true");

            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(typeViewChanged(action.viewType, response.data)))
                .catch(e => Rx.Observable.of(loadActionError("signalement-management.load.searchTask.error", e)));
        });

export const updateViewDataEpic = (action$, store) =>
    action$.ofType(actions.TYPE_VIEW_CHANGED)
        .switchMap((action) => {
            console.log("sigm epics type view changed");
            // Affichage de la carte
            return Rx.Observable.of(displayMapView(action.data));
        });

export const displayMapViewDataEpic = (action$, store) =>
    action$.ofType(actions.DISPLAY_MAP_VIEW)
        .switchMap((action) => {
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            return Rx.Observable.from((signalementsLayer
                    ? [updateNode(SIGNALEMENT_MANAGEMENT_LAYER_ID, 'layer', {features: createNewFeatures(action)})]
                    : [addLayer({
                        handleClickOnLayer: true,
                        hideLoading: true,
                        id: SIGNALEMENT_MANAGEMENT_LAYER_ID,
                        name: SIGNALEMENT_MANAGEMENT_LAYER_NAME,
                        style: undefined,
                        type: "vector",
                        visibility: true,
                        features: createNewFeatures(action),
                        viewer: {
                            type: 'TaskViewer'
                        }

                    }),selectNode(SIGNALEMENT_MANAGEMENT_LAYER_ID,"layer",false)]
            ).concat([
                changeLayerProperties(SIGNALEMENT_MANAGEMENT_LAYER_ID, {visibility: true}),
                changeMapInfoState(true)
            ]));
        });


export const openTabularViewEpic = (action$, store) =>
    action$.ofType(actions.OPEN_TABULAR_VIEW)
        .switchMap((action) => {
            console.log("sigm epics open");
            const url = backendURLPrefix + "/task/geojson/properties?contextName=" + action.context.name;
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            if( signalementsLayer) {
                return Rx.Observable.of(browseData({...signalementsLayer, url: url}));
            } else {
                return Rx.Observable.from(
                    [addLayer({
                        type: 'vector',
                        visibility: true,
                        id:SIGNALEMENT_MANAGEMENT_LAYER_ID,
                        name: SIGNALEMENT_MANAGEMENT_LAYER_NAME,
                        rowViewer: viewer,
                        hideLoading: true,
                        style: action.style,
                        handleClickOnLayer: true
                    })]
                ).concat(
                    [
                        setLayer(SIGNALEMENT_MANAGEMENT_LAYER_ID),
                        openFeatureGrid()
                    ]);
            }
        });

export const closeTabularViewEpic = (action$, store) =>
    action$.ofType(actions.CLOSE_TABULAR_VIEW)
        .switchMap((action) => {
            console.log("sigm epics close");
            const signalementsLayer = head(store.getState().layers.flat.filter(l => l.id === SIGNALEMENT_MANAGEMENT_LAYER_ID));
            return Rx.Observable.from((signalementsLayer
                    ? [closeFeatureGrid()]
                    : null
            ));
        });

const createNewFeatures = (action) => {
    return action.featureCollection && action.featureCollection.features
        ? action.featureCollection.features
        : [];
};
