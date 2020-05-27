import React from 'react';
import Dock from 'react-dock';
import {pick} from 'lodash';
import assign from 'object-assign';
import ContainerDimensions from 'react-container-dimensions';
import {connect} from 'react-redux';
import {PropTypes} from 'prop-types';
import {Grid, Col, Row, Glyphicon, Button, Form, FormControl, ControlLabel, FormGroup} from 'react-bootstrap';
import Select from 'react-select';
import Message from '../../../MapStore2/web/client/components/I18N/Message';
import './signalement.css';

export class SignalementPanelComponent extends React.Component {
	 static propTypes = {
        id: PropTypes.string,
        active: PropTypes.bool,
        // config
        wrap: PropTypes.bool,
        wrapWithPanel: PropTypes.bool,
        panelStyle: PropTypes.object,
        panelClassName: PropTypes.string,
        toggleControl: PropTypes.func,
        closeGlyph: PropTypes.string,
        createGlyph: PropTypes.string,
        buttonStyle: PropTypes.object,
        style: PropTypes.object,
        dockProps: PropTypes.object,
        width: PropTypes.number,
        // misc
        loadAttachmentConfiguration: PropTypes.func,
        loadThemas: PropTypes.func,
        loadLayers: PropTypes.func,
        getMe: PropTypes.func,
        // data
        currentlayer: PropTypes.object,
        contextLayers: PropTypes.array,
        contextThemas: PropTypes.array,
        task: PropTypes.object,
        user: PropTypes.object
    };

    static defaultProps = {
        id: "signalement-panel",
        active: false,
        wrap: false,
        modal: true,
        wrapWithPanel: false,
        panelStyle: {
            zIndex: 100,
            overflow: "hidden",
            height: "100%"
        },
        panelClassName: "signalement-panel",
        toggleControl: () => {},
        closeGlyph: "1-close",
        createGlyph: "ok",
        // side panel properties
        width: 660,
        dockProps: {
            dimMode: "none",
            size: 0.30,
            fluid: true,
            position: "right",
            zIndex: 1030
        },
        dockStyle: {},
        // misc
        loadAttachmentConfiguration: ()=>{},
        loadThemas: ()=>{},
        loadLayers: ()=>{},
        getMe: ()=>{},
        // data
        currentLayer: null,
        contextLayers: [],
        contextThemas: [],
        task: {
            asset:{
                contextDescription: null,
                description: ""
            }
        },
        user: null
    };

    constructor(props) {
        super(props);
        const pickedProps = pick(this.props, 'currentLayer', 'contextLayers', 'contextThemas', 'user');
        const newState = assign({}, pickedProps);
        this.state = newState;
        console.log("sig create");
    }
	
    render() {
        console.log("sig render");
		return this.props.active ? (
            <ContainerDimensions>
                { ({ width }) =>
                    <span className="ms-signalement-panel react-dock-no-resize ms-absolute-dock ms-side-panel">
                        <Dock
                            dockStyle={this.props.dockStyle} {...this.props.dockProps}
                            isVisible={this.props.active}
                            size={this.props.width / width > 1 ? 1 : this.props.width / width} >
                            <div className={this.props.panelClassName}>
                                {this.renderHeader()}
                                <Form model={this.props.signalement}>
                                    {this.renderUserInformation()}                            
                                    {this.renderContext()}
                                    {this.renderDetail()}
                                    {this.renderAttachments()}
                                    {this.renderLocalisation()}
                                </Form>
                            </div>
                        </Dock>
                    </span>
                }
            </ContainerDimensions>
        ) : null;

    }

    renderHeader() {
        return (
            <Grid fluid className="ms-header" style={this.props.styling || this.props.mode !== "list" ? { width: '100%', boxShadow: 'none'} : { width: '100%' }}>
                <Row>
                    <Col xs={2}>
                        <Button className="square-button no-events">
                            <Glyphicon glyph="exclamation-sign"/>
                        </Button>
                    </Col>
                    <Col xs={8}>
                        <h4><Message msgId="signalement.msgBox.title"/></h4>
                        <span>{this.props.message ? this.props.message : '' }</span>
                    </Col>
                    <Col xs={2}>
                        <Button className="square-button no-border" onClick={() => this.create()} >
                            <Glyphicon glyph={this.props.createGlyph}/>
                        </Button>
                        <Button className="square-button no-border" onClick={() => this.cancel()} >
                            <Glyphicon glyph={this.props.closeGlyph}/>
                        </Button>
                    </Col>
                </Row>
            </Grid>
        );
    }

    renderUserInformation() {
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.user"/></legend>
                    <FormGroup controlId="signalement.user.login">
                        <ControlLabel><Message msgId="signalement.login"/></ControlLabel>
                        <FormControl type="text" readOnly value={this.props.user !== null ? this.props.user.login : ''}/>
                    </FormGroup>
                    <FormGroup controlId="signalement.user.organization">
                        <ControlLabel><Message msgId="signalement.organization"/></ControlLabel>
                        <FormControl type="text" readOnly value={this.props.user !== null ? this.props.user.organization : ''}/>
                    </FormGroup>
                    <FormGroup controlId="signalement.user.email">
                        <ControlLabel><Message msgId="signalement.email"/></ControlLabel>
                        <FormControl type="text" readOnly value={this.props.user !== null ? this.props.user.email : ''}/>
                    </FormGroup>                 
                </fieldset>
            </div>
        );
    }

    renderContext() {
        if( this.props.currentLayer !== null) {
            <div id={this.props.id}>
                <fieldset>
                    <legend><Message msgId="signalement.reporting.layer"/></legend>
                    <FormGroup controlId="signalement.layer">
                        <FormControl type="text" readOnly value={this.props.currentLayer.label}/>
                    </FormGroup>
                </fieldset>
            </div>
        } else {
            return (
                <div id={this.props.id}>
                    <fieldset>
                        <legend><Message msgId="signalement.reporting.thema"/></legend>
                        <FormGroup controlId="signalement.thema">
                            <Select
                                value={this.props.task.asset.contextDescription}
                                options={(this.props.contextThemas || []).map((thema) => ({label: thema.label, value: thema.name}))}/>
                        </FormGroup>
                    </fieldset>
                </div>
            );
        }
    }

    renderDetail() {
		return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.description"/></legend>
                    <FormGroup controlId="signalement.description">
                        <FormControl componentClass="textarea" 
                            defaultValue={this.props.task.asset.description}
                        />
                        <ControlLabel></ControlLabel>
                    </FormGroup>
                </fieldset>
            </div>
        );

    }

    renderAttachments() {
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.attachment.files"/></legend>
                    <div></div>
                </fieldset>
            </div>
        )
    }
    
    renderLocalisation() {
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.localization"/></legend>
                    <div></div>
                </fieldset>
            </div>
        )
    }

    cancel() {
        console.log("Cancel and close");
        //this.props.active = false;
    }

    create() {
        console.log("create and close");
        this.props.loadAttachmentConfiguration();
        this.props.loadThemas();
        this.props.loadLayers();
        this.props.getMe();
        //this.props.active = false;
    }
};