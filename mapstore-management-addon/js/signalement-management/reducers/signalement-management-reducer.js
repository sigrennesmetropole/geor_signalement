import assign from 'object-assign';
import {actions, status, viewType} from '../actions/signalement-management-action';

const initialState = {
    user: null,
    context: [],
    status: status.NOOP,
    tabularViewOpen: false
}

export default (state = initialState, action) => {
    console.log("sigm reduce:" + action.type);
    switch (action.type) {
        case actions.INIT_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.ACTION_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.CONTEXTS_LOADED: {
            return assign({}, state, {contexts: action.contexts});
        }
        case actions.USER_ME_GOT: {
            return assign({}, state, {user: action.user});
        }
        case actions.OPEN_TABULAR_VIEW: {
            return assign({}, state, {tabularViewOpen: true});
        }
        case actions.CLOSE_TABULAR_VIEW: {
            return assign({}, state, {tabularViewOpen: false});
        }
        case actions.CHANGE_TYPE_VIEW: {
            return assign({}, state, {viewType: action.viewType});
        }
        default: {
            return state;
        }
    }
};