import React from 'react';
import Dock from 'react-dock';
import ContainerDimensions from 'react-container-dimensions';
import {PropTypes} from 'prop-types';
import {Grid, Col, Row, Glyphicon, Button, Form, FormControl,ControlLabel, FormGroup} from 'react-bootstrap';
import Message from '../../../MapStore2/web/client/components/I18N/Message';
import ConfirmDialog from '../../../MapStore2/web/client/components/misc/ConfirmDialog';
import ResizableModal from '../../../MapStore2/web/client/components/misc/ResizableModal';
import './signalement.css';
import {status} from '../actions/signalement-action';
import {GeometryType} from '../constants/signalement-constants';


export class SignalementPanelComponent extends React.Component {
    static propTypes = {
        id: PropTypes.string,
        active: PropTypes.bool,
        status: PropTypes.string,
        closing: PropTypes.bool,
        drawing: PropTypes.bool,
        // config
        wrap: PropTypes.bool,
        wrapWithPanel: PropTypes.bool,
        panelStyle: PropTypes.object,
        panelClassName: PropTypes.string,
        closeGlyph: PropTypes.string,
        createGlyph: PropTypes.string,
        deleteGlyph: PropTypes.string,
        buttonStyle: PropTypes.object,
        style: PropTypes.object,
        dockProps: PropTypes.object,
        width: PropTypes.number,
        // data
        attachmentConfiguration: PropTypes.object,
        contextLayers: PropTypes.array,
        contextThemas: PropTypes.array,
        user: PropTypes.object,
        currentLayer: PropTypes.object,
        task: PropTypes.object,
        attachements: PropTypes.array,
        error: PropTypes.object,
        // redux
        initDrawingSupport: PropTypes.func,
        stopDrawingSupport: PropTypes.func,
        startDrawing: PropTypes.func,
        stopDrawing: PropTypes.func,
        clearDrawn: PropTypes.func,
        loadAttachmentConfiguration: PropTypes.func,
        addAttachment: PropTypes.func,
        removeAttachment: PropTypes.func,
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
        drawing: false,
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
        deleteGlyph: "trash",
        // side panel properties
        width: 660,
        dockProps: {
            dimMode: "none",
            size: 0.30,
            fluid: true,
            position: "right",
            zIndex: 1030
        },
        dockStyle: {
            zIndex: 100,
        },
        // data
        attachmentConfiguration: null,
        contextLayers: null,
        contextThemas: null,
        user: null,
        currentLayer: null,
        task: null,
        attachements: [],
        // misc
        initDrawingSupport: ()=>{},
        stopDrawingSupport: ()=>{},
        startDrawing: ()=>{},
        stopDrawing: ()=>{},
        clearDrawn: ()=>{},
        loadAttachmentConfiguration: ()=>{},
        addAttachment: () => {},
        removeAttachment: () => {},
        loadThemas: ()=>{},
        loadLayers: ()=>{},
        getMe: ()=>{},
        createDraft: ()=>{},
        cancelDraft: ()=>{},
        createTask: ()=>{},
        requestClosing: ()=>{},
        cancelClosing: ()=>{},
        confirmClosing: ()=>{},
        toggleControl: () => {}
    };


    constructor(props) {
        super(props);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handleContextChange = this.handleContextChange.bind(this);
        this.state = {
            errorAttachment: ""
        }
        //configureBackendUrl(this.props.backendurl);
        //console.log(this.state);
        //console.log(this.props);
    }

    componentWillMount() {
        this.setState({initialized: false, loaded: false, task: null, currentLayer: null});
        this.props.loadAttachmentConfiguration();
        this.props.loadThemas();
        this.props.loadLayers();
        this.props.initDrawingSupport();
        this.props.getMe();
    }

    componentDidUpdate(prevProps, prevState) {
        console.log("sig didUpdate...");
        console.log(this.props);
        // Tout est-il initialisé ?
        this.state.initialized = this.props.contextLayers !== null && this.props.contextThemas !== null &&
            this.props.attachmentConfiguration !== null && this.props.user !== null;
        // on récupère la current layer si elle existe
        this.state.currentLayer = this.props.currentLayer;

        if (this.props.task !== null && this.state.task === null && this.props.status === status.TASK_INITIALIZED) {
            // on a une tâche dans les props, pas dans le state et on est à "tâche initialisée"
            console.log("sig draft created");
            this.state.task = this.props.task;
            this.state.loaded = true;
            this.setState(this.state);
        }

        if (this.state.task !== null && this.state.task.asset !== null) {
            this.state.task.asset.attachments = this.props.attachments
            if (this.props.task && this.props.task.asset) {
                this.state.task.asset.localisation = this.props.task.asset.localisation;
            }
        }

        if( this.state.task !== null && this.state.task.asset !== null && this.state.task.asset.uuid &&
            this.props.status === status.REQUEST_UNLOAD_TASK){
            // on a une tâche et on demande son annulation => on lancer l'annulation
            console.log("sig draft cancel");
            this.props.cancelDraft(this.state.task.asset.uuid);
        }

        if ((this.props.status === status.TASK_UNLOADED || this.props.status === status.TASK_CREATED) &&
            this.props.active === true && this.state.loaded === true) {
            // on a demandé l'annulation et on l'a obtenue => on ferme le panel
            console.log("sig draft canceled");
            this.state.task = null;
            this.state.loaded = false;
            this.setState(this.state);
            this.props.stopDrawingSupport();
            this.props.toggleControl();
        }

        if (this.props.status === status.TASK_CREATED &&
            this.props.active === true && this.state.loaded === true) {
            // on a demandé la création et on l'a obtenue => on ferme le panel
            console.log("sig task created");
            this.state.task = null;
            this.state.loaded = false;
            this.setState(this.state);
            this.props.toggleControl();
        }
        console.log(this.state);
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
        if( contextDescriptions != null && contextDescriptions.length > 0) {
            this.state.task.asset.contextDescription = contextDescriptions[0];
            this.props.clearDrawn();
        }
        this.setState(this.state);
    }

    render() {
        console.log("sig render");
        if (this.props.active) {
            // si le panel est ouvert
            if (this.state.initialized && this.props.contextThemas.length > 0) {
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
        if (this.props.active) {
            // le panel est ouvert
            return (
                <ContainerDimensions>
                    {({width}) =>
                        <span>
                            <span className="ms-signalement-panel react-dock-no-resize ms-absolute-dock ms-side-panel">
                                <Dock
                                    dockStyle={this.props.dockStyle} {...this.props.dockProps}
                                    isVisible={this.props.active}
                                    size={this.props.width / width > 1 ? 1 : this.props.width / width}>
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
    renderModelClosing() {
        if (this.props.closing) {
            // si closing == true on demande l'abandon
            console.log("sig closing");
            return (<ConfirmDialog
                show
                modal
                onClose={this.props.cancelClosing}
                onConfirm={this.props.confirmClosing}
                confirmButtonBSStyle="default"
                closeGlyph="1-close"
                confirmButtonContent={<Message msgId="signalement.msgBox.ok"/>}
                closeText={<Message msgId="signalement.msgBox.cancel"/>}>
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
            <Grid fluid className="ms-header" style={this.props.styling || this.props.mode !== "list" ? {
                width: '100%',
                boxShadow: 'none'
            } : {width: '100%'}}>
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
                        <Button className="square-button no-border" onClick={() => this.create()}>
                            <Glyphicon glyph={this.props.createGlyph}/>
                        </Button>
                        <Button className="square-button no-border" onClick={() => this.cancel()}>
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
    renderMessage() {
        if (this.props.error) {
            return (
                <span className="error"><Message msgId={this.props.error.message}/></span>
            );
        } else if (this.props.message) {
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
                        <FormControl type="text" readOnly
                                     value={this.props.user !== null ? this.props.user.login : ''}/>
                    </FormGroup>
                    <FormGroup controlId="signalement.user.organization">
                        <ControlLabel><Message msgId="signalement.organization"/></ControlLabel>
                        <FormControl type="text" readOnly
                                     value={this.props.user !== null ? this.props.user.organization : ''}/>
                    </FormGroup>
                    <FormGroup controlId="signalement.user.email">
                        <ControlLabel><Message msgId="signalement.email"/></ControlLabel>
                        <FormControl type="text" readOnly
                                     value={this.props.user !== null ? this.props.user.email : ''}/>
                    </FormGroup>
                </fieldset>
            </div>
        );
    }

    /**
     * La rendition du contexte
     */
    renderContext() {
        if (this.state.currentLayer !== null) {
            return (<div id={this.props.id}>
                <fieldset>
                    <legend><Message msgId="signalement.reporting.layer"/></legend>
                    <FormGroup controlId="signalement.layer">
                        <ControlLabel>&nbsp;</ControlLabel>
                        <FormControl type="text" readOnly value={this.state.currentLayer.label}/>
                    </FormGroup>
                </fieldset>
            </div>);
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
                    <div className="container">
                        <div className="row">
                            <div className="col-md-6">
                                <div className="form-group files color">
                                    <span className="btn btn-primary btn-file">
                                       <Message msgId="signalement.attachment.add"/><input type="file" name="file"
                                                                                           onChange={(e) => this.fileAddedHandler(e)}/>
                                    </span>
                                    <Message msgId="signalement.fileUpload.info"/>
                                    <div>
                                        {
                                            this.state.errorAttachment ? (
                                                <legend><Message msgId={this.state.errorAttachment}/></legend>
                                            ) : null
                                        }
                                    </div>
                                    <table className="table">
                                        <thead>
                                        <tr>
                                            <th scope="col">Name</th>
                                            <th scope="col">Action</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {this.renderTable(this.props.attachments)}
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </fieldset>
            </div>
        )
    }


    renderTable(attachments) {

        if (attachments) {
            return attachments.map((attachment, index) => {
                return (
                    <tr key={index}>
                        <td>{attachment.name}</td>
                        <td><Button className="btn btn-sq-xs btn-danger"
                                    onClick={() => this.fileDeleteHandler(attachment.id, index)}>
                            <Glyphicon glyph={this.props.deleteGlyph}/>
                        </Button></td>
                    </tr>
                )
            })
        }
    }

    /**
     * La rendition de la saisie de la géométrie
     */
    renderLocalisation() {
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.localization"/></legend>
                    <div>
                        { this.renderGeometryDrawButton() }
                        { this.renderGeometryDrawMessage() }
                    </div>
                </fieldset>
            </div>
        )
    }

    /**
     * Affichage du bouton permettant de définir la géométrie d'un signalement
     */
    renderGeometryDrawButton = ()=> {
        return (
            <Button className="square-button" bsStyle={this.props.drawing ? 'primary' : 'default'} onClick={this.onDraw}>
                <Glyphicon glyph={this.state.task.asset.contextDescription.geographicType.toLowerCase()}/>
            </Button>
        );
    }

    /**
     * Action sur le bouton permettant de définir la géométrie d'un signalement (start ou stop du dessin)
     */
    onDraw = ()=> {
        const geometryType = GeometryType[this.state.task.asset.contextDescription.geographicType];
        if (this.props.drawing) {
            this.props.stopDrawing(geometryType);
        }
        else {
            this.props.startDrawing(geometryType, this.props.task.asset.localisation);
        }
    }

    /**
     * Affichage du message sur le dessin de la geometrie du signalement
     */
    renderGeometryDrawMessage = ()=> {
        if (this.state.task && this.state.task.asset && this.state.task.asset.localisation && this.state.task.asset.localisation.length > 0) {
            return (
                <Message msgId="signalement.localization.drawn"/>
            );
        }
        else {
            return (
                <Message msgId="signalement.localization.tips"/>
            );
        }
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


    validateAttachment(attachment) {
        let errorAttachment = "";
        if (attachment.file === undefined || !(attachment.file instanceof File) || this.props.attachmentConfiguration.mimeTypes.includes(attachment.file.type) === false) {
            errorAttachment = 'signalement.attachment.typeFile';
        }

        if (attachment.file.size > this.props.attachmentConfiguration.maxSize) {
            errorAttachment = 'signalement.attachment.size';
        }

        if (this.props.attachments.length + 1 > this.props.attachmentConfiguration.maxCount) {
            errorAttachment = 'signalement.attachment.length';
        }

        if (errorAttachment) {
            this.setState({errorAttachment});
            return false;
        }
        return true;
    }

    fileAddedHandler(e) {

        //les differents test avant d'uploader le fichier (type, taille)
        console.log('add attachement: ' + e.target.files[0]);
        const attachment = {file: e.target.files[0], uuid: this.state.task.asset.uuid};
        const isValid = this.validateAttachment(attachment);
        if (isValid) {
            this.setState({errorAttachment: ""});
            this.props.addAttachment(attachment);
        }

    }


    fileDeleteHandler(id, index) {
        const attachment = {id: id, uuid: this.state.task.asset.uuid, index: index};
        this.props.removeAttachment(attachment);

    }

    /**
     * L'action d'abandon
     */
    cancel() {
        if (this.state.task != null && this.state.task.asset.uuid && this.state.task.asset.uuid !== null) {
            console.log("Cancel and close:" + this.state.task.asset.uuid);
            this.props.requestClosing();
        } else {
            this.props.toggleControl();
        }
    }

    /**
     * L'action de création
     */
    create() {
        if (this.state.task != null && this.state.task.asset.uuid && this.state.task.asset.uuid !== null) {
            console.log("Create and close:" + this.state.task.asset.uuid);
            this.props.createTask(this.state.task);
        }
    }
};
