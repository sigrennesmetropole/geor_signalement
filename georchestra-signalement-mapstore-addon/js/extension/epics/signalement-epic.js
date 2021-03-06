import * as Rx from 'rxjs';
import axios from 'axios';
//import {changedGeometriesSelector} from "@mapstore/selectors/draw";
import {changeDrawingStatus, END_DRAWING, GEOMETRY_CHANGED} from "@mapstore/actions/draw";
import {changeMapInfoState} from "@mapstore/actions/mapInfo";
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
    updateLocalisation
} from '../actions/signalement-action';
import {FeatureProjection, GeometryType} from "../constants/signalement-constants";

let backendURLPrefix = "/signalement";

export const initSignalementEpic = (action$) =>
	action$.ofType(actions.INIT_SIGNALEMENT)
	    .switchMap((action) => {
	        console.log("sig epics init:"+ action.url);
	        if( action.url ) {	        	
	        	backendURLPrefix = action.url;
	        }
	        return Rx.Observable.of(initSignalementDone()).delay(0);
	    });        

export const loadAttachmentConfigurationEpic = (action$) =>
    action$.ofType(actions.ATTACHMENT_CONFIGURATION_LOAD)
        .switchMap((action) => {
            console.log("sig epics attachment config");
            if (action.attachmentConfiguration) {
                return Rx.Observable.of(loadedAttachmentConfiguration(action.attachmentConfiguration)).delay(0);
            }
            console.log("sig back " + backendURLPrefix);
            const url = backendURLPrefix + "/reporting/attachment/configuration";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedAttachmentConfiguration(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.attattachmentConfiguration.error", e)));
        });

export const addAttachmentEpic = (action$) =>
    action$.ofType(actions.ADD_ATTACHMENT)
        .switchMap((action) => {
            console.log("sig epics add attachement");
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
            console.log("sig epics remove attachement");
            const url = backendURLPrefix + "/reporting/" + action.attachment.uuid + "/delete/" + action.attachment.id;

            return Rx.Observable.defer(() => axios.delete(url))
                .switchMap((response) => Rx.Observable.of(removedAttachment(action.attachment.index)))
                .catch(e => Rx.Observable.of(loadActionError("signalement.attachment.delete.error", e)));
        });

export const loadThemasEpic = (action$, store) =>
    action$.ofType(actions.THEMAS_LOAD)
        .switchMap((action) => {
            console.log("sig epics themas");
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
            console.log("sig epics layers");
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
            console.log("sig epics me");
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
            console.log("sig epics draft");
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
            console.log("sig epics draft");
            const url = backendURLPrefix + "/task/start";
            const task = action.task;
            const params = {
                timeout: 30000,
                headers: {'Accept': 'application/json', 'Content-Type': 'application/json'}
            };

            return Rx.Observable.defer(() => axios.post(url, task, params))
                .switchMap((response) => Rx.Observable.of(taskCreated(response.data)))
                .catch(e => Rx.Observable.of(loadActionError("signalement.task.error", e)));
        });

export const cancelDraftEpic = (action$) =>
    action$.ofType(actions.SIGNALEMENT_DRAFT_CANCEL)
        .switchMap((action) => {
            console.log("sig epics draft");
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

export const stopDrawingSupportEpic = action$ =>
    action$.ofType(actions.SIGNALEMENT_STOP_SUPPORT_DRAWING)
        .switchMap(() => {
            return Rx.Observable.from([
                changeMapInfoState(true),
                changeDrawingStatus("clean", null, "signalement", [], {})
            ]);
        });
