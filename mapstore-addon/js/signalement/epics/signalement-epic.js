import * as Rx from 'rxjs';
import axios from 'axios';
import {actions, loadedAttachmentConfiguration, addedAttachment, removedAttachment, loadedLayers, loadedThemas, gotMe, draftCreated,
    draftCanceled, taskCreated, loadInitError, loadActionError} from '../actions/signalement-action';
    
let backendURLPrefix = "http://localhost:8082";

/*export function configureBackendUrl(value){
    console.log("sig configure backend url:" + value);
    //backendURLPrefix = value;
};*/

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
