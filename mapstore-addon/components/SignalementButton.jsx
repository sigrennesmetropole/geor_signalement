const PropTypes = require('prop-types');
const React = require('react');
const {Button, Glyphicon, Tooltip} = require('react-bootstrap');
const OverlayTrigger = require('../../../web/client/components/misc/OverlayTrigger');

class SignalementButton extends React.Component {
    static propTypes = {
        id: PropTypes.string,
        style: PropTypes.object,
        glyphicon: PropTypes.string,
        text: PropTypes.string,
        btnSize: PropTypes.oneOf(['large', 'small', 'xsmall']),
        className: PropTypes.string,
        help: PropTypes.oneOfType([PropTypes.string, PropTypes.element]),
        onClick: PropTypes.func,
        tooltip: PropTypes.element,
        tooltipPlace: PropTypes.string,
        bsStyle: PropTypes.string
    };

    static defaultProps = {
        id: "mapstore-signalement",
        className: "square-button",
        glyphicon: "warning-sign",
        btnSize: 'xsmall',
        tooltipPlace: "left",
        step: 1,
        onClick: () => {},
        bsStyle: "default",
        style: {}
    };

    render() {
        return this.addTooltip(
            <Button
                id={this.props.id}
                style={this.props.style}
                onClick={() => this.props.onClick()}
                className={this.props.className}
                bsStyle={this.props.bsStyle}
            >
                {this.props.glyphicon ? <Glyphicon glyph={this.props.glyphicon}/> : null}
                {this.props.glyphicon && this.props.text ? "\u00A0" : null}
                {this.props.text}
                {this.props.help}
            </Button>
        );
    }

    addTooltip = (btn) => {
        if (!this.props.tooltip) {
            return btn;
        }
        let tooltip = <Tooltip id="locate-tooltip">{this.props.tooltip}</Tooltip>;
        return (
            <OverlayTrigger placement={this.props.tooltipPlace} key={"overlay-trigger." + this.props.id} overlay={tooltip}>
                {btn}
            </OverlayTrigger>
        );
    };
};


module.exports = SignalementButton;
