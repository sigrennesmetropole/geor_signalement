import React from 'react';
import {PropTypes} from 'prop-types';
import {Button, Glyphicon, Tooltip} from 'react-bootstrap';
import Message from '@mapstore/components/I18N/Message';
import OverlayTrigger from '@mapstore/components/misc/OverlayTrigger';

export class SignalementLayerToolButton extends React.Component {

    static propTypes = {
        contextLayers : PropTypes.array,
        selectedLayer: PropTypes.object,
        isOpen: PropTypes.bool,
        onClick: PropTypes.func,
    };

    static defaultProps = {
        contextLayers: null,
        selectedLayer: null,
        isOpen: false,
        onClick: () =>{},
    };

    constructor(props) {
        super(props);
        window.signalement.debug("sig create layer tool button");
        window.signalement.debug(this.props);
    }

    render() {
        window.signalement.debug("sig render layer tool button");
        window.signalement.debug(this.props);
        window.signalement.debug(this.state);

        const signalableLayer = this.props.selectedLayers != null && 
            this.props.selectedLayers.length == 1 &&
            this.props.contextLayers && this.props.contextLayers.length > 0;
        let selectableLayers = signalableLayer ? 
            this.props.contextLayers.filter( layer => layer.name === this.props.selectedLayers[0].name): [];

        if( signalableLayer && selectableLayers.length > 0 ) {
            return (
                <OverlayTrigger
                            key="signalement"
                            placement="top"
                            overlay={<Tooltip id="legend-tooltip-signalement"><Message msgId="signalement.reporting.layer"/></Tooltip>}>            
                    <Button key="signalement-layer" bsStyle={this.props.isOpen ? 'success' : 'primary'} className="square-button-md" onClick={() => this.props.onClick(selectableLayers[0])}>
                        <Glyphicon glyph="exclamation-sign" />
                    </Button>
                </OverlayTrigger>
            );
        } else {
            return null;
        }
    }
};
