import * as Rx from 'rxjs';
import axios from 'axios';
//import {changedGeometriesSelector} from "@mapstore/selectors/draw";
import {changeDrawingStatus, END_DRAWING, GEOMETRY_CHANGED} from "@mapstore/actions/draw";
import {changeMapInfoState} from "@mapstore/actions/mapInfo";
import {error, success} from '@mapstore/actions/notifications';
import {
    actions,
    initSignalementDone,
    loadedAttachmentConfiguration,
    addedAttachment,
    removedAttachment,
    loadedLayers,
    loadedThemas,
    gotMe,
    draftCreated,
    draftCanceled,
    loadActionError,
    loadInitError,
    setDrawing,
    taskCreated,
    updateLocalisation,
    setTaskCreationFail,
    initDrawingSupport,
    openPanel,
    closePanel
} from '../actions/signalement-action';
import {
    FeatureProjection,
    GeometryType, RIGHT_SIDEBAR_MARGIN_LEFT,
    SIGNALEMENT_PANEL_WIDTH
} from "../constants/signalement-constants";
import {
    FORCE_UPDATE_MAP_LAYOUT,
    UPDATE_MAP_LAYOUT,
    updateDockPanelsList,
    updateMapLayout
} from "@mapstore/actions/maplayout";
import {TOGGLE_CONTROL, toggleControl} from "@mapstore/actions/controls";
import {signalementSidebarControlSelector} from "@js/extension/selectors/signalement-selector";

let backendURLPrefix = "/signalement";
let currentLayout;

export const initSignalementEpic = (action$) =>
	action$.ofType(actions.INIT_SIGNALEMENT)
	    .switchMap((action) => {
            window.signalement.debug("sig epics init:"+ action.url);
	        if( action.url ) {	        	
	        	backendURLPrefix = action.url;
	        }
	        return Rx.Observable.of(initSignalementDone()).delay(0);
	    });

export const openSignalementPanelEpic = (action$, store) =>
    action$.ofType(TOGGLE_CONTROL)
        .filter(action => action.control === "signalement" && !!store.getState() && !!signalementSidebarControlSelector(store.getState()))
        .switchMap(() => {

            let layout = store.getState().maplayout;
            layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: false, ...layout.boundingMapRect, right: SIGNALEMENT_PANEL_WIDTH+RIGHT_SIDEBAR_MARGIN_LEFT, boundingMapRect: {...layout.boundingMapRect, right: SIGNALEMENT_PANEL_WIDTH+RIGHT_SIDEBAR_MARGIN_LEFT}, boundingSidebarRect: layout.boundingSidebarRect}
            window.signalement.debug("sig panel signalement added to right dockpanels list");
            currentLayout = layout;
            return Rx.Observable.from([updateDockPanelsList('signalement', 'add', 'right'), openPanel(null), updateMapLayout(layout), initDrawingSupport()]);
        });

export const closeSignalementPanelEpic = (action$, store) =>
    action$.ofType(TOGGLE_CONTROL, actions.SIGNALEMENT_DRAFT_CANCELED)
        .filter(action => action.type === actions.SIGNALEMENT_DRAFT_CANCELED || (action.control === "signalement" && !!store.getState() && !signalementSidebarControlSelector(store.getState())))
        .switchMap((action) => {
            const actionsList = [updateDockPanelsList('signalement', 'remove', 'right')]
            if (action.type === actions.SIGNALEMENT_DRAFT_CANCELED) {
                actionsList.push(toggleControl('signalement'))
            } else {
                actionsList.push(closePanel())
            }
            let layout = store.getState().maplayout;
            layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: false, ...layout.boundingMapRect, right: layout.boundingSidebarRect.right, boundingMapRect: {...layout.boundingMapRect, right: layout.boundingSidebarRect.right}, boundingSidebarRect: layout.boundingSidebarRect}
            window.signalement.debug("sig panel signalement removed from right dockpanels list");
            currentLayout = layout;
            return Rx.Observable.from(actionsList).concat(Rx.Observable.of(updateMapLayout(layout)));
        });

export function onOpeningAnotherRightPanel(action$, store) {
    return action$.ofType(TOGGLE_CONTROL)
        .filter((action) => store && store.getState() &&
            action.control !== 'signalement' &&
            store.getState().maplayout.dockPanels.right.includes("signalement") &&
            store.getState().maplayout.dockPanels.right.includes(action.control))
        .switchMap((action) => {
            return Rx.Observable.of(updateDockPanelsList("signalement", "remove", "right"))
                .concat(Rx.Observable.of(closePanel()));
        })
}

export function onUpdatingLayoutWhenPluiPanelOpened(action$, store) {
    return action$.ofType(UPDATE_MAP_LAYOUT, FORCE_UPDATE_MAP_LAYOUT)
        .filter((action) => store && store.getState() &&
            !!signalementSidebarControlSelector(store.getState()) &&
            currentLayout?.right !== action?.layout?.right)
        .switchMap((action) => {
            let layout = store.getState().maplayout;
            layout = {transform: layout.layout.transform, height: layout.layout.height, rightPanel: true, leftPanel: layout.layout.leftPanel, ...layout.boundingMapRect, right: SIGNALEMENT_PANEL_WIDTH + RIGHT_SIDEBAR_MARGIN_LEFT, boundingMapRect: {...layout.boundingMapRect, right: SIGNALEMENT_PANEL_WIDTH + RIGHT_SIDEBAR_MARGIN_LEFT}, boundingSidebarRect: layout.boundingSidebarRect};
            currentLayout = layout;
            return Rx.Observable.of(updateMapLayout(layout));
        });
}

export const loadAttachmentConfigurationEpic = (action$) =>
    action$.ofType(actions.ATTACHMENT_CONFIGURATION_LOAD)
        .switchMap((action) => {
            window.signalement.debug("sig epics attachment config");
            if (action.attachmentConfiguration) {
                return Rx.Observable.of(loadedAttachmentConfiguration(action.attachmentConfiguration)).delay(0);
            }
            window.signalement.debug("sig back " + backendURLPrefix);
            const url = backendURLPrefix + "/reporting/attachment/configuration";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedAttachmentConfiguration(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.attattachmentConfiguration.error", e)));
        });

export const addAttachmentEpic = (action$) =>
    action$.ofType(actions.ADD_ATTACHMENT)
        .switchMap((action) => {
            window.signalement.debug("sig epics add attachement");
            const url = backendURLPrefix + "/reporting/" + action.attachment.uuid + "/upload";
            const formData = new FormData();
            formData.append('file',action.attachment.file);

            return Rx.Observable.defer(() => axios.post(url, formData))
                .switchMap((response) => Rx.Observable.of(addedAttachment(response.data)))
                .catch(e => Rx.Observable.of(loadActionError("signalement.attachment.error", e)));
        });

export const removeAttachmentEpic = (action$) =>
    action$.ofType(actions.REMOVE_ATTACHMENT)
        .switchMap((action) => {
            window.signalement.debug("sig epics remove attachement");
            const url = backendURLPrefix + "/reporting/" + action.attachment.uuid + "/delete/" + action.attachment.id;

            return Rx.Observable.defer(() => axios.delete(url))
                .switchMap((response) => Rx.Observable.of(removedAttachment(action.attachment.index)))
                .catch(e => Rx.Observable.of(loadActionError("signalement.attachment.delete.error", e)));
        });

export const loadThemasEpic = (action$, store) =>
    action$.ofType(actions.THEMAS_LOAD)
        .switchMap((action) => {
            window.signalement.debug("sig epics themas");
            if (action.themas) {
                return Rx.Observable.of(loadedThemas(action.themas)).delay(0);
            }
            const url = backendURLPrefix + "/reporting/contextDescription/search?contextType=THEMA";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedThemas(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.themas.error", e)));
        });

export const loadLayersEpic = (action$) =>
    action$.ofType(actions.LAYERS_LOAD)
        .switchMap((action) => {
            window.signalement.debug("sig epics layers");
            if (action.layers) {
                return Rx.Observable.of(loadedLayers(action.layers)).delay(0);
            }
            const url = backendURLPrefix + "/reporting/contextDescription/search?contextType=LAYER";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedLayers(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.layers.error", e)));
        });

export const loadMeEpic = (action$) =>
    action$.ofType(actions.USER_ME_GET)
        .switchMap((action) => {
            window.signalement.debug("sig epics me");
            if (action.user) {
                return Rx.Observable.of(gotMe(action.user)).delay(0);
            }
            const url = backendURLPrefix + "/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.me.error", e)));
        });

export const createDraftEpic = (action$) =>
    action$.ofType(actions.SIGNALEMENT_DRAFT_CREATE)
        .switchMap((action) => {
            window.signalement.debug("sig epics draft");
            const url = backendURLPrefix + "/task/draft";
            const task = { contextDescription: action.context, description: ""};
            const params = {
                timeout: 30000,
                headers: {'Accept': 'application/json', 'Content-Type': 'application/json'}
            };

            return Rx.Observable.defer(() => axios.post(url, task, params))
                .switchMap((response) => Rx.Observable.of(draftCreated(response.data)))
                .catch(e => Rx.Observable.of(loadActionError("signalement.draf.error", e)));
        });

export const createTaskEpic = (action$) =>
    action$.ofType(actions.SIGNALEMENT_TASK_CREATE)
        .switchMap((action) => {
            window.signalement.debug("sig epics draft");
            const url = backendURLPrefix + "/task/start";
            const task = action.task;
            const params = {
                timeout: 30000,
                headers: {'Accept': 'application/json', 'Content-Type': 'application/json'}
            };

            return Rx.Observable.defer(() => axios.post(url, task, params))
                .catch(e => {
                    return Rx.Observable.throw(e);
                })
                .switchMap((response) => Rx.Observable.from([
                    success({
                        title: "signalement.task.success.title",
                        message: "signalement.task.success.message",
                        uid: "signalement.task.success.",
                        position: "tr",
                        autoDismiss: 5
                    }),
                    taskCreated(response.data)
                ]))
                .catch(() => Rx.Observable.from([
                    error({
                        title: "signalement.task.fail.title",
                        message: "signalement.task.fail.message",
                        uid: "signalement.task.fail.",
                        position: "tr",
                        autoDismiss: 5
                    }),
                    setTaskCreationFail()
                ]));
        });

export const cancelDraftEpic = (action$) =>
    action$.ofType(actions.SIGNALEMENT_DRAFT_CANCEL)
        .switchMap((action) => {
            window.signalement.debug("sig epics draft");
            const url = backendURLPrefix + "/task/cancel/" + action.uuid;

            return Rx.Observable.defer(() => axios.delete(url))
                .switchMap((response) => Rx.Observable.of(draftCanceled()))
                .catch(e => Rx.Observable.of(loadActionError("signalement.generic.error", e)));
        });

export const initDrawingSupportEpic = action$ =>
    action$.ofType(actions.SIGNALEMENT_INIT_SUPPORT_DRAWING)
        .switchMap(() => {
            return Rx.Observable.of(changeMapInfoState(false));
        });

export const startDrawingEpic = action$ =>
    action$.ofType(actions.SIGNALEMENT_START_DRAWING)
        .switchMap((action) => {
            const existingLocalisation = action.localisation && action.localisation.length > 0;
            let coordinates = Array(0);
            if (existingLocalisation) {
                switch (action.geometryType) {
                    case GeometryType.POLYGON:
                        coordinates = [action.localisation.map(localisationCoords => [parseFloat(localisationCoords.x), parseFloat(localisationCoords.y)])];
                        break;
                    case GeometryType.LINE:
                        coordinates = action.localisation.map(localisationCoords => [parseFloat(localisationCoords.x), parseFloat(localisationCoords.y)]);
                        break;
                    case GeometryType.POINT:
                        coordinates = [parseFloat(action.localisation[0].x), parseFloat(action.localisation[0].y)];
                }
            }

            const feature = {
                geometry: {
                    type: action.geometryType,
                    coordinates: coordinates
                },
                newFeature: !existingLocalisation,
                type: "Feature",
            };

            const drawOptions = {
                drawEnabled: true,
                editEnabled: true,
                featureProjection: FeatureProjection,
                selectEnabled: false,
                stopAfterDrawing: true,
                transformToFeatureCollection: false,
                translateEnabled: false,
                useSelectedStyle: false
            };
            return Rx.Observable.from([
                changeDrawingStatus("drawOrEdit", action.geometryType, "signalement", [feature], drawOptions),
                setDrawing(true)
            ]);
        });

export const geometryChangeEpic = action$ =>
    action$.ofType(GEOMETRY_CHANGED)
        .filter(action => action.owner === 'signalement')
        .switchMap( (action) => {
            let localisation = [];
            if (action.features && action.features.length > 0) {
                const geometryType = action.features[0].geometry.type;
                const coordinates = action.features[0].geometry.coordinates;
                switch (geometryType) {
                    case GeometryType.POINT:
                        localisation = [{
                            x: coordinates[0].toString(),
                            y: coordinates[1].toString()
                        }];
                        break;
                    case GeometryType.LINE:
                        localisation = coordinates.map(coordinate => ({
                            x: coordinate[0].toString(),
                            y: coordinate[1].toString()
                        }));
                        break;
                    case GeometryType.POLYGON:
                        localisation = coordinates[0].map(coordinate => ({
                            x: coordinate[0].toString(),
                            y: coordinate[1].toString()
                        }));
                        break;
                    default:
                        localisation = [];
                }
            }
            return Rx.Observable.of(updateLocalisation(localisation));
        });

export const endDrawingEpic = action$ =>
    action$.ofType(END_DRAWING)
        .filter(action => action.owner === 'signalement')
        .switchMap(() => {
            return Rx.Observable.of(setDrawing(false));
        });

export const clearDrawnEpic = action$ =>
    action$.ofType(actions.SIGNALEMENT_CLEAR_DRAWN)
        .switchMap(() => {
            return Rx.Observable.from([
                changeDrawingStatus("clean", null, "signalement", [], {}),
                updateLocalisation([]),
                setDrawing(false)
            ]);
        });

export const stopDrawingEpic = (action$, store) =>
    action$.ofType(actions.SIGNALEMENT_STOP_DRAWING)
        .switchMap((action) => {
            const state = store.getState();
            const drawOptions = {
                drawEnabled: false,
                editEnabled: false,
                featureProjection: FeatureProjection,
                selectEnabled: false,
                drawing: false,
                stopAfterDrawing: true,
                transformToFeatureCollection: false,
                translateEnabled: false
            };
            //let actualFeatures = changedGeometriesSelector(state);
            //work around to avoid import of draw.js - see issues with geosolutions
            let actualFeatures = state && state.draw && state.draw.tempFeatures; 
            if (!actualFeatures || actualFeatures.length === 0) {
                actualFeatures = [
                    {
                        geometry: {
                            type: action.geometryType,
                            coordinates: Array(0)
                        },
                        type: "Feature"
                    }
                ];
            }

            return Rx.Observable.from([
                changeDrawingStatus("drawOrEdit", action.geometryType, "signalement", actualFeatures, drawOptions),
                setDrawing(false)
            ]);
        });

/**
 * Appelée pendant la fermeture de la fenetre, on réactive l'IDENTIFIER
 * @param action$
 * @returns {*}
 */
export const stopDrawingSupportEpic = action$ =>
    action$.ofType(actions.SIGNALEMENT_STOP_SUPPORT_DRAWING)
        .switchMap(() => {
            return Rx.Observable.from([
                changeMapInfoState(true),
                changeDrawingStatus("clean", null, "signalement", [], {})
            ]);
        });
