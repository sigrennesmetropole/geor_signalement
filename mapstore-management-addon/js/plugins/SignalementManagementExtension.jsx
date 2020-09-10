import React from 'react';
import {createPlugin} from '@mapstore/utils/PluginsUtils';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {get} from 'lodash';
import Message from '@mapstore/components/I18N/Message';
import {SignalementManagementPanelComponent} from '../signalement-management/components/SignalementManagementPanelComponent';
import * as epics from '../signalement-management/epics/signalement-management-epic';
import signalementManagementReducer from '../signalement-management/reducers/signalement-management-reducer';
import {initSignalementManagement, loadContexts, getMe, openTabularView, closeTabularView, changeTypeView} from '../signalement-management/actions/signalement-management-action';
import {getSignalementManagement, signalementManagementContextsSelector, signalementManagementMeSelector} from '../signalement-management/selectors/signalement-management-selector';
import {selectNode} from '@mapstore/actions/layers';

const Connected = connect((state) => ({
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

export default {
    component: Connected,
    epics,
    reducers: {
        signalementManagement: signalementManagementReducer
    }
};
