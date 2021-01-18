import React from 'react';
import {createPlugin} from '@mapstore/utils/PluginsUtils';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {get} from 'lodash';
import Message from '@mapstore/components/I18N/Message';
import {SignalementManagementPanelComponent} from '../components/SignalementManagementPanelComponent';
import * as epics from '../epics/signalement-management-epic';
import signalementManagementReducer from '../reducers/signalement-management-reducer';
import {
    initSignalementManagement, 
    loadContexts, 
    getMe, 
    openTabularView, 
    closeTabularView, 
    changeTypeView,
    getTask,
    downloadAttachment,
    claimTask,
    updateTask,
    updateDoAction,
} from '../actions/signalement-management-action';
import {closeIdentify} from '@mapstore/actions/mapInfo';
import {
    getSignalementManagement, 
    signalementManagementContextsSelector, 
    signalementManagementMeSelector,
    signalementManagementTaskSelector
} from '../selectors/signalement-management-selector';
import {selectNode} from '@mapstore/actions/layers';
import '../assets/signalement-management.css';
import { name } from '../../../config';
import { setViewer } from '@mapstore/utils/MapInfoUtils';
import {SignalementTaskViewer} from '../components/SignalementTaskViewer';

const SignalementManagementPanelComponentConnected = connect((state) => ({
    contexts: signalementManagementContextsSelector(state),
    user: signalementManagementMeSelector(state),
    status: state.signalementManagement.status,
    error: state.signalementManagement.error,
    tabularViewOpen: state.signalementManagement.tabularViewOpen,
    viewType: state.signalementManagement.viewType,
    // debug
    state : state
}), {
    initSignalementManagement: initSignalementManagement,
    loadContexts: loadContexts,
    getMe: getMe,
    openTabularView: openTabularView,
    closeTabularView: closeTabularView,
    changeTypeView: changeTypeView,
    selectNode: selectNode
})(SignalementManagementPanelComponent);

/*const SignalementTaskViewerConnected = connect((state) => ({
    task: signalementManagementTaskSelector(state),
    user: signalementManagementMeSelector(state),
    viewType: state.signalementManagement.viewType,
    errorTask: state.signalementManagement.errorTask,
    // debug
    state : state
}), {
    getTask: getTask,
    downloadAttachment: downloadAttachment,
    claimTask: claimTask,
    updateTask: updateTask,
    updateDoAction: updateDoAction,
    closeIdentify: closeIdentify
})(SignalementTaskViewer);
setViewer("TaskViewer", SignalementTaskViewerConnected);*/

export default {
    name,
    component: SignalementManagementPanelComponentConnected,
    epics,
    reducers: {
        signalementManagement: signalementManagementReducer
    },
    containers: {
    }
};
