import assign from 'object-assign';
import {actions, status} from '../actions/signalement-action';

const initialState = {
    user: null,
    contextLayers: [],
    contextThemas: [],
    attachments: [],
    attachmentConfiguration: {},
    status: status.NO_TASK
}

export default (state = initialState, action) => {
    window.signalement.debug("sig reduce:" + action.type);
    switch (action.type) {
        case actions.INIT_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.ACTION_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.SIGNALEMENT_OPEN_PANEL: {
            return assign({}, state, {currentLayer: action.currentLayer, open: true, attachments: []});
        }
        case actions.SIGNALEMENT_CLOSE_PANEL: {
            return assign({}, state, {currentLayer: null, open: false});
        }
        case actions.ATTACHMENT_CONFIGURATION_LOADED: {
            // return set('attachmentConfiguration', action.attachmentConfiguration, state);
            return assign({}, state, {attachmentConfiguration: action.attachmentConfiguration});
        }
        case actions.ATTACHMENT_ADDED: {
            let attachments = [...state.attachments];
            attachments.push(action.attachment);
            return assign({}, state, {attachments: attachments });
        }
        case actions.ATTACHMENT_REMOVED: {
            let attachments = [...state.attachments];
            attachments.splice(action.attachmentIndex, 1)
            return assign({}, state, {attachments: attachments });
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
            return assign({}, state, {task: null, status: status.TASK_UNLOADED, open: false});
        }
        case actions.SIGNALEMENT_TASK_CREATE: {
            return assign({}, state, {task: action.task, status: status.CREATE_TASK, creating: true });
        }
        case actions.SIGNALEMENT_TASK_CREATED: {
            return assign({}, state, {task: null, status: status.TASK_CREATED, open: false, creating: false});
        }
        case actions.SIGNALEMENT_TASK_NOT_CREATED: {
            return assign({}, state, {creating: false});
        }
        case actions.SIGNALEMENT_UPDATE_LOCALISATION: {
            return {
                ...state,
                task: {
                    ...state.task,
                    asset: {
                        ...state.task.asset,
                        localisation: action.localisation
                    }
                }
            };
        }
        case actions.SIGNALEMENT_SET_DRAWING: {
            return assign({}, state, {drawing: action.drawing});
        }
        default: {
            return state;
        }
    }
};
