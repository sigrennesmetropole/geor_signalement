import React from 'react';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {createControlEnabledSelector} from '@mapstore/selectors/controls';
import Message from '@mapstore/components/I18N/Message';
import { name } from '../../../config';
import {SignalementPanelComponent} from '../components/SignalementPanelComponent';
import {SignalementLayerToolButton} from '../components/SignalementLayerToolButton';
import * as epics from '../epics/signalement-epic';
import signalementReducer from '../reducers/signalement-reducer';
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
} from '../actions/signalement-action';
import {
    isOpen,
    signalementAttachmentConfigurationSelector,
    signalementLayersSelector,
    signalementMeSelector,
    signalementThemasSelector
} from '../selectors/signalement-selector';
import '../assets/signalement.css';

const isEnabled = createControlEnabledSelector('signalement');

const SignalementPanelComponentConnected = connect((state) => ({
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

const SignalementLayerToolButtonConnected = connect((state) => ({
    contextLayers: signalementLayersSelector(state),
    isOpen: isOpen(state),
    state: state
    }), {
    onClick: openPanel
})(SignalementLayerToolButton);

export default {
	name,
    component: SignalementPanelComponentConnected,
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
        },
        TOC: {
            name: "signalement",
            target: "toolbar",
            //In case of target: toolbar, selector determine to show or not show the tool (returning true or false).
            // As argument of this function you have several information, that will be passed also to the component.
            // - status: that can be LAYER, LAYERS, GROUP or GROUPS, depending if only one or more than one layer is selected.
            // - selectedGroups: current list of selected groups
            // - selectedLayers: current list of selected layers
            selector: ({ status }) => status === 'LAYER',
            // The component to render. It receives as props the same object passed to the selector function.
            Component: SignalementLayerToolButtonConnected
        },
    }
};
