import * as Rx from 'rxjs';
import axios from 'axios';
import {actions, loadedContexts, gotMe, loadInitError, loadActionError} from '../actions/signalement-management-action';

export const loadContextsEpic = (action$) =>
    action$.ofType(actions.CONTEXTS_LOAD)
        .switchMap((action) => {
            console.log("sigm epics load contexts");
            if (action.contexts) {
                return Rx.Observable.of(loadedContexts(action.contexts)).delay(0);
            }
            const url = "http://localhost:8082/user/contexts";
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
            const url = "http://localhost:8082/user/me";
            return Rx.Observable.defer(() => axios.get(url))
                .switchMap((response) => Rx.Observable.of(gotMe(response.data)))
                .catch(e => Rx.Observable.of(loadInitError("signalement-management.init.me.error", e)));
        });  
        
