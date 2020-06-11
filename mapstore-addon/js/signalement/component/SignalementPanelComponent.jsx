import React, {useState, useEffect} from 'react';
import Dock from 'react-dock';
import {pick} from 'lodash';
import assign from 'object-assign';
import ContainerDimensions from 'react-container-dimensions';
import {connect} from 'react-redux';
import {PropTypes} from 'prop-types';
import {Grid, Col, Row, Glyphicon, Button, Form, FormControl, ControlLabel, FormGroup} from 'react-bootstrap';
import Select from 'react-select';
import Message from '../../../MapStore2/web/client/components/I18N/Message';
import {setControlProperty} from '../../../MapStore2/web/client/actions/controls';
import ConfirmDialog from '../../../MapStore2/web/client/components/misc/ConfirmDialog';
import './signalement.css';
import { actions, status } from '../actions/signalement-action';

export class SignalementPanelComponent extends React.Component {
	 static propTypes = {
        id: PropTypes.string,
        active: PropTypes.bool,
        status: PropTypes.string,
        closing: PropTypes.bool,
        // config
        wrap: PropTypes.bool,
        wrapWithPanel: PropTypes.bool,
        panelStyle: PropTypes.object,
        panelClassName: PropTypes.string,
        closeGlyph: PropTypes.string,
        createGlyph: PropTypes.string,
        buttonStyle: PropTypes.object,
        style: PropTypes.object,
        dockProps: PropTypes.object,
        width: PropTypes.number,
        // data
        attachmentConfiguration: PropTypes.object,
        contextLayers: PropTypes.array,
        contextThemas: PropTypes.array,
        user: PropTypes.object,
        task: PropTypes.object,
        error: PropTypes.object,
        // redux
        loadAttachmentConfiguration: PropTypes.func,
        loadThemas: PropTypes.func,
        loadLayers: PropTypes.func,
        getMe: PropTypes.func,
        createDraft: PropTypes.func,
        cancelDraft: PropTypes.func,
        createTask: PropTypes.func,
        requestClosing: PropTypes.func,
        cancelClosing: PropTypes.func,
        confirmClosing: PropTypes.func,
        toggleControl: PropTypes.func,
    };

    static defaultProps = {
        id: "signalement-panel",
        active: false,
        status: status.NO_TASK,
        closing: false,
        // config
        wrap: false,
        modal: true,
        wrapWithPanel: false,
        panelStyle: {
            zIndex: 100,
            overflow: "hidden",
            height: "100%"
        },
        panelClassName: "signalement-panel",
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
        // data
        attachmentConfiguration: null,
        contextLayers: null,
        contextThemas: null,
        user: null,
        task: null,
        // misc
        loadAttachmentConfiguration: ()=>{},
        loadThemas: ()=>{},
        loadLayers: ()=>{},
        getMe: ()=>{},
        createDraft: ()=>{},
        cancelDraft: ()=>{},
        createTask: ()=>{},
        requestClosing: ()=>{},
        cancelClosing: ()=>{},
        confirmClosing: ()=>{},
        toggleControl: () => {},
    };

    constructor(props) {
        super(props);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleContextChange = this.handleContextChange.bind(this);
        //console.log(this.state);
        //console.log(this.props);
    }

    componentWillMount() {
        this.setState({initialized: false, loaded: false, task: null, currentLayer: null});
        this.props.loadAttachmentConfiguration();
        this.props.loadThemas();
        this.props.loadLayers();
        this.props.getMe();
    }

    componentDidUpdate(prevProps, prevState) {
        console.log("didUpdate...");
        console.log(this.props);
        // Tout est-il initialisé ?
        this.state.initialized = this.props.contextLayers !== null && this.props.contextThemas !== null && 
            this.props.attachmentConfiguration !== null && this.props.user !== null; 
        
        if( this.props.task !== null && this.state.task === null && this.props.status === status.TASK_INITIALIZED ){
            // on a une tâche dans les props, pas dans le state et on est à "tâche initialisée"
            console.log("sig draft created");
            this.state.task = this.props.task;
            this.state.loaded = true;
            this.setState(this.state);
        }
        if( this.state.task !== null && this.state.task.asset !== null && this.state.task.asset.uuid && 
            this.props.status === status.REQUEST_UNLOAD_TASK){
            // on a une tâche et on demande son annulation => on lancer l'annulation
            console.log("sig draft cancel");
            this.props.cancelDraft(this.state.task.asset.uuid);
        }
        if( (this.props.status === status.TASK_UNLOADED || this.props.status === status.TASK_CREATED) && 
            this.props.active===true && this.state.loaded === true){
            // on a demandé l'annulation et on l'a obtenue => on ferme le panel
            console.log("sig draft canceled");
            this.state.task = null;
            this.state.loaded = false;
            this.setState(this.state);
            this.props.toggleControl();
        }
        if( this.props.status === status.TASK_CREATED && 
            this.props.active===true && this.state.loaded === true){
            // on a demandé la création et on l'a obtenue => on ferme le panel
            console.log("sig task created");
            this.state.task = null;
            this.state.loaded = false;
            this.setState(this.state);
            this.props.toggleControl();
        }
    }

    /**
     * Changement de la description
     * 
     * @param {*} e l'événement
     */
    handleDescriptionChange(e) {
        this.state.task.asset.description = e.target.value;
        this.setState(this.state);
    }

    /**
     * Changement du contexte
     * @param {*} e l'événement
     */
    handleContextChange(e) {
        const contextDescriptions = this.props.contextThemas.filter(thema => thema.name === e.target.value);
        if( contextDescriptions != null && contextDescriptions.length >0){
            this.state.task.asset.contextDescription = contextDescriptions[0];
        }
        this.setState(this.state);
    }
	
    render() {
        console.log("sig render");
        if( this.props.active ){
            // si le panel est ouvert
            if( this.state.initialized && this.props.contextThemas.length > 0 ){
                // si on est initialisé avec au moins un context
                if( (!this.props.task || this.props.task === null) && 
                    (this.props.status === status.NO_TASK || this.props.status === status.TASK_UNLOADED || this.props.status === status.TASK_CREATED)){
                    // il n'y a pas de tâche dans les props et on a rien fait ou a vient de créer un tâche avec succès
                    // on lance la création d'une tâche draft avec le context par défaut
                    console.log("sig create draft");
                    this.props.createDraft(this.props.contextThemas[0]);
                }
            }
        }
        if( this.props.active ){
            // le panel est ouvert
            return (
                <ContainerDimensions>
                    { ({ width }) =>
                        <span>
                            <span className="ms-signalement-panel react-dock-no-resize ms-absolute-dock ms-side-panel">
                                <Dock
                                    dockStyle={this.props.dockStyle} {...this.props.dockProps}
                                    isVisible={this.props.active}
                                    size={this.props.width / width > 1 ? 1 : this.props.width / width} >
                                    <div className={this.props.panelClassName}>
                                        {this.renderHeader()}
                                        {
                                            !this.state.initialized || !this.state.loaded ? 
                                                this.renderLoading() : 
                                                this.renderForm()
                                        }
                                    </div>
                                </Dock>
                            </span>
                            {this.renderModelClosing()}
                        </span>
                    }
                </ContainerDimensions>
            );
        } else {
            return null;
        }
    }

    /**
     * La rendition de la fenêtre modal de confirmation d'abandon
     */
    renderModelClosing(){
        if (this.props.closing ) {
            // si closing == true on demande l'abandon
            console.log("sig closing");
            return (<ConfirmDialog
                show
                modal
                onClose={this.props.cancelClosing}
                onConfirm={this.props.confirmClosing}
                confirmButtonBSStyle="default"
                closeGlyph="1-close"
                confirmButtonContent={<Message msgId="signalement.msgBox.ok" />}
                closeText={<Message msgId="signalement.msgBox.cancel" />}>
                <Message msgId="signalement.msgBox.info"/>
            </ConfirmDialog>);
        } else {
            return null;
        }
    }

    /**
     * Rendition du message de chargement en attendant la nouvelle tâche draft
     */
    renderLoading() {
        return (
            <div><Message msgId="signalement.loading"/></div>
        );
    }

    /**
     * La rendition du formulaire
     */
    renderForm() {
        return (
            <Form model={this.state.task}>
                {this.renderUserInformation()}                            
                {this.renderContext()}
                {this.renderDetail()}
                {this.renderAttachments()}
                {this.renderLocalisation()}
                {this.renderCustomForm()}
            </Form>
        );
    }

    /**
     * La rendition de l'entête 
     */
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
                        {this.renderMessage()}
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

    /**
     * La rendition d'un message d'erreur
     */
    renderMessage(){
        if( this.props.error ){
            return (
                <span className="error"><Message msgId={this.props.error.message}/></span>
            );
        } else if( this.props.message ){
            return (
                <span className="info"><Message msgId={this.props.message}/></span>
            );  
        } else {
            return null;
        }  
    }

    /**
     * La rendition de la partie utilisateur
     */
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

    /**
     * La rendition du contexte
     */
    renderContext() {
        if( this.state.currentLayer !== null) {
            <div id={this.props.id}>
                <fieldset>
                    <legend><Message msgId="signalement.reporting.layer"/></legend>
                    <FormGroup controlId="signalement.layer">
                        <FormControl type="text" readOnly value={this.state.currentLayer.label}/>
                    </FormGroup>
                </fieldset>
            </div>
        } else {
            return (
                <div id={this.props.id}>
                    <fieldset>
                        <legend><Message msgId="signalement.reporting.thema"/></legend>
                        <FormGroup controlId="signalement.thema">
                            <FormControl componentClass="select"
                                value={this.state.task.asset.contextDescription.name}
                                onChange={this.handleContextChange}
                                >
                                {
                                    (this.props.contextThemas || []).map((thema) => {
                                        return <option key={thema.name} value={thema.name}>{thema.label}</option>
                                    })
                                }
                            </FormControl>
                        </FormGroup>
                    </fieldset>
                </div>
            );
        }
    }

    /**
     * La rendition du détail du signalement
     */
    renderDetail() {
		return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.description"/></legend>
                    <FormGroup controlId="signalement.description">
                        <FormControl componentClass="textarea" 
                            defaultValue={this.state.task.asset.description}
                            onChange={this.handleDescriptionChange}
                        />
                        <ControlLabel></ControlLabel>
                    </FormGroup>
                </fieldset>
            </div>
        );

    }

    /**
     * La rendition de la gestion des picèces jointes
     */
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
    
    /**
     * La rendition de la saisie de la géométrie
     */
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

    /**
     * La rendition du formulaire associé à la task
     */
    renderCustomForm() {
        return (
            <div>
            </div>
        )
    }

    /**
     * L'action d'abandon
     */
    cancel() {
        if(  this.state.task != null && this.state.task.asset.uuid && this.state.task.asset.uuid !== null) {
            console.log("Cancel and close:"+this.state.task.asset.uuid);
            this.props.requestClosing();
        } else {
            this.props.toggleControl();
        }
    }

    /**
     * L'action de création
     */
    create() {
        if( this.state.task != null && this.state.task.asset.uuid && this.state.task.asset.uuid !== null) {
            console.log("Create and close:"+this.state.task.asset.uuid);
            this.props.createTask(this.state.task);
        }
    }
};