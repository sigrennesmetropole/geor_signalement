import React from 'react';
import {Button, Col, Grid, Row, Image, Glyphicon, Table, Panel, Alert} from 'react-bootstrap';
import {DateFormat} from '../I18N/I18N';
import {Message} from '../I18N/Message';
import {ConfigUtils} from '../../utils/ConfigUtils';
import {Dialog} from '../misc/Dialog';

require("./signalement-panel.css");

/**
 * SignalementSubmitPanel allow submit a signalement on a layer or on themas
 */
class SignalmentSubmitPanel extends React.Component {
    static propTypes = {
        id: PropTypes.string,
        name: PropTypes.oneOfType([PropTypes.string, PropTypes.element]),
        map: ConfigUtils.PropTypes.config,
        dateFormat: PropTypes.object,
		saveButtonText: PropTypes.string,
		cancelButtonText: PropTypes.string,
        wrap: PropTypes.bool,
        wrapWithPanel: PropTypes.bool,
        panelStyle: PropTypes.object,
        panelClassName: PropTypes.string,
        closeGlyph: PropTypes.string,
        buttonStyle: PropTypes.string,
		toggleControl: PropTypes.func
    };

    static defaultProps = {
        id: "signalement_submit_panel",
        dateFormat: {day: "numeric", month: "long", year: "numeric"},
        wrap: false,
        wrapWithPanel: false,
        panelStyle: {
            minWidth: "720px",
            zIndex: 100,
            position: "absolute",
            overflow: "auto",
            top: "60px",
            right: "100px"
        },
        panelClassName: "signalement-submit-panel",
        closeGlyph: "1-close",
        buttonStyle: "primary",
        bounds: '#container',
		toggleControl: () => {}
    };

    onClickCreate = () => {
        
    };

	onClickCancel = () => {
        
    };


    wrap = (panel) => {
        if (this.props.wrap) {
            if (this.props.wrapWithPanel) {
                return (<Panel id={this.props.id} header={<span><span className="signalement-panel-title">
						<Message msgId="signalement.title"/></span><span className="settings-panel-close panel-close" onClick={this.props.toggleControl}></span></span>} style={this.props.panelStyle} className={this.props.panelClassName}>
                    {panel}
                </Panel>);
            }
            return (
                <Portal>
                    <Dialog id="mapstore-signalement-panel" style={this.props.panelStyle} bounds={this.props.bounds}>
                        <span role="header">
								<span className="signalement-panel-title"><Message msgId="signalement.title"/></span>
								<button onClick={this.props.toggleControl} className="print-panel-close close">{this.props.closeGlyph ? <Glyphicon glyph={this.props.closeGlyph}/> : <span>×</span>}</button></span>
                        {panel}
                    </Dialog>
                </Portal>
            );
        }
        return panel;
    };

    render() {
        let signalementReady = this.isSignalementReady();
        return this.props.active ? this.wrap(
            <Grid role="body" className="signalement-panel" fluid>
                <Row key="main">
                    <Col key="dataCol" xs={5} sm={5} md={5}>
                        La fenêtre
                    </Col>
                </Row>

                <Row key="buttons" htopclassName="pull-right" style={{marginTop: "5px"}}>
                   <Button bsStyle={this.props.buttonStyle} bsSize="xs" disabled={!snapshotReady}
            		onClick={this.onClickCancel}>
            			<Glyphicon disabled={{}}/>&nbsp;<Message msgId={this.props.cancelButtonText}/>
        			</Button>
					<Button bsStyle={this.props.buttonStyle} bsSize="xs" disabled={!snapshotReady}
            			onClick={this.onClickCreate}>
            			<Glyphicon glyph="floppy-save" disabled={{}}/>&nbsp;<Message msgId={this.props.saveButtonText}/>
        			</Button>
                </Row>

            </Grid>
        ) : null;
    }


    isSignalementReady = () => {
        return this.props.signalement.state === "READY" && !this.mapIsLoading(this.props.layers) && this.props.map && this.props.map.size;
    };
}

module.exports = SignalmentSubmitPanel;

