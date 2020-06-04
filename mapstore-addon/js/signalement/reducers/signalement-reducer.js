import assign from 'object-assign';
import {find, get} from 'lodash';
import {set, arrayUpdate} from '../../../MapStore2/web/client/utils/ImmutableUtils';
import {actions, status} from '../actions/signalement-action';

const initialState = {
    user: null,
    contextLayers: [],
    contextThemas: [],
    attachmentConfiguration: {},
    status: status.NO_TASK
}

export default (state = initialState, action) => {
    console.log("sig reduce:" + action.type);
    switch (action.type) {
        case actions.INIT_ERORR: {
            return assign({}, state, {error: action.error});
        }
        case actions.ACTION_ERORR: {
            return assign({}, state, {error: action.error});
        }                
        case actions.ATTACHMENT_CONFIGURATION_LOADED: {
           // return set('attachmentConfiguration', action.attachmentConfiguration, state);
           return assign({}, state, {attachmentConfiguration: action.attachmentConfiguration});
        }
        case actions.LAYERS_LOADED: {
            return assign({}, state, {contextLayers: action.layers});
        }
        case actions.THEMAS_LOADED: {
            return assign({}, state, {contextThemas: action.themas});
        }
        case actions.USER_ME_GOT: {
            return assign({}, state, {user: action.user});
        }
        case actions.SIGNALEMENT_DRAFT_CREATE: {
            return assign({}, state, {task: null, status: status.LOAD_TASK, loadingDraft: true});
        }
        case actions.SIGNALEMENT_DRAFT_CREATED: {
            return assign({}, state, {task: action.task, status: status.TASK_INITIALIZED, loadingDraft: false});
        }
        case actions.SIGNALEMENT_CLOSING: {
            return assign({}, state, {closing: true});
        }
        case actions.SIGNALEMENT_CANCEL_CLOSING: {
            return assign({}, state, {closing: false});
        }
        case actions.SIGNALEMENT_CONFIRM_CLOSING: {
            return assign({}, state, {closing: false, status: status.REQUEST_UNLOAD_TASK});
        }
        case actions.SIGNALEMENT_DRAFT_CANCEL: {
            return assign({}, state, {status: status.UNLOAD_TASK});
        }        
        case actions.SIGNALEMENT_DRAFT_CANCELED: {
            return assign({}, state, {task: null, status: status.TASK_UNLOADED});
        }
        case actions.SIGNALEMENT_TASK_CREATE: {
            return assign({}, state, {task: action.task, status: status.CREATE_TASK });
        }
        case actions.SIGNALEMENT_TASK_CREATED: {
            return assign({}, state, {task: null, status: status.TASK_CREATED});
        }
        default: {
            return state;
        }
    }
};