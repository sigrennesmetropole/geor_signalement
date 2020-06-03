import * as Rx from 'rxjs';
import axios from 'axios';
//const axios = require('../../../MapStore2/web/client/libs/ajax');
import {actions, loadedAttachmentConfiguration, loadedLayers, loadedThemas, gotMe, draftCreated, draftCanceled, taskCreated, loadInitError, loadActionError} from '../actions/signalement-action';

export const loadAttachmentConfigurationEpic = (action$) =>
    action$.ofType(actions.ATTACHMENT_CONFIGURATION_LOAD)
        .switchMap((action) => {
            console.log("sig epics attachment config");
            if (action.attachmentConfiguration) {
                return Rx.Observable.of(loadedAttachmentConfiguration(action.attachmentConfiguration)).delay(0);
            }
            const url = "http://localhost:8082/reporting/attachment/configuration";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedAttachmentConfiguration(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.attattachmentConfiguration.error", e)));
        });

export const loadThemasEpic = (action$, store) =>
    action$.ofType(actions.THEMAS_LOAD)
        .switchMap((action) => {
            console.log("sig epics themas");
            if (action.themas) {
                return Rx.Observable.of(loadedThemas(action.themas)).delay(0);
            }
            const url = "http://localhost:8082/reporting/contextDescription/search?contextType=THEMA";
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
            const url = "http://localhost:8082/reporting/contextDescription/search?contextType=LAYER";
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
            const url = "http://localhost:8082/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement.init.me.error", e)));
        });  
        
export const createDraftEpic = (action$) =>
    action$.ofType(actions.SIGNALEMENT_DRAFT_CREATE)
        .switchMap((action) => {
            console.log("sig epics draft");
            const url = "http://localhost:8082/task/draft";
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
            const url = "http://localhost:8082/task/start";
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
            const url = "http://localhost:8082/task/cancel/" + action.uuid;
            
            return Rx.Observable.defer(() => axios.delete(url))
                .switchMap((response) => Rx.Observable.of(draftCanceled()))
                .catch(e => Rx.Observable.of(loadActionError("signalement.generic.error", e)));
        });          