import React from 'react';
import {connect} from 'react-redux';
import PropTypes from 'prop-types';
import {get} from 'lodash';

class SignalementComponent extends React.Component {
    render() {
        const style = {position: "absolute", top: "100px", left: "100px", zIndex: 10000000};
        return <div style={style}>Sample</div>;
    }
}

export const SignalementPlugin = SignalementComponent;