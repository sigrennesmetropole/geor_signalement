import * as Rx from 'rxjs';
import axios from 'axios';
import assign from 'object-assign';
//const axios = require('../../../MapStore2/web/client/libs/ajax');
import {actions, loadedAttachmentConfiguration, loadedLayers, loadedThemas, gotMe, loadError} from '../actions/signalement-action';

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
                .catch(e => Rx.Observable.of(loadError("Failed to load attachment configuration", e)));
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
                .catch(e => Rx.Observable.of(loadError("Failed to load themas", e)));
        }); 
        
export const loadLayersEpic = (action$, store) =>
    action$.ofType(actions.LAYERS_LOAD)
        .switchMap((action) => {
            console.log("sig epics layers");
            if (action.layers) {
                return Rx.Observable.of(loadedLayers(action.layers)).delay(0);
            }
            const url = "http://localhost:8082/reporting/contextDescription/search?contextType=LAYER";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(loadedLayers(response.data)))
                .catch(e => Rx.Observable.of(loadError("Failed to load layers", e)));
        });  
        
export const loadMeEpic = (action$, store) =>
    action$.ofType(actions.USER_ME_GET)
        .switchMap((action) => {
            console.log("sig epics me");
            if (action.user) {
                return Rx.Observable.of(gotMe(action.user)).delay(0);
            }
            const url = "http://localhost:8082/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadError("Failed to load me", e)));
        });          