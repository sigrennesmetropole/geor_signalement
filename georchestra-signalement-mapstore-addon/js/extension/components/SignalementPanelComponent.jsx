import React from 'react';
import {PropTypes} from 'prop-types';
import {
    Button,
    Col,
    ControlLabel,
    Form,
    FormControl,
    FormGroup,
    Glyphicon,
    Grid,
    HelpBlock,
    Row,
    InputGroup
} from 'react-bootstrap';
import Message from '@mapstore/components/I18N/Message';
import ConfirmDialog from '@mapstore/components/misc/ConfirmDialog';
import {status} from '../actions/signalement-action';
import {GeometryType, SIGNALEMENT_PANEL_WIDTH} from '../constants/signalement-constants';
import InlineSpinner from "mapstore2/web/client/components/misc/spinners/InlineSpinner/InlineSpinner";
import ResponsivePanel from "@mapstore/components/misc/panels/ResponsivePanel";
import * as ReactIntl from 'react-intl';

export class SignalementPanelComponent extends React.Component {
    static propTypes = {
        id: PropTypes.string,
        active: PropTypes.bool,
        creating: PropTypes.bool,
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
        infoGlyph: PropTypes.string,
        buttonStyle: PropTypes.object,
        style: PropTypes.object,
        dockStyle:PropTypes.object,
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
		initSignalement: PropTypes.func,
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
        resetAttachments: PropTypes.func,
    };

    static defaultProps = {
        id: "signalement-panel",
        active: false,
        creating: false,
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
        infoGlyph: "info-sign",
        // side panel properties
        width: SIGNALEMENT_PANEL_WIDTH,
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
		initSignalement: ()=>{},
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
        toggleControl: () => {},
        resetAttachments: ()=>{},
    };

    constructor(props) {
        super(props);
        this.state = {
            errorAttachment: "",
            errorFields: {},
            themaSelected: false,
            selectedContextValue: "",
            isContextVisible: false,
            pendingAttachments: [], // Stockage local des fichiers en attente d'upload
            isCreatingDraft: false
        }

        // disable custom logging function if debug_signalement is set to false in local config
        if (this.props.debug_signalement) {
            window.signalement.debug = (...args) => { console.log(...args) };
        }

		this.props.initSignalement(this.props.backendurl);
    }

    componentWillMount() {
        this.setState({initialized: false, loaded: false, task: null, currentLayer: null, errorAttachment: "", themaSelected: false});
        this.props.loadAttachmentConfiguration();
        this.props.loadThemas();
        this.props.loadLayers();
        this.props.getMe();
    }

    componentDidUpdate(prevProps, prevState) {
        window.signalement.debug("sig didUpdate...");
        window.signalement.debug("sig didUpdate props...", this.props);
        window.signalement.debug("sig didUpdate state...", this.state);

        // Réinitialiser le champ file quand les attachments sont réinitialisés
        if (prevProps.attachments.length > 0 && this.props.attachments.length === 0) {
            this.resetFileInput();
            this.setState({ pendingAttachments: [] });
        }


        // Tout est-il initialisé ?
        this.state.initialized = this.props.contextLayers !== null && this.props.contextThemas !== null &&
            this.props.attachmentConfiguration !== null && this.props.user !== null;
        // on récupère la current layer si elle existe
        this.state.currentLayer = this.props.currentLayer;

        if (this.props.task !== null && this.state.task === null && this.props.status === status.TASK_INITIALIZED) {
            // on a une tâche dans les props, pas dans le state et on est à "tâche initialisée"
            window.signalement.debug("sig draft created");
            window.signalement.debug("sig draft created props ", this.props);
            window.signalement.debug("sig draft created state ", this.state);
            this.setState({
                task: this.props.task,
                loaded: true
            });
        }

        if (this.state.task !== null && this.state.task?.asset !== null) {
            this.state.task.asset.attachments = this.props.attachments
            if (this.props.task && this.props.task.asset) {
                this.state.task.asset.localisation = this.props.task.asset.localisation;
            }
        }

        if( this.state.task !== null && this.state.task?.asset !== null && this.state.task?.asset.uuid &&
            this.props.status === status.REQUEST_UNLOAD_TASK){
            // on a une tâche et on demande son annulation => on lancer l'annulation
            window.signalement.debug("sig draft cancel");
            this.props.cancelDraft(this.props.task?.asset?.uuid);
        }

        if( (this.props.status === status.TASK_UNLOADED || this.props.status === status.TASK_CREATED) && this.state.loaded === true){
            // on a demandé l'annulation et on l'a obtenue => on ferme le panel
            window.signalement.debug("sig draft canceled or task created");
            this.setState({
                task: null,
                loaded: false,
                errorAttachment: "",
                errorFields: {},
                pendingAttachments: []
            })
            this.props.stopDrawingSupport();
            this.props.toggleControl();
        }
        window.signalement.debug(this.state);


        //     Quand on passe d'un signalmenent par couche à un signalement par thématique
        const isLastDraftLayer = this.props.contextLayers?.length > 0 && this.props?.contextLayers?.find(layer => layer.name === this.props?.task?.asset?.contextDescription?.name);
        if ((this.props.status === status.TASK_INITIALIZED)
            && !this.props.currentLayer
            && !this.state.currentLayer
            && isLastDraftLayer
            && !this.state.isCreatingDraft
            && prevProps.status !== status.LOAD_TASK) {
            const initContext = this.props.contextThemas[0];

            this.setState({ isCreatingDraft: true }); // Marquer qu'on crée un draft
            this.props.createDraft(initContext, this.props.task?.asset?.uuid);

            this.setState({
                isContextVisible: this.props.contextThemas.length === 1,
                selectedContextValue: "",
                themaSelected: false,
                task: null,
                errorFields: {},
                pendingAttachments: []
            });
        }

        //  Réinitialiser le flag quand le draft est créé
        if (prevProps.status !== status.TASK_INITIALIZED && this.props.status === status.TASK_INITIALIZED) {
            this.setState({ isCreatingDraft: false });
        }



        //     Quand on passe d'un signalmenent par thématique à un signalement par couche
        if((this.props.status === status.TASK_INITIALIZED)  && this.props.currentLayer && this.state.currentLayer && !isLastDraftLayer) {
            this.props.createDraft(this.props.currentLayer, this.props.task?.asset?.uuid);
            this.isContextVisible = true;
            this.setState(prevState => ({
                task: {
                    ...prevState.task,
                    asset: {
                        ...prevState.task.asset,
                        description: "",
                        geographicType: this.props.currentLayer.geographicType,
                        localisation: null,
                        attachments: null
                    },
                },
                    errorFields: {},
                pendingAttachments: []
            }));
        }

        // Quand il n'y a qu'une seule thématique, la sélectionner automatiquement
        if (
            !this.props.currentLayer &&
            this.props.contextThemas?.length === 1 &&
            !this.state.themaSelected &&
            this.props.status === status.TASK_INITIALIZED &&
            this.props.task
        ) {
            const singleThema = this.props.contextThemas[0];
            this.setState({
                themaSelected: true,
                selectedContextValue: singleThema.name,
                isContextVisible: true
            });
        }

        // Vérification si la valeur du contexte a changé pour mise à jour du contexte
        if (
            prevProps.task?.asset?.contextDescription?.label !==
            this.props.task?.asset?.contextDescription?.label
        ) {
            // Nouvelle valeur calculée à partir des props
            const newPropValue =
                this.props.task?.asset?.contextDescription?.label ?? this.props.contextThemas?.[0]?.name;

            // Vérifier si nous sommes déjà synchronisés avec la valeur provenant des props
            if (this.state.selectedContextValue !== newPropValue) {
                // Mettre à jour uniquement si l'utilisateur n'a pas interagi récemment
                this.setState((prevState) => {
                    // Si une interaction utilisateur a clairement modifié la valeur,
                    // nous ne mettons pas à jour celle-ci depuis les props.
                    if (!prevState.themaSelected) {
                        return {
                            selectedContextValue: "",
                        };
                    }
                    // Sinon, on garde l'état tel qu'il est
                    return null;
                });
            }
        }

        // Lorsque le draft est vraiment annulé côté serveur
        if (prevProps.status !== status.TASK_UNLOADED && this.props.status === status.TASK_UNLOADED) {
            // on ferme le panel et on reset le form
            this.resetForm();
            this.props.stopDrawingSupport();
            this.props.toggleControl();
        }

    }

    /**
     * Réinitialise tous les champs du formulaire à leur état initial.
     */
    resetForm = () => {
        this.setState({
            task: {
                asset: {
                    contextDescription: null,
                    contextDescriptionLabel: "",
                    description: "",
                    geographicType: null,
                    localisation: null,
                    attachments: []
                }
            },
            errorFields: {},
            pendingAttachments: [],
            selectedContextValue: "",
            isContextVisible: false,
            themaSelected: false
        });
        this.props.clearDrawn();
        this.props.resetAttachments();
        this.resetFileInput();
    }


    /**
     * Changement de la description
     *
     * @param {*} e l'événement
     */
    handleDescriptionChange = (e) => {
        const newDescription = e.target.value;
        this.setState(prevState => ({
            task: {
                ...prevState.task,
                asset: {
                    ...prevState.task.asset,
                    description: newDescription
                }
            }
        }));
    }

    /**
     * Changement du contexte
     * @param {*} e l'événement
     */
    handleContextChange = (e) => {
        const newValue = e.target.value;

        const contextDescriptions = this.props.contextThemas.filter(thema => thema.name === newValue);
        if( contextDescriptions != null && contextDescriptions.length > 0) {
            const newTask = {
                asset: {
                    contextDescription: contextDescriptions[0],
                    geographicType: contextDescriptions[0].geographicType,
                    description: "",
                },
            };

            this.setState({
                selectedContextValue: newValue,
                isContextVisible: true,
                themaSelected: true,
                task: newTask,
                errorFields: {},
                pendingAttachments: [], // Réinitialiser les fichiers locaux
                isCreatingDraft: true //   Marquer qu'on crée un draft

            });

            this.props.clearDrawn();
            // Réinitialiser les attachments quand on change de contexte
            this.props.resetAttachments();
            //  Ne créer le draft que si on n'est pas déjà en train d'en créer un
            if (this.props.status !== status.LOAD_TASK) {
                this.props.createDraft(contextDescriptions[0], this.props.task?.asset?.uuid);
            }


        }
    }

    render() {
        window.signalement.debug("sig render");

        if( this.props.active ){
            // si le panel est ouvert
            if( this.state.initialized && (this.props.contextThemas.length > 0 || this.props.currentLayer) ){
                // si on est initialisé avec au moins un context
                if( !this.props.task &&
                    (this.props.status === status.NO_TASK
                        || this.props.status === status.TASK_CREATED)) {
                    // il n'y a pas de tâche dans les props et on a rien fait ou on vient de créer un tâche avec succès
                    // on lance la création d'une tâche draft avec le context par défaut
                    const initContext = this.props.currentLayer ? this.props.currentLayer : this.props.contextThemas[0];
                    this.props.createDraft(initContext, undefined);
                    this.setState({
                        themaSelected: false
                    });
                }
                if(this.props.status === status.TASK_UNLOADED  && !this.props.currentLayer) {
                    this.props.createDraft(this.props.contextThemas[0], undefined);
                }
                if((this.props.status === status.TASK_INITIALIZED || this.props.status === status.TASK_UNLOADED)  && this.props.currentLayer && !this.state.currentLayer) {
                    this.props.createDraft(this.props.currentLayer, undefined);
                    this.setState({
                        currentLayer: this.props.currentLayer
                    });
                }

            }

        }
        if( this.props.active ){
            // le panel est ouvert
            return (
                <ResponsivePanel
                    containerStyle={this.props.dockStyle}
                    style={this.props.dockStyle}
                    containerId="ms-signalement-panel"
                    containerClassName="signalement-dock-container"
                    className={this.props.panelClassName}
                    open={this.props.active}
                    position="right"
                    size={this.props.width}
                    bsStyle="primary"
                    title={<Message msgId="signalement.title"/>}
                    glyph="exclamation-sign"
                    onClose={() => this.cancel()}>
                    <span>
                        <div>
                            {this.renderHeader()}
                            {
                                !this.state.initialized && !this.state.loaded ?
                                    this.renderLoading() :
                                    this.renderForm()
                            }
                            </div>
                        {this.renderModelClosing()}
                    </span>
                </ResponsivePanel>
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
            window.signalement.debug("sig closing");
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
                {this.renderInstructions()}
                {this.renderContext()}
                {this.renderDetail()}
                {this.renderCustomForm()}
                {this.renderAttachments()}
                {this.renderLocalisation()}
                {this.renderFormButton()}
            </Form>
        );
    }

    /**
     * La rendition de l'entête
     */
    renderHeader() {
        return (
            <Grid fluid className="ms-header" style={this.props.styling || this.props.mode !== "list" ? { width: '100%', boxShadow: 'none'} : { width: '100%' }}>
                <Row className="error-box">
                    {this.renderMessage()}
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
                    <FormGroup controlId="signalement.user.organization">
                        <InputGroup className="input-group">
                            <InputGroup.Addon className="addon">
                                <Message msgId="signalement.organization"/>
                            </InputGroup.Addon>
                            <FormControl type="text" readOnly value={this.props.user !== null ? this.props.user.organization : ''}/>
                        </InputGroup>
                    </FormGroup>
                </fieldset>
            </div>
        );
    }

    /**
     * La rendition du bloc d'instructions
     */
    renderInstructions() {
        return (
            <div>
                <fieldset className="instructions">
                    <Row>
                        <Col xs={2}>
                            <Button className="square-button no-events info-glyph">
                                <Glyphicon glyph={this.props.infoGlyph}/>
                            </Button>
                        </Col>
                        <Col className="message-instructions" xs={10}>
                            <Message msgId="signalement.instructions"/>
                        </Col>
                    </Row>
                </fieldset>
            </div>
        )
    }

    /**
     * La rendition du contexte
     */
    renderContext() {
        if( this.state.currentLayer !== null) {
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
                        <legend><Message msgId="signalement.reporting.thema"/> *</legend>
                        <FormGroup controlId="signalement.thema">
                            <FormControl componentClass="select"
                                         value={this.state.selectedContextValue}
                                         onChange={this.handleContextChange}
                            >
                                {
                                    (!this.state.themaSelected && this.props.contextThemas.length > 1)
                                        ? (
                                        <ReactIntl.FormattedMessage id="signalement.reporting.thema.placeholder">
                                            {(message) => <option>{message}</option>}
                                        </ReactIntl.FormattedMessage>
                                        )
                                        : null
                                }
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
                    <legend><Message msgId="signalement.description"/> *</legend>
                    <FormGroup controlId="signalement.description">
                        <FormControl componentClass="textarea"
                                     value={this.state.task?.asset?.description}
                                     onChange={this.handleDescriptionChange}
                                     maxLength={1000}
                        />
                        <HelpBlock>
                            <Message msgId="signalement.description.count"/> {1000 - this.state.task?.asset?.description?.length}
                        </HelpBlock>
                    </FormGroup>
                </fieldset>
            </div>
        );

    }

    /**
     * La rendition de la gestion des picèces jointes
     */
    renderAttachments() {
        const hasActiveTask = !!this.props.task?.asset?.uuid;
        // Vérifier si un contexte est sélectionné (thématique ou layer)
        const hasContextSelected = !!(
            (this.state.selectedContextValue && this.state.selectedContextValue !== "") || // Thématique sélectionnée
            this.state.currentLayer // Layer sélectionné
        );

        // Le bouton est activé seulement si on a une tâche active ET un contexte sélectionné
        const isUploadEnabled = hasActiveTask && hasContextSelected;



        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.attachment.files"/></legend>
                    <FormGroup controlId="formControlsFile">
                        <FormControl type="file" name="file"
                                     disabled={!isUploadEnabled}
                                     onChange={(e) => this.fileAddedHandler(e)} />
                        <HelpBlock><Message msgId="signalement.fileUpload.info"/></HelpBlock>
                    </FormGroup>
                    <div className="col-sm-12">
                        <div id="passwordHelp" className="text-danger">
                            {
                                this.state.errorAttachment ? (
                                    <Message msgId={this.state.errorAttachment}/>
                                ) : null
                            }
                        </div>
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
                </fieldset>
            </div>
        )
    }

    /**
     * La rendition du panel des pièces jointes
     */
    renderTable() {

        // Combiner les attachments uploadés et les fichiers locaux en attente
        const uploadedAttachments = (this.props.attachments || []).map(att => ({
            ...att,
            isLocal: false
        }));

        const allAttachments = [...uploadedAttachments, ...this.state.pendingAttachments];

        if (allAttachments.length > 0) {
            return allAttachments.map((attachment, index) => {
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
        return null;
    }

    /**
     * La rendition de la saisie de la géométrie
     */
    renderLocalisation() {
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.localization"/> *</legend>
                    <FormGroup controlId="localisation">
                        <Row>
                            <Col xs={9} className="localization-tips">
                                <Message msgId="signalement.localization.tips"/>
                            </Col>
                            <Col xs={3}>
                                {this.renderGeometryDrawButton()}
                            </Col>
                        </Row>
                    </FormGroup>
                </fieldset>
            </div>
        )
    }

    /**
     * Affichage du bouton permettant de définir la géométrie d'un signalement
     */
    renderGeometryDrawButton = ()=> {
        return (
            <ReactIntl.FormattedMessage id="signalement.localization.geolocate.hover">
                {(message) =>
                    <Button
                        className={!this.state.themaSelected? "geometry-button boutonHover": "geometry-button"}
                        data-message={message}
                        disabled={!this.state.themaSelected && !this.state.currentLayer}
                        bsStyle={this.props.drawing ? 'primary' : 'default'}
                        bsSize="small"
                        onClick={this.onDraw}
                    >
                        <Message msgId="signalement.localization.geolocate" children={(param) => param}/>
                        <Glyphicon glyph={this.state.task?.asset?.geographicType?.toLowerCase()}/>
                    </Button>
                }
            </ReactIntl.FormattedMessage>
        );
    }

    /**
     * Action sur le bouton permettant de définir la géométrie d'un signalement (start ou stop du dessin)
     */
    onDraw = ()=> {
        const geometryType = GeometryType[this.state.task.asset.geographicType];
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
        if (this.state.task && this.props.task.asset && this.props.task.asset.localisation && this.props.task.asset.localisation.length > 0) {
            return (
                <Message msgId="signalement.localization.drawn"/>
            );
        } else {
            return null;
        }
    }

    renderFormButton() {
        return (
            <fieldset>
                <div className="block-inline-spinner">
                    <InlineSpinner loading={this.props.creating} className="inline-spinner"/>
                </div>
                <div className="block-valid-form">
                    <Button bsStyle="warning"
                            bsSize="sm"
                            onClick={() => this.cancel()}>
                        <Message msgId="signalement.cancel"/>
                    </Button>

                    <ReactIntl.FormattedMessage id="signalement.task.form.invalid">
                        {(message) =>
                            <Button bsStyle="primary"
                                    bsSize="sm"
                                    className={!this.checkTaskValid() ? "validation-button boutonHover": "validation-button"}
                                    data-message={message}
                                    disabled={!this.checkTaskValid()}
                                    onClick={() => this.create()}>
                                <Message msgId="signalement.validate"/>
                            </Button>
                        }
                    </ReactIntl.FormattedMessage>
                </div>
            </fieldset>
        )
    }

    /**
     * La rendition du formulaire associé à la task
     */
    renderCustomForm() {
        if(this.props.task && this.props.task.form && this.props.task.form.sections &&
            ((this.state.isContextVisible === true && this.state.selectedContextValue !== "") ||
                (this.state.isContextVisible === true && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER") ||
                (this.state.isContextVisible === false && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER")
            )) {
            return (
                <div>
                    <fieldset>
                        <div>
                            {this.props.task.form.sections.map((section, index) => {
                                return (
                                    <fieldset key={index}>
                                        <legend>{section.label} </legend>
                                        {this.renderSection(section, index)}
                                    </fieldset>
                                )
                            })}
                        </div>
                    </fieldset>
                </div>
            )
        }
    }

    /**
     * La rendition d'une section du formulaire associé à la task
     */
    renderSection(section, index) {
        if(section && section.fields) {
            return (
                <div key={`section.${index}`}> {section.fields.map((field, indexField) => {
                    return (
                        this.renderField(field, indexField, index, section.readOnly)
                    )
                })}</div>
            )
        }
    }

    /**
     * La rendition de chaque field associe à une section
     */
    renderField(field,indexField, index, sectionReadOnly) {

        switch (field.definition.type) {

            case 'LONG':
                return (
                    <div key={`div.section.${index}.field.${indexField}`} className="form-group row">
                        <FormGroup controlId={`section.${index}.field.${indexField}`} >
                            <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}
                            </ControlLabel>
                            <div className="col-sm-5">
                                <FormControl type="number"
                                             readOnly={field.definition.readOnly  || sectionReadOnly}
                                             required={field.definition.required}
                                             name={field.definition.name}
                                             defaultValue={field.values ? field.values[0] : null}
                                             onChange={this.handleFieldChange}
                                />
                            </div>
                            <div>
                                <div className="col-sm-12">
                                    <div id="passwordHelp" className="text-danger">
                                        {
                                            this.state.errorFields[`section.${index}.field.${indexField}`] ? (
                                                <Message msgId={this.state.errorFields[`section.${index}.field.${indexField}`]}/>
                                            ) : null
                                        }
                                    </div>
                                </div>
                            </div>
                        </FormGroup>
                    </div>
                );
            case 'DOUBLE':
                return (
                    <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                        <FormGroup controlId={`section.${index}.field.${indexField}`}>
                            <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                            <div className="col-sm-5">
                                <FormControl type="number"
                                             step={0.1}
                                             readOnly={field.definition.readOnly  || sectionReadOnly}
                                             required={field.definition.required}
                                             name={field.definition.name}
                                             defaultValue={field.values  ? field.values[0] : null}
                                             onChange={this.handleFieldChange}
                                />
                            </div>
                        </FormGroup>
                        <div className="col-sm-12">
                            <div id="passwordHelp" className="text-danger">
                                {
                                    this.state.errorFields[`section.${index}.field.${indexField}`] ? (
                                        <Message msgId={this.state.errorFields[`section.${index}.field.${indexField}`]}/>
                                    ) : null
                                }
                            </div>
                        </div>
                    </div>
                );
            case 'STRING':
                return (this.renderFieldString(field, index, indexField, sectionReadOnly))
            case 'DATE':
                return (
                    <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                        <FormGroup controlId={`section.${index}.field.${indexField}`}>
                            <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                            <div className="col-sm-5">
                                <FormControl type="date"
                                             readOnly={field.definition.readOnly  || sectionReadOnly}
                                             required={field.definition.required}
                                             name={field.definition.name}
                                             defaultValue={field.values  ? field.values[0] : null}
                                             onChange={this.handleFieldChange}
                                />
                            </div>
                        </FormGroup>
                    </div>
                );
            case 'BOOLEAN':
                return (
                    <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                        <FormGroup controlId={`section.${index}.field.${indexField}`}>
                            <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                            <div className="col-sm-5">
                                <FormControl type="checkbox"
                                             disabled={field.definition.readOnly  || sectionReadOnly}
                                             required={field.definition.required}
                                             name={field.definition.name}
                                             defaultChecked={field.values ? field.values[0] : null}
                                             onChange={this.handleFieldChange}
                                />
                            </div>
                        </FormGroup>
                    </div>
                );
            case 'LIST':
                return (
                    <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                        <FormGroup controlId={`section.${index}.field.${indexField}`}>
                            <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                            <div className="col-sm-5">
                                <FormControl componentClass="select"
                                             readOnly={field.definition.readOnly  || sectionReadOnly}
                                             required={field.definition.required}
                                             name={field.definition.name}
                                             multiple={field.definition.multiple}
                                             onChange={this.handleFieldChange}
                                >
                                    {
                                        (JSON.parse(field.definition.extendedType) || []).map((option) => {
                                            return <option key={option.code} value={option.code}>{option.label}</option>
                                        })
                                    }
                                </FormControl>
                            </div>
                        </FormGroup>
                    </div>

                );
            default:
                window.signalement.debug("Type of definition undefined");
        }
    }

    /**
     * La rendition du field de type String
     */
    renderFieldString(field, index, indexField, sectionReadOnly) {

        if (field.definition.extendedType === "textarea") {
            return (
                <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                    <FormGroup controlId={`section.${index}.field.${indexField}`}>
                        <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                        <div className="col-sm-7">
                            <FormControl componentClass="textarea"
                                         readOnly={field.definition.readOnly  || sectionReadOnly}
                                         required={field.definition.required}
                                         name={field.definition.name}
                                         maxLength={field.definition.validators[0].attribute}
                                         defaultValue={field.values ? field.values[0] : null}
                                         onChange={this.handleFieldChange}
                            />
                        </div>
                        <div className="col-sm-12">
                            <div id="passwordHelp" className="text-danger">
                                {
                                    this.state.errorFields[`section.${index}.field.${indexField}`] ? (
                                        <Message msgId={this.state.errorFields[`section.${index}.field.${indexField}`]}/>
                                    ) : null
                                }
                            </div>
                        </div>
                    </FormGroup>
                </div>
            )
        } else {
            return (
                <div  key={`div.section.${index}.field.${indexField}`} className="form-group row">
                    <FormGroup controlId={`section.${index}.field.${indexField}`}>
                        <ControlLabel className="col-sm-2">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                        <div className="col-sm-5">
                            <FormControl type="text"
                                         readOnly={field.definition.readOnly  || sectionReadOnly}
                                         required={field.definition.required}
                                         name={field.definition.name}
                                         maxLength={field.definition.validators[0].attribute}
                                         defaultValue={field.values ? field.values[0] : null}
                                         onChange={this.handleFieldChange}
                            />
                        </div>
                        <div className="col-sm-12">
                            <div id="passwordHelp" className="text-danger">
                                {
                                    this.state.errorFields[`section.${index}.field.${indexField}`] ? (
                                        <Message msgId={this.state.errorFields[`section.${index}.field.${indexField}`]}/>
                                    ) : null
                                }
                            </div>
                        </div>
                    </FormGroup>
                </div>
            )
        }
    }

    /**
     * Changement d'un champs du formulaire associé à la task
     *
     * @param {*} e l'événement
     */
    handleFieldChange = (e) => {
        const idParts = e.target.id.split(".");
        const idSection = idParts[1];
        const idField = idParts[3];

        const field = this.props.task.form.sections[idSection].fields[idField];
        const value = field.definition.type === "BOOLEAN" ? e.target.checked : e.target.value;

        // Clone des erreurs existantes
        const errorFields = { ...this.state.errorFields };

        if (field.definition.validators[0]) {
            const currentErrors = this.getErrorFields(value, field.definition.validators[0], e.target.id);
            if (Object.keys(currentErrors).length === 0) {
                delete errorFields[e.target.id];
            } else {
                errorFields[e.target.id] = currentErrors[e.target.id];
            }
        }

        // Mise à jour du champ
        field.values = [value];

        this.setState({ errorFields });
    }


    /**
     * Récupération des erreurs en fonction de la valeur et le validateur
     *
     * @param {*} value valeur du champs
     * @param {*} validator type du validateur (positive, negative, maxlength)
     * @param {*} id du champs
     */

    getErrorFields(value, validator, id){
        let errorFields= {};

        switch (validator.type) {
            case 'POSITIVE':
                if(value < 0){
                    errorFields[id] = 'signalement.field.error.positive';
                }
                break;
            case 'NEGATIVE':
                if(value > 0) {
                    errorFields[id] = 'signalement.field.error.negative';
                }
                break;
            case 'MAXLENGTH':
                if(value.length > parseInt(validator.attribute, 10)) {
                    errorFields[id] = `maximum de caractères : ${validator.attribute}`;
                }
                break;
        }
        return errorFields;
    }

    /**
     * Validation de la pièce de jointe (type, taille...) avant l'uploader
     *
     * @param {*} attachment
     */
    validateAttachment(attachment) {
        let errorAttachment = "";
        if (attachment.file === undefined || !(attachment.file instanceof File) || this.props.attachmentConfiguration.mimeTypes.includes(attachment.file.type) === false) {
            errorAttachment = 'signalement.attachment.typeFile';
        }

        if (attachment.file && attachment.file.size > this.props.attachmentConfiguration.maxSize) {
            errorAttachment = `la taille du fichier est supérieur à : ${this.props.attachmentConfiguration.maxSize}`
        }

// Compter les fichiers locaux + ceux déjà uploadés
        const totalAttachments = this.state.pendingAttachments.length + this.props.attachments.length;
        if (totalAttachments + 1 > this.props.attachmentConfiguration.maxCount) {
            errorAttachment = `Vous ne pouvez pas ajouter plus de ${this.props.attachmentConfiguration.maxCount} fichiers`
        }


        if (errorAttachment) {
            this.setState({errorAttachment});
            return false;
        }
        return true;
    }

    /**
     * Réinitialise le champ d'upload de fichier
     */
    resetFileInput = () => {
        const fileInput = document.getElementById('formControlsFile');
        if (fileInput) {
            fileInput.value = '';
        }
    }


    /**
     * Action pour ajouter une pièce jointe
     *
     * @param {*} e l'événement
     */
    fileAddedHandler(e) {
        // Récupérer l'UUID depuis les props plutôt que depuis le state local
        const taskUuid = this.props.task?.asset?.uuid;


        if (!taskUuid) {
            window.signalement.debug("Impossible d'ajouter une pièce jointe : UUID de la tâche non disponible");
            // this.setState({errorAttachment: "signalement.attachment.no.task"});
            // Réinitialiser le champ file en cas d'erreur
            this.resetFileInput();
            return;
        }

        // Vérifier si un contexte est sélectionné
        const hasContextSelected = !!(
            (this.state.selectedContextValue && this.state.selectedContextValue !== "") || // Thématique sélectionnée
            this.state.currentLayer // Layer sélectionné
        );

        if (!hasContextSelected) {
            window.signalement.debug("Impossible d'ajouter une pièce jointe : aucun contexte sélectionné");
            this.setState({errorAttachment: "signalement.attachment.no.context"});
            // Réinitialiser le champ file en cas d'erreur
            this.resetFileInput();
            return;
        }

        // Créer un objet attachment avec le fichier pour stockage local
        let attachment = {
            file: e.target.files[0],
            uuid: taskUuid,
            id: Date.now() + '_' + Math.random().toString(36).substr(2, 9), // ID temporaire
            name: e.target.files[0].name,
            size: e.target.files[0].size,
            type: e.target.files[0].type,
            isLocal: true // Flag pour indiquer que c'est un fichier local non uploadé
        }


        const isValid = this.validateAttachment(attachment);

        if (isValid) {
            this.setState(prevState => ({
                errorAttachment: "",
                pendingAttachments: [...prevState.pendingAttachments, attachment]
            }));
            // Réinitialiser le champ file après upload réussi
            this.resetFileInput();
        } else {
            // Réinitialiser le champ file en cas d'erreur de validation
            this.resetFileInput();
        }

    }

    /**
     * Action pour supprimer une pièce jointe
     *
     * @param id du fichier à supprimer
     * @param index du fichier dans la liste
     */
    fileDeleteHandler(id, index) {
// Si c'est un fichier local (pas encore uploadé)
        const localAttachment = this.state.pendingAttachments.find(att => att.id === id);
        if (localAttachment) {
            this.setState(prevState => ({
                pendingAttachments: prevState.pendingAttachments.filter(att => att.id !== id)
            }));
            return;
        }

        // Si c'est un fichier déjà uploadé (cas existant)
        const taskUuid = this.props.task?.asset?.uuid;
        if (!taskUuid) {
            window.signalement.debug("Impossible de supprimer une pièce jointe : UUID de la tâche non disponible");
            return;
        }

        const attachment = {id: id, uuid: taskUuid, index: index};
        this.props.removeAttachment(attachment);
    }

    /**
     * L'action d'abandon
     */
    cancel() {
        this.setState({
            task: this.props.task,
            loaded: true
        });
            window.signalement.debug("Cancel and close state: ", this.state);
            window.signalement.debug("Cancel and close props: ", this.props);
        if(this.state.task != null && this.state.task.asset.uuid) {
            this.props.requestClosing();
        } else {
            this.resetForm();
            this.props.toggleControl();
        }
    }


    /**
     * Upload de tous les fichiers en attente
     */
    uploadPendingAttachments = async (taskUuid) => {
        const uploadPromises = this.state.pendingAttachments.map(attachment => {
            const formData = new FormData();
            formData.append('file', attachment.file);

            const url = "/signalement/reporting/" + taskUuid + "/upload";

            return fetch(url, {
                method: 'POST',
                body: formData
            }).then(response => {
                if (!response.ok) {
                    throw new Error(`Upload failed: ${response.statusText}`);
                }
                return response.json();
            });
        });

        try {
            const uploadedAttachments = await Promise.all(uploadPromises);
            window.signalement.debug("Tous les fichiers ont été uploadés avec succès", uploadedAttachments);
            return uploadedAttachments;
        } catch (error) {
            window.signalement.debug("Erreur lors de l'upload des fichiers", error);
            throw error;
        }
    }



    /**
     * L'action de création avec upload des fichiers
     */
    async create() {
        if((this.state.isContextVisible || (!this.state.isContextVisible && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER")) && !this.props.creating)
        {
            const taskUuid = this.props.task?.asset?.uuid;
            window.signalement.debug("Create and close:", taskUuid);

            try {
                // 1. D'abord uploader tous les fichiers en attente si il y en a
                if (this.state.pendingAttachments.length > 0) {
                    window.signalement.debug("Upload des fichiers en cours...");
                    await this.uploadPendingAttachments(taskUuid);
                }

                // 2. Ensuite créer la tâche
                if(this.state.currentLayer !== null) {
                    const layerTaskData = {...this.state.task,  asset: {...this.state.task.asset, uuid: taskUuid,
                            geographicType: this.state.currentLayer.geographicType, contextDescription: this.state.currentLayer}, form: this.props.task?.form}
                    this.props.createTask(layerTaskData);
                } else {
                    const themaTaskData = {...this.state.task, asset: {...this.state.task.asset, uuid: taskUuid},
                        form: this.props.task?.form, assignee: "", functionalId: this.props.task?.functionalId,
                        creationDate: this.props.task.creationDate, updatedDate: this.props.task.updatedDate,
                        initiator: this.props.task?.initiator, status: this.props.task.status
                    }
                    this.props.createTask(themaTaskData);
                }

                this.props.toggleControl();

                this.setState({
                    task: null,
                    loaded: false,
                    errorAttachment: "",
                    errorFields: {},
                    selectedContextValue: "",
                    isContextVisible: false,
                    themaSelected: false,
                    pendingAttachments: [] // Vider les fichiers en attente
                });

            } catch (error) {
                window.signalement.debug("Erreur lors de la création de la tâche", error);
                this.setState({
                    errorAttachment: "signalement.task.create.error"
                });
            }

            window.signalement.debug("Create and close panel END state: ", this.state);
            window.signalement.debug("Create and close panel END props: ", this.props);
        }

    }

/**
 * Fonction de vérification des champs du custom form pour déterminer si les champs required sont bien remplis
*/
checkRequiredFields() {
    return this.props.task?.form?.sections?.every(section =>
        section.fields.every(field => {
            // Si un champ est "required", il doit avoir des valeurs non vides
            if (field.definition.required) {
                return field.values.length > 0;
            }
            return true; // Si ce n'est pas "required", on considère le champ comme valide
            })
        ) ?? true; // Retourne true si form est null (pas de champs à valider);
    }

/**
 * Fonction de vérification de la validité du signalement avant soumission
*/
checkTaskValid() {
    if (!this.state.task?.asset?.description || !this.state.task?.asset?.localisation || Object.keys(this.state.errorFields).length !== 0) {
        return false;
    }

    return !(!!this.state.task?.asset?.localisation &&
        !!this.state.task?.asset?.description &&
        ((!this.state.isContextVisible && this.state.selectedContextValue !== "") ||
            (this.state.isContextVisible && this.state.selectedContextValue === ""
                && this.props.task.asset.contextDescription.contextType ==="LAYER"))) &&
                this.checkRequiredFields()
    }
}
