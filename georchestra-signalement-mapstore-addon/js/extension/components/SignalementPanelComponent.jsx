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
        toggleControl: () => {}
    };

    constructor(props) {
        super(props);
        this.state = {
            errorAttachment: "",
            errorFields: {},
            themaSelected: false,
            selectedContextValue: "",
            // selectedContextValue: props.task?.asset?.contextDescription?.label ?? props.contextThemas?.[0]?.label,
            isContextVisible: false
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
        // Tout est-il initialisé ?
        this.state.initialized = this.props.contextLayers !== null && this.props.contextThemas !== null &&
            this.props.attachmentConfiguration !== null && this.props.user !== null;
        // on récupère la current layer si elle existe
        this.state.currentLayer = this.props.currentLayer;
        window.signalement.debug("sig didUpdate props...", this.props);
        window.signalement.debug("sig didUpdate state...", this.state);

        if( this.props.task !== null && this.state.task === null  && this.props.status === status.TASK_INITIALIZED ){
            // on a une tâche dans les props, pas dans le state et on est à "tâche initialisée"
            window.signalement.debug("sig draft created");
            window.signalement.debug("sig draft created props ", this.props);
            window.signalement.debug("sig draft created state ", this.state);
            this.state.task = this.props.task;
            this.state.loaded = true;
            this.setState(this.state);
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
            this.state.task = null;
            this.state.loaded = false;
            this.state.errorAttachment = "";
            this.setState(this.state);
            this.props.stopDrawingSupport();
            this.props.toggleControl();
            this.state.errorFields = {};
        }
        window.signalement.debug(this.state);


        //     Quand on passe d'un signalmenent par couche à un signalement par thématique
        const isLastDraftLayer = this.props.contextLayers?.length > 0 && this.props?.contextLayers?.find(layer => layer.name === this.props?.task?.asset?.contextDescription?.name);
        if((this.props.status === status.TASK_INITIALIZED)  && !this.props.currentLayer && !this.state.currentLayer && isLastDraftLayer) {
            const initContext = this.props.contextThemas[0];
            this.props.createDraft(initContext, this.props.task?.asset?.uuid);

            this.state.isContextVisible = this.props.contextThemas.length === 1;
            this.state.selectedContextValue = "";
            this.state.themaSelected = false;
            this.setState(this.state);
        }
        //     Quand on passe d'un signalmenent par thématique à un signalement par couche
        if((this.props.status === status.TASK_INITIALIZED)  && this.props.currentLayer && this.state.currentLayer && !isLastDraftLayer) {
            const initContext = this.props.contextThemas[0];
            // this.props.createDraft(initContext, this.props.task?.asset?.uuid);
            this.props.createDraft(this.props.currentLayer, this.props.task?.asset?.uuid);
            // this.state.themaSelected = true;
            this.isContextVisible = true;
            this.setState(this.state);
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
                    // Contrôle avancé : Si une interaction utilisateur a clairement modifié la valeur,
                    // nous ne mettons pas à jour celle-ci depuis les props.
                    if (!prevState.themaSelected) {
                        return {
                            selectedContextValue: "",
                            // selectedContextValue: newPropValue,
                        };
                    }
                    // Sinon, on garde l'état tel qu'il est
                    return null;
                });
            }


        }

    }

    /**
     * Changement de la description
     *
     * @param {*} e l'événement
     */
    handleDescriptionChange = (e) => {
        this.state.task.asset.description = e.target.value;
        this.setState(this.state);
    }

    /**
     * Changement du contexte
     * @param {*} e l'événement
     */
    handleContextChange = (e) => {
        const newValue = e.target.value;
        // this.setState({...this.state, selectedContextValue: newValue });

        const contextDescriptions = this.props.contextThemas.filter(thema => thema.name === newValue);
        if( contextDescriptions != null && contextDescriptions.length > 0) {

            // this.setState({themaSelected: true });

            // this.state.task = this.props.task;
            // this.state.loaded = true;
            // this.setState(this.state);

            // this.state.task.asset.contextDescription = contextDescriptions[0];
            // this.state.task.asset.geographicType = contextDescriptions[0].geographicType;
            // this.state.task = {asset : { contextDescription: contextDescriptions[0],
            //         geographicType: contextDescriptions[0].geographicType}};
            window.signalement.debug("sig context change state before ", this.state);
            window.signalement.debug("sig context change props before ", this.props);
            const newTask = {
                asset: {
                    contextDescription: contextDescriptions[0],
                    geographicType: contextDescriptions[0].geographicType,
                },
            };

            this.setState({
                selectedContextValue: newValue,
                isContextVisible: true,
                themaSelected: true,
                task: newTask,
            });

            this.props.clearDrawn();
            this.props.createDraft(contextDescriptions[0], this.props.task?.asset?.uuid);
            window.signalement.debug("sig context change state after ", this.state);
            window.signalement.debug("sig context change props after ", this.props);

        }
        // this.setState(this.state);
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
                    // il n'y a pas de tâche dans les props et on a rien fait ou a vient de créer un tâche avec succès
                    // on lance la création d'une tâche draft avec le context par défaut
                    window.signalement.debug("sig create draft 0");
                    window.signalement.debug("sig create draft 0 props ", this.props);
                    window.signalement.debug("sig create draft 0 state ", this.state);
                    const initContext = this.props.currentLayer ? this.props.currentLayer : this.props.contextThemas[0];
                    this.props.createDraft(initContext, undefined);

                    this.state.themaSelected = false;
                    this.setState(this.state);
                }
                if(this.props.status === status.TASK_UNLOADED  && !this.props.currentLayer) {
                    window.signalement.debug("sig create draft 1");
                    this.props.createDraft(this.props.contextThemas[0], undefined);
                }
                if((this.props.status === status.TASK_INITIALIZED || this.props.status === status.TASK_UNLOADED)  && this.props.currentLayer && !this.state.currentLayer) {
                    window.signalement.debug("sig create draft 2");
                    this.props.createDraft(this.props.currentLayer, undefined);
                    this.state.currentLayer = this.props.currentLayer
                    this.setState(this.state);
                }

            }
            if (this.props.contextThemas.length <=1) {
                this.state.themaSelected = true;
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
                        <legend><Message msgId="signalement.reporting.thema"/></legend>
                        <FormGroup controlId="signalement.thema">
                            <FormControl componentClass="select"
                                         value={this.state.selectedContextValue}
                                         onChange={this.handleContextChange}
                                // defaultValue={this.props.task?.asset?.contextDescription?.label ?? this.props.contextThemas?.[0]?.name} // Valeur par défaut
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
                    <legend><Message msgId="signalement.description"/></legend>
                    <FormGroup controlId="signalement.description">
                        <FormControl componentClass="textarea"
                                     defaultValue={this.state.task?.asset?.description}
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
        return (
            <div>
                <fieldset>
                    <legend><Message msgId="signalement.attachment.files"/></legend>
                    <FormGroup controlId="formControlsFile">
                        <FormControl type="file" name="file"
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

                    <ReactIntl.FormattedMessage id="signalement.localization.geolocate.hover">
                        {(message) =>
                            <Button bsStyle="primary"
                                    bsSize="sm"
                                    className={((!this.state.isContextVisible && this.state.selectedContextValue !== "") || (this.state.isContextVisible && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER"))? "validation-button boutonHover": "validation-button"}
                                    // className={(!this.state.themaSelected)? "validation-button boutonHover": "validation-button"}
                                    data-message={message}
                                    // disabled={!this.state.themaSelected && !this.state.currentLayer}
                                    disabled={(!this.state.isContextVisible && this.state.selectedContextValue !== "") || (this.state.isContextVisible && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER")}
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
    handleFieldChange = (e)=>{

        const idSection= e.target.id.split(".")[1];
        const idField= e.target.id.split(".")[3];

        let field = this.props.task.form.sections[idSection].fields[idField];
        // let field = this.state.task.form.sections[idSection].fields[idField];

        // valider le changement après modification du champs
        // pour s'assurer qu'il est en format correct avec le validateur de chaque champs
        let errorFields = {};
        if(field.definition.validators[0]){
            errorFields = this.getErrorFields(e.target.value , field.definition.validators[0], e.target.id);
        }

        // verifier si la liste des erreurs est vide sinon on affecte le changement des valeurs
        if (errorFields && Object.keys(errorFields).length === 0 && errorFields.constructor === Object) {

            if(field.definition.type === "BOOLEAN"){
                field.values = [e.target.checked] ;
            }else{
                field.values = [e.target.value] ;
            }

        }

        this.state.errorFields = errorFields;
        this.setState(this.state);

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
                    this.setState({errorFields});
                }
                break;
            case 'NEGATIVE':
                if(value > 0) {
                    errorFields[id] = 'signalement.field.error.negative';
                    this.setState({errorFields});
                }
                break;
            case 'MAXLENGTH':
                if(value.length > parseInt(validator.attribute, 10)) {
                    errorFields[id] = `maximum de caractères : ${validator.attribute}`;
                    this.setState({errorFields});
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

        if (this.props.attachments.length + 1 > this.props.attachmentConfiguration.maxCount) {
            errorAttachment = `Vous ne pouvez pas ajouter plus de " : ${this.props.attachmentConfiguration.maxCount} fichiers`
        }

        if (errorAttachment) {
            this.setState({errorAttachment});
            return false;
        }
        return true;
    }

    /**
     * Action pour ajouter une pièce jointe
     *
     * @param {*} e l'événement
     */
    fileAddedHandler(e) {
        //les differents test avant d'uploader le fichier (type, taille)
        var attachment = {file: e.target.files[0], uuid: this.state.task.asset.uuid}

        const isValid = this.validateAttachment(attachment);

        if (isValid) {
            this.setState({errorAttachment: ""});
            // uploader le fichier
            this.props.addAttachment(attachment);
        }

    }


    /**
     * Action pour supprimer une pièce jointe
     *
     * @param id du fichier à supprimer
     * @param index du fichier dans la liste
     */
    fileDeleteHandler(id, index) {
        const attachment = {id: id, uuid: this.state.task.asset.uuid, index: index};
        this.props.removeAttachment(attachment);
    }

    /**
     * L'action d'abandon
     */
    cancel() {
        this.state.task = this.props.task;
        this.state.loaded = true;
        this.setState(this.state);
            window.signalement.debug("Cancel and close state: ", this.state);
            window.signalement.debug("Cancel and close props: ", this.props);
        if(this.state.task != null && this.state.task.asset.uuid) {
            this.props.requestClosing();
        } else {
            this.props.toggleControl();
        }
    }

    /**
     * L'action de création
     */
    create() {
        // if( this.state.task != null && this.state.task.asset.uuid && this.state.task.asset.contextDescription && !this.props.creating)
        if((this.state.isContextVisible || (!this.state.isContextVisible && this.state.selectedContextValue === "" && this.props.task.asset.contextDescription.contextType ==="LAYER")) && !this.props.creating)
        {
            window.signalement.debug("Create and close:"+this.state.task.asset.uuid);
            if(this.state.currentLayer !== null) {
                const layerTaskData = {...this.state.task,  asset: {...this.state.task.asset, uuid: this.props.task?.asset?.uuid,
                        geographicType: this.state.currentLayer.geographicType, contextDescription: this.state.currentLayer}, form: this.props.task?.form}
                this.props.createTask(layerTaskData);
            } else {
                const themaTaskData = {...this.state.task, asset: {...this.state.task.asset, uuid: this.props.task?.asset?.uuid},
                    form: this.props.task?.form, assignee: "", functionalId: this.props.task?.functionalId,
                    creationDate: this.props.task.creationDate, updatedDate: this.props.task.updatedDate,
                    initiator: this.props.task?.initiator, status: this.props.task.status
                }
                this.props.createTask(themaTaskData);
            }
            this.props.toggleControl();
            this.props.cancelDraft(this.props.task?.asset?.uuid);
            this.state.selectedContextValue = "";
            this.state.isContextVisible = false;
            this.state.themaSelected = false;
            this.setState(this.state);
        }
    }
}
