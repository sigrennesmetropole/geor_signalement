import React from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {get} from 'lodash';
import {actions} from '../actions/signalement';

const {createSelector} = require('reselect');
const {mapSelector} = require('../selectors/map');
const {layersSelector} = require('../selectors/layers');
const {mapTypeSelector} = require('../selectors/maptype');
const {toggleControl} = require('../actions/controls');
const {Glyphicon} = require('react-bootstrap');
const assign = require('object-assign');
const Message = require('./locale/Message');


const signalementSelector = createSelector([
    mapSelector,
    mapTypeSelector,
    layersSelector,
    (state) => state.controls && state.controls.toolbar && state.controls.toolbar.active === "signalement" || state.controls.signalement && state.controls.signalement.enabled,
    (state) => state.browser,
    (state) => state.signalement
], (map, mapType, layers, active, browser, signalement) => ({
    map,
    mapType,
    layers,
    active,
    browser,
    signalement
}));

const SignalementSubmitPanel = connect(signalementSelector, {
    toggleControl: toggleControl.bind(null, 'signalement', null)
})(require("../components/signalement/SignalementSubmitPanel"));


const SignalementPlugin = connect((state) => ({
}), {
});


module.exports = {
    SignalementPlugin: assign(SignalementPlugin, {
        BurgerMenu: {
            name: 'signalement',
            position: 3,
            panel: SignalementSubmitPanel,
            text: <Message msgId="snapshot.title"/>,
            icon: <Glyphicon glyph="camera"/>,
            action: toggleControl.bind(null, 'signalement', null),
            tools: [SnapshotPlugin],
            priority: 2
        }
    }),
    reducers: {
    }
};

/*class SignalementComponent extends React.Component {
    render() {
        const style = {position: "absolute", top: "100px", left: "100px", zIndex: 10000000};
        return <div style={style}>Sample</div>;
    }
}*/

/*module.exports = {
    SignalementPlugin: assign(SignalementComponent, {
        Toolbar: {
            name: 'signalement',
            position: 8,
            help: "Signalement",
            tooltip: "snapshot.tooltip",
            icon: <Glyphicon glyph="camera"/>,
            wrap: true,
            title: "snapshot.title",
            exclusive: true,
            priority: 1
        },
        BurgerMenu: {
            name: 'signalement',
            position: 3,
            text: "Signalement",
            icon: <Glyphicon glyph="camera"/>,
            priority: 2
        }
    }),
    reducers: {
    }
};*/




