import React from 'react';
import {createPlugin} from '../../MapStore2/web/client/utils/PluginsUtils';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {get} from 'lodash';
import {createControlEnabledSelector} from '../../MapStore2/web/client/selectors/controls';
import Message from '../../MapStore2/web/client/components/I18N/Message';
import { setControlProperty } from '../../MapStore2/web/client/actions/controls';
import {SignalementPanelComponent} from '../signalement/components/SignalementPanelComponent';
import * as epics from '../signalement/epics/signalement-epic';
import signalementReducer from '../signalement/reducers/signalement-reducer';
import {loadAttachmentConfiguration, loadLayers, loadThemas, getMe, createDraft, cancelDraft, 
    createTask, requestClosing, cancelClosing, confirmClosing, openPanel, closePanel } from '../signalement/actions/signalement-action';
import {isOpen, signalementLayersSelector, signalementThemasSelector, signalementMeSelector,
    signalementAttachmentConfigurationSelector} from '../signalement/selectors/signalement-selector';

const isEnabled = createControlEnabledSelector('signalement');

const Connected = connect((state) => ({
    active: /*isEnabled(state) ||*/ isOpen(state) ? true : false,
    attachmentConfiguration: signalementAttachmentConfigurationSelector(state),
    contextLayers: signalementLayersSelector(state),
    contextThemas: signalementThemasSelector(state),
    user: signalementMeSelector(state),
    currentLayer: state.signalement.currentLayer,
    task: state.signalement.task,
    status: state.signalement.status,
    closing: state.signalement.closing,
    error: state.signalement.error,
    // debug
    state : state
}), {
    loadAttachmentConfiguration: loadAttachmentConfiguration,
    loadLayers: loadLayers,
    loadThemas: loadThemas,
    getMe: getMe,
    createDraft: createDraft,
    cancelDraft: cancelDraft,
    createTask: createTask,
    requestClosing: requestClosing,
    cancelClosing: cancelClosing,
    confirmClosing: confirmClosing,
    toggleControl: () => /*setControlProperty("signalement", "enabled", false)*/closePanel()
})(SignalementPanelComponent);

export default createPlugin("Signalement", {
    component: Connected,
    epics,
    reducers: {
        signalement: signalementReducer
    },
    containers: {
        BurgerMenu: {
            name: 'signalement',
            position: 9,
            panel: false,
            tooltip: "signalement.reporting.thema",
            text: <Message msgId="signalement.msgBox.title" />,
            icon: <Glyphicon glyph="exclamation-sign" />,
            action: () => /*setControlProperty("signalement", "enabled", true)*/openPanel(null)
        }
    }
});