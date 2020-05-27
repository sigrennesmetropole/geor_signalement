import assign from 'object-assign';
import {find, get} from 'lodash';
import {set, arrayUpdate} from '../../../MapStore2/web/client/utils/ImmutableUtils';
import {actions} from '../actions/signalement-action';
import {SET_CONTROL_PROPERTY} from '../../../MapStore2/web/client/actions/config';

const initialState = {
    initialized: false,
    user: null,
    contextLayers: [],
    contextThemas: [],
    attachmentConfiguration: {},
}

export default (state = initialState, action) => {
    console.log("sig reduce:"+ action.type);
    switch (action.type) {
        case SET_CONTROL_PROPERTY: {
            return state;
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
        case actions.INIT_ERORR: {
            return assign({}, state, {loadingError: action.error, message: action.error.message});
        }
        default: {
            return state;
        }
    }
};