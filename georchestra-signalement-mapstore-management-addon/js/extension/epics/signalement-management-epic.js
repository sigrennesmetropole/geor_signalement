import * as Rx from 'rxjs';
import axios from 'axios';
import {head} from 'lodash';
import {addLayer, changeLayerProperties, updateNode, browseData,selectNode} from '@mapstore/actions/layers';
import {changeMapInfoState, closeIdentify, FEATURE_INFO_CLICK} from "@mapstore/actions/mapInfo";
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
    loadTaskViewer,
    viewType, closeViewer, getTask
} from '../actions/signalement-management-action';
import {closeFeatureGrid, openFeatureGrid, setLayer} from '@mapstore/actions/featuregrid';
import {isSignalementManagementActivateAndSelected} from "@js/extension/selectors/signalement-management-selector";
import {
    backendURLPrefix, RIGHT_SIDEBAR_MARGIN_LEFT,
    SIGNALEMENT_MANAGEMENT_LAYER_ID,
    SIGNALEMENT_MANAGEMENT_LAYER_NAME, SIGNALEMENT_TASK_VIEWER_WIDTH
} from "../constants/signalement-management-constants";
import {error, success} from '@mapstore/actions/notifications';
import {
    FORCE_UPDATE_MAP_LAYOUT, forceUpdateMapLayout,
    UPDATE_MAP_LAYOUT,
    updateDockPanelsList,
    updateMapLayout
} from "@mapstore/actions/maplayout";
import {TOGGLE_CONTROL} from "@mapstore/actions/controls";

let currentLayout;

/**
 * Catch GFI response on identify load event and close identify if Signalement identify tabs is selected
 * TODO: take showIdentify pluginCfg param into account
 * @param {*} action$
 * @param {*} store
 */
export function loadTaskViewerEpic(action$, store) {
    return action$.ofType(FEATURE_INFO_CLICK)
        .filter((action) => action.layer === 'signalements' || isSignalementManagementActivateAndSelected(store.getState(), SIGNALEMENT_MANAGEMENT_LAYER_ID))
        .switchMap((action) => {
            let responses = (store.getState().mapInfo.responses?.length) ? store.getState().mapInfo.responses[0] : {};
            // si features présentent dans la zone de clic
            if (responses?.response && responses.response?.features && responses.response.features.length) {
                let features = responses.response.features;
                let clickedPoint = responses.queryParams;
                let layout = store.getState().maplayout;
                layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: layout.layout.leftPanel, ...layout.boundingMapRect, right: SIGNALEMENT_TASK_VIEWER_WIDTH + RIGHT_SIDEBAR_MARGIN_LEFT, boundingMapRect: {...layout.boundingMapRect, right: RIGHT_SIDEBAR_MARGIN_LEFT + 38}, boundingSidebarRect: layout.boundingSidebarRect}
                currentLayout = layout;
                return Rx.Observable.of(loadTaskViewer(features, clickedPoint))
                    .concat(Rx.Observable.of(updateDockPanelsList("signalement_task_viewer", "add", "right")))
                    .concat(Rx.Observable.of(updateMapLayout(layout)))
                    .concat(Rx.Observable.of(closeIdentify()));
            }
            return  Rx.Observable.of(closeViewer()).concat(Rx.Observable.of(forceUpdateMapLayout()).delay(0));
        });
}

export function closeTaskViewerEpic(action$, store) {
    return action$.ofType(actions.CLOSE_TASK_VIEWER)
        .filter((action) => store && store.getState() &&
            store.getState().signalementManagement.taskViewerOpen)
        .switchMap((action) => {
            let layout = store.getState().maplayout;
            layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: layout.layout.leftPanel, ...layout.boundingMapRect, right: layout.boundingSidebarRect.right, boundingMapRect: {...layout.boundingMapRect, right: layout.boundingSidebarRect.right}, boundingSidebarRect: layout.boundingSidebarRect}
            currentLayout = layout;
            return Rx.Observable.of(updateDockPanelsList("signalement_task_viewer", "remove", "right"))
                .concat(Rx.Observable.of(updateMapLayout(layout)))
        })
}

export function onOpeningAnotherRightPanel(action$, store) {
    return action$.ofType(TOGGLE_CONTROL)
        .filter((action) => store && store.getState() &&
            action.control !== 'signalement_task_viewer' &&
            store.getState().maplayout.dockPanels.right.includes("signalement_task_viewer") &&
            store.getState().maplayout.dockPanels.right.includes(action.control))
        .switchMap((action) => {
            return Rx.Observable.of(updateDockPanelsList("signalement_task_viewer", "remove", "right"))
                .concat(Rx.Observable.of(closeViewer()));
        })
}

export function onUpdatingLayoutWhenPluiPanelOpened(action$, store) {
    return action$.ofType(UPDATE_MAP_LAYOUT, FORCE_UPDATE_MAP_LAYOUT)
        .filter((action) => store && store.getState() &&
            !!store.getState().taskViewerOpen &&
            currentLayout?.right !== action?.layout?.right)
        .switchMap((action) => {
            let layout = store.getState().maplayout;
            layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: layout.layout.leftPanel, ...layout.boundingMapRect, right: RIGHT_SIDEBAR_MARGIN_LEFT + RIGHT_SIDEBAR_MARGIN_LEFT, boundingMapRect: {...layout.boundingMapRect, right: RIGHT_SIDEBAR_MARGIN_LEFT + RIGHT_SIDEBAR_MARGIN_LEFT}, boundingSidebarRect: layout.boundingSidebarRect};
            currentLayout = layout;
            return Rx.Observable.of(updateMapLayout(layout));
        });
}

export const initSignalementManagementEpic = (action$) =>
action$.ofType(actions.INIT_SIGNALEMENT)
    .switchMap((action) => {
        window.signalementMgmt.debug("sig epics init:"+ action.url);
        if( action.url ) {	        	
        	backendURLPrefix = action.url;
        }
        return Rx.Observable.of(initSignalementManagementDone()).delay(0);
    });

export const loadContextsEpic = (action$) =>
    action$.ofType(actions.CONTEXTS_LOAD)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics load contexts");
            if (action.contexts) {
                return Rx.Observable.of(loadedContexts(action.contexts)).delay(0);
            }
            window.signalementMgmt.debug("sigm back " + backendURLPrefix);
            const url = backendURLPrefix + "/user/contexts";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedContexts(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.contexts.error", e)));
        });

export const loadManagementMeEpic = (action$) =>
    action$.ofType(actions.USER_ME_GET)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics me");
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
            window.signalementMgmt.debug("sigm epics task");

            const url = backendURLPrefix + "/task/" + action.id ;
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.get.task.error", e)));
        });

export const downloadAttachmentEpic = (action$) =>
    action$.ofType(actions.DOWNLOAD_ATTACHMENT)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics download attachment");
            const url = backendURLPrefix + "/reporting/" + action.attachment.uuid + "/download/" + action.attachment.id ;
            window.open(url);
           return Rx.Observable.empty();
        });

export const claimTaskEpic = (action$) =>
    action$.ofType(actions.CLAIM_TASK)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics claim");

            const url = backendURLPrefix + "/task/claim/" + action.id ;
            return Rx.Observable.defer(() => axios.put(url))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.claim.error", e)));
        });

export const updateAndDoActionEpic = (action$) =>
    action$.ofType(actions.UPDATE_DO_ACTION)
        .switchMap((action) => {

            window.signalementMgmt.debug("sigm epics update & do action");

            const urlUpdate = backendURLPrefix + "/task/update" ;
            const urlDoAction = backendURLPrefix + "/task/do/" + action.task.id + "/" + action.actionName;

           return Rx.Observable.fromPromise(axios.put(urlUpdate, action.task)
               .then(() => axios.put(urlDoAction)))
               .switchMap(() => {
                   let switchActions = [
                       changeTypeView(action.viewType, action.task.asset.contextDescription, action.task.functionalId),
                       success({
                           title: "signalement-management.do-action.success.title",
                           message: "signalement-management.do-action.success.message",
                           uid: "signalement-management.do-action.success.",
                           position: "tr",
                           autoDismiss: 5
                   })];
                   // fermer le viewer si on passe à done ou cancelled
                   if (action.actionName !== 'handled') {
                       switchActions.push(closeViewer());
                   }
                   return Rx.Observable.from(switchActions);
               })
               .catch(e => Rx.Observable.from([
                   // loadTaskActionError("signalement-management.doIt.error", e),
                   error({
                       title: "signalement-management.do-action.fail.title",
                       message: "signalement-management.do-action.fail.message",
                       uid: "signalement-management.do-action.fail.",
                       position: "tr",
                       autoDismiss: 5
                   })
               ]));

        });

export const updateTaskEpic = (action$) =>
    action$.ofType(actions.UPDATE_TASK)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics update task");

            const url = backendURLPrefix + "/task/update" ;
            return Rx.Observable.defer(() => axios.put(url, action.task))
                .switchMap((response) => Rx.Observable.of(gotTask(response.data)))
                .catch(e => Rx.Observable.of(loadTaskActionError("signalement-management.update.error", e)));
        });

export const loadViewDataEpic = (action$) =>
    action$.ofType(actions.CHANGE_TYPE_VIEW)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics change type view");
            const url = backendURLPrefix + "/task/search/geojson?contextName=" + action.context.name +
                "&asAdmin=" + (action.viewType === viewType.MY ? "false":"true");

            return Rx.Observable.fromPromise(axios.get(url))
                .switchMap((response) => {
                    // Parcours des nouvelles features pour vérifier si la task actuelle a été mise à jour
                    let updateCurrentTask = null;
                    window.signalementMgmt.debug('load view response', response.data.features);
                    window.signalementMgmt.debug('currentFunctionalId', action.taskFunctionalId);
                    if (response.data.features) {
                        for (let i = 0; i < response.data.features.length; i++) {
                            if (response.data.features[i].properties.functionalId === action.taskFunctionalId) {
                                updateCurrentTask = response.data.features[i].properties.id;
                                break;
                            }
                        }
                    }
                    window.signalementMgmt.debug('updateCurrentTask', updateCurrentTask);

                    let switchActions = [typeViewChanged(action.viewType, response.data)];
                    if (updateCurrentTask !== null) {
                        switchActions.push(getTask(updateCurrentTask));
                    }
                    return Rx.Observable.from(switchActions);
                })
                .catch(e => Rx.Observable.of(loadActionError("signalement-management.load.searchTask.error", e)));
        });

export const updateViewDataEpic = (action$, store) =>
    action$.ofType(actions.TYPE_VIEW_CHANGED)
        .switchMap((action) => {
            window.signalementMgmt.debug("sigm epics type view changed");
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
            window.signalementMgmt.debug("sigm epics open");
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
            window.signalementMgmt.debug("sigm epics close");
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
