import {actions} from '../actions/signalement';
import assign from 'object-assign';

const initialState = {
		attachmentConfiguration: null,
		themas: null,
		layers: null
};

export function signalementReducer(state = initialState, action) {
    switch(action.type) {
        case actions.ATTACHMENT_CONFIGURATION_LOAD:
            return Object.assign({}, state, {attachmentConfiguration: action.payload.attachmentConfiguration});
        case actions.THEMAS_LOAD:
            saveToLocalStorage([]);
            return Object.assign({}, state, {themas: action.payload.themas});
        case actions.LAYERS_LOAD:
        	return Object.assign({}, state, {layers: action.payload.layers});
        default:
            return state;
    }
}