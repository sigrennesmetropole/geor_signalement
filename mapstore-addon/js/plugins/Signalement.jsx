import React from 'react';
import {createPlugin} from '../../MapStore2/web/client/utils/PluginsUtils';
import {Glyphicon} from 'react-bootstrap';
import {connect} from 'react-redux';
import {get} from 'lodash';
import {createControlEnabledSelector} from '../../MapStore2/web/client/selectors/controls';
import Message from '../../MapStore2/web/client/components/I18N/Message';
import { setControlProperty } from '../../MapStore2/web/client/actions/controls';
import {SignalementPanelComponent} from '../signalement/component/SignalementPanelComponent';
import * as epics from '../signalement/epics/signalement-epic';
import signalementReducer from '../signalement/reducers/signalement-reducer';
import {actions, loadAttachmentConfiguration, loadLayers, loadThemas, getMe} from '../signalement/actions/signalement-action';

const isEnabled = createControlEnabledSelector('signalement');

const Connected = connect((state) => ({
    active: isEnabled(state) ? true : false,
    state: state
}), {
    loadAttachmentConfiguration: loadAttachmentConfiguration,
    loadLayers: loadLayers,
    loadThemas: loadThemas,
    getMe: getMe
})(SignalementPanelComponent);

export default createPlugin("Signalement", {
    component: Connected,
    epics,
    reducers: {
        signalementReducer
    },
    containers: {
        BurgerMenu: {
            name: 'signalement',
            position: 9,
            panel: false,
            tooltip: "signalement.reporting.thema",
            text: <Message msgId="signalement.msgBox.title" />,
            icon: <Glyphicon glyph="exclamation-sign" />,
            action: () => setControlProperty("signalement", "enabled", true)
        }
    }
});