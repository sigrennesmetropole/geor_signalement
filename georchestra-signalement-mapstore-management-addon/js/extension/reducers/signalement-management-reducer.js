import assign from 'object-assign';
import {actions, status, viewType} from '../actions/signalement-management-action';

const {
    CLOSE_FEATURE_GRID
} = require('@mapstore/actions/featuregrid');

const initialState = {
    user: null,
    context: [],
    status: status.NOOP,
    tabularViewOpen: false,
    taskViewerOpen: false
}

export default (state = initialState, action) => {
    window.signalementMgmt.debug("sigm reduce:" + action.type);
    switch (action.type) {
        case actions.INIT_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.ACTION_ERROR: {
            return assign({}, state, {error: action.error});
        }
        case actions.ACTION_TASK_ERROR: {
            return assign({}, state, {errorTask: action.error});
        }
        case actions.CONTEXTS_LOADED: {
            return assign({}, state, {contexts: action.contexts});
        }
        case actions.USER_ME_GOT: {
            return assign({}, state, {user: action.user});
        }
        case actions.CLAIM_TASK:
        case actions.UPDATE_TASK:
        case actions.UPDATE_DO_ACTION: {
            return assign({}, state, {task: action.task, actionInProgress: true});
        }
        case actions.TASK_GET: {
            return assign({}, state, {task: null});
        }
        case actions.TASK_GOT: {
            return assign({}, state, {task: action.task, actionInProgress: false});
        }
        case actions.OPEN_TABULAR_VIEW: {
            return assign({}, state, {tabularViewOpen: true});
        }
        case actions.CLOSE_TABULAR_VIEW: {
            return assign({}, state, {tabularViewOpen: false});
        }
        case CLOSE_FEATURE_GRID: {
            return assign({}, state, {tabularViewOpen: false});
        }
        case actions.CHANGE_TYPE_VIEW: {
            return assign({}, state, {viewType: action.viewType});
        }
        case actions.OPEN_TASK_VIEWER: {
            return assign({}, state, {taskViewerOpen: true, features: action.features, clickedPoint: action.clickedPoint, actionInProgress: false})
        }
        case actions.CLOSE_TASK_VIEWER: {
            return assign({}, state, {taskViewerOpen: false, features: [], clickedPoint: {long: 0.0, lat: 0.0}, actionInProgress: false})
        }
        default: {
            return state;
        }
    }
};
