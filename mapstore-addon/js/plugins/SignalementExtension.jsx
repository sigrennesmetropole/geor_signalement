import React from 'react';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {createPlugin} from '@mapstore/utils/PluginsUtils';
import {createControlEnabledSelector} from '@mapstore/selectors/controls';
import Message from '@mapstore/components/I18N/Message';
import {SignalementPanelComponent} from '../signalement/components/SignalementPanelComponent';
import * as epics from '../signalement/epics/signalement-epic';
import signalementReducer from '../signalement/reducers/signalement-reducer';
import {
	initSignalement,
    addAttachment,
    cancelClosing,
    cancelDraft,
    clearDrawn,
    closePanel,
    confirmClosing,
    createDraft,
    createTask,
    getMe,
    initDrawingSupport,
    loadAttachmentConfiguration,
    loadLayers,
    loadThemas,
    openPanel,
    removeAttachment,
    requestClosing,
    startDrawing,
    stopDrawing,
    stopDrawingSupport
} from '../signalement/actions/signalement-action';
import {
    isOpen,
    signalementAttachmentConfigurationSelector,
    signalementLayersSelector,
    signalementMeSelector,
    signalementThemasSelector
} from '../signalement/selectors/signalement-selector';

const isEnabled = createControlEnabledSelector('signalement');

const Connected = connect((state) => ({
    active: /*isEnabled(state) ||*/ !!isOpen(state),
    attachmentConfiguration: signalementAttachmentConfigurationSelector(state),
    contextLayers: signalementLayersSelector(state),
    contextThemas: signalementThemasSelector(state),
    user: signalementMeSelector(state),
    currentLayer: state.signalement.currentLayer,
    task: state.signalement.task,
    attachments: state.signalement.attachments,
    status: state.signalement.status,
    closing: state.signalement.closing,
    drawing: state.signalement.drawing,
    error: state.signalement.error,
    // debug
    state : state
}), {
	initSignalement: initSignalement,
    initDrawingSupport: initDrawingSupport,
    stopDrawingSupport: stopDrawingSupport,
    startDrawing: startDrawing,
    stopDrawing: stopDrawing,
    clearDrawn: clearDrawn,
    loadAttachmentConfiguration: loadAttachmentConfiguration,
    addAttachment: addAttachment,
    removeAttachment: removeAttachment,
    loadLayers: loadLayers,
    loadThemas: loadThemas,
    getMe: getMe,
    createDraft: createDraft,
    cancelDraft: cancelDraft,
    createTask: createTask,
    requestClosing: requestClosing,
    cancelClosing: cancelClosing,
    confirmClosing: confirmClosing,
    toggleControl: () => closePanel()
})(SignalementPanelComponent);

export default {
	name: "Signalement",
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
            action: () => openPanel(null)
        }
    }
};
