import React from 'react';
import ContainerDimensions from 'react-container-dimensions';
import {PropTypes} from 'prop-types';
import {Grid, Col, Row, Glyphicon, Button, Form, FormControl, Label, Tooltip, FormGroup, OverlayTrigger} from 'react-bootstrap';
import Message from '@mapstore/components/I18N/Message';
import {
    status,
    viewType
} from '../actions/signalement-management-action';
import {SignalementViewer} from "@js/extension/components/SignalementViewer";
import ResponsivePanel from "@mapstore/components/misc/panels/ResponsivePanel";
import {SIGNALEMENT_TASK_VIEWER_WIDTH} from "@js/extension/constants/signalement-management-constants";

export class SignalementManagementPanelComponent extends React.Component {
	 static propTypes = {
        id: PropTypes.string,
        status: PropTypes.string,
        active: PropTypes.bool,
        // config
        panelClassName: PropTypes.string,
        dockProps: PropTypes.object,
        dockStyle: PropTypes.object,
        width: PropTypes.number,
        // data
		contexts: PropTypes.array,
        user: PropTypes.object,
        errorTask: PropTypes.object,
        task: PropTypes.object,
        error: PropTypes.object,
        actionInProgress: PropTypes.bool,
        tabularViewOpen: PropTypes.bool,
        viewType: PropTypes.string,
        features: PropTypes.array,
        clickedPoint: PropTypes.object,
        // redux
		initSignalementManagement: PropTypes.func,
        loadContexts: PropTypes.func,
        getMe: PropTypes.func,
        openTabularView: PropTypes.func,
        closeTabularView: PropTypes.func,
        changeTypeView: PropTypes.func,
        selectNode: PropTypes.func,
        loadTaskViewer: PropTypes.func,
        closeViewer: PropTypes.func
    };

    static defaultProps = {
        id: "signalement-panel",
        status: status.NOOP,
        active: false,
        // config
        panelClassName: "signalement-management-panel",
        closeGlyph: "1-close",
        // side panel properties
        width: SIGNALEMENT_TASK_VIEWER_WIDTH,
        dockProps: {
            dimMode: "none",
            size: 0.3,
            fluid: true,
            position: "right",
            zIndex: 1030,
            width:"25%",
            resizable: false
        },
        dockStyle: {
            zIndex: 1030,
        },
        // data
		contexts: null,
        user: null,
        error: null,
        actionInProgress: false,
        tabularViewOpen: false,
        viewType: null,
        features: [],
        clickedPoint: {lat: 0.0, lng: 0.0},
        errorTask: null,
        task: null,
        // misc
		initSignalementManagement: ()=>{},
        loadContexts: ()=>{},
        getMe: ()=>{},
        openTabularView: ()=>{},
        closeTabularView: ()=>{},
        changeTypeView: ()=>{},
        selectNode: ()=>{},
        getTask: ()=>{}
    };

    constructor(props) {
        super(props);
        
        if (this.props.debug_signalement_management) {
            window.signalementMgmt.debug = (...args) => { console.log(...args) };
        }

        window.signalementMgmt.debug("sigm constructor...");
        this.handleContextChange = this.handleContextChange.bind(this);
		this.props.initSignalementManagement(this.props.backendurl);
        this.state = {
            viewAdmin : false
        }
        window.signalementMgmt.debug("sigm constructor done.");
    }

    componentWillMount() {
        window.signalementMgmt.debug("sigm willmount...");
        this.setState({initialized: false, currentContext: null});
        this.props.loadContexts();
        this.props.getMe();
        window.signalementMgmt.debug("sigm willmount done.");
    }

    componentDidUpdate(prevProps, prevState) {
        window.signalementMgmt.debug("sigm didUpdate...");
        window.signalementMgmt.debug(this.props);
        this.state.initialized = this.props.contexts !== null && 
            this.props.contexts.length > 0 && this.props.user !== null; 

        if( this.state.initialized && this.state.currentContext === null ) {
            this.state.currentContext = this.props.contexts[0];
            this.props.changeTypeView(viewType.MY,this.state.currentContext);
        }
        window.signalementMgmt.debug("sigm didUpdate done.");
    }

    /**
     * Changement du contexte
     * @param {*} e l'événement
     */
    handleContextChange(e) {
        const contextDescriptions = this.props.contexts.filter(context => context.name === e.target.value);
        if( contextDescriptions != null && contextDescriptions.length >0){
            if( this.state.currentContext !== contextDescriptions[0] ) {
                this.state.currentContext = contextDescriptions[0];
                this.props.changeTypeView(this.props.viewType,this.state.currentContext);
            }
        }
        this.setState(this.state);
    }
	
    render() {
        window.signalementMgmt.debug("sigm render", this.state.initialized);

        if( this.state.initialized) {
            return [<ContainerDimensions key="context-panel">
                        { ({ width }) =>
                            <span>
                                <span className="ms-signalement-management-panel ">
                                    <div className={this.props.panelClassName}>
                                        {this.renderHeader()}
                                        <div className="signalement-panel-body">
                                            {
                                                !this.state.initialized ?
                                                    this.renderLoading() :
                                                    this.renderForm()
                                            }
                                        </div>
                                    </div>
                                </span>
                            </span>
                        }
                    </ContainerDimensions>,
                    <ResponsivePanel key="viewer-panel"
                            containerId="ms-signalement-management-viewer"
                            containerStyle={this.props.dockStyle}
                            style={this.props.dockStyle}
                            dockStyle={this.props.dockStyle}
                            open={this.props.active}
                            containerClassName={this.props.panelClassName}
                            size={this.props.width}
                            position="right"
                            bsStyle="primary"
                            glyph="map-marker"
                            onClose={() => this.props.closeViewer()}
                        >
                            <div>
                                {
                                    !this.state.initialized &&
                                    this.renderLoading()
                                }
                                {
                                    this.state.initialized &&
                                    this.renderViewer()
                                }
                            </div>
                    </ResponsivePanel>
                ];
        } else {
            return null;
        }
    }

    /**
     * La rendition du viewer parent
     */
    renderViewer() {
        if (this.props.active) {
            return (
                <SignalementViewer features={this.props.features}
                                   clickedPoint={this.props.clickedPoint}
                                   closeViewer={this.props.closeViewer}
                                   task={this.props.task}
                                   user={this.props.user}
                                   viewType={this.props.viewType}
                                   errorTask={this.props.errorTask}
                                   actionInProgress={this.props.actionInProgress}
                                   getTask={this.props.getTask}
                                   downloadAttachment={this.props.downloadAttachment}
                                   claimTask={this.props.claimTask}
                                   updateTask={this.props.updateTask}
                                   updateDoAction={this.props.updateDoAction}
                                   closeIdentify={this.props.closeIdentify}
                />
            )
        }
        return null;
    }

    /**
     * Rendition du message de chargement en attendant la nouvelle tâche draft
     */
    renderLoading() {
        return (
            <div><Message msgId="signalement-management.loading"/></div>
        );
    }

    /**
     * La rendition du formulaire
     */
    renderForm() {
        return (
            <div className="signalement-management-form">
                <Form>
                    <FormGroup controlId="signalement.thema">
                        <Label><Message msgId="signalement-management.select.label"/></Label>
                        <FormControl componentClass="select"
                            value={this.state.currentContext.name}
                            onChange={this.handleContextChange} >
                            {
                                (this.props.contexts || []).map((context) => {
                                    return <option key={context.name} value={context.name}>{context.label}</option>
                                })
                            }
                        </FormControl>
                    </FormGroup>
                    <Grid fluid className="ms-header signalement-action-buttons" style={this.props.styling || this.props.mode !== "list" ? { width: '100%', boxShadow: 'none'} : { width: '100%' }}>
                        <Row>
                            <OverlayTrigger
                                key="tabular-view-overlay"
                                placement="bottom"
                                overlay={
                                    <Tooltip id={`tooltip-tabular-view`}>
                                        <Message msgId="signalement-management.tooltip.tabular_view"/>
                                    </Tooltip>
                                }
                            >
                                <Button key="signalement-admin-view" bsStyle={this.props.tabularViewOpen ? 'success' : 'primary'}
                                    className="square-button-md" onClick={()=> this.toggleTabularView()}>
                                    <Glyphicon glyph="features-grid" />
                                </Button>
                            </OverlayTrigger>
                            <OverlayTrigger
                                key="map-picker-overlay"
                                placement="bottom"
                                overlay={
                                    <Tooltip id={`tooltip-map-picker`}>
                                        <Message msgId="signalement-management.tooltip.map_picker"/>
                                    </Tooltip>
                                }
                            >
                                <Button key="signalement-my-view" bsStyle={(this.props.state.layers.selected && this.props.state.layers.selected.includes("signalements")) ? 'success' : 'primary'}  className="square-button-md"
                                        onClick={ () => this.selectLayerSign()}>
                                    <Glyphicon glyph="exclamation-sign" />
                                </Button>
                            </OverlayTrigger>
                            {this.renderButtonAdmin()}
                        </Row>
                    </Grid>
                </Form>
            </div>
        );
    }

    /**
     * La rendition du button admin
     */
    renderButtonAdmin(){
        if(this.props.user.roles.find(role => role === "ADMIN")){
            return(
                <Button key="signalement-tabular-view" bsStyle={(this.state.viewAdmin === true) ? 'success' : 'primary'} className="square-button-md"
                        onClick={() => this.displayAdminView()}>
                    <Glyphicon glyph="cog" />
                </Button>
            )
        }

    }

    /**
     * La rendition de l'entête 
     */
    renderHeader() {
        return (
            <Grid fluid className="ms-header" style={this.props.styling || this.props.mode !== "list" ? { width: '100%', boxShadow: 'none'} : { width: '100%' }}>
                <Row className="ms-header-title">
                    <Col xs={2}>
                        <Button className="square-button no-events">
                            <Glyphicon glyph="exclamation-sign"/>
                        </Button>
                    </Col>
                    <Col xs={10}>
                        <h4><Message msgId="signalement-management.title"/></h4>
                        {this.renderMessage()}
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

    toggleTabularView(){
        !this.props.tabularViewOpen ? this.props.openTabularView(this.state.currentContext) : this.props.closeTabularView();
    }

    displayAdminView(){
        this.state.viewAdmin = !this.state.viewAdmin;
        if(this.state.viewAdmin === true){
            this.props.changeTypeView(viewType.ADMIN, this.state.currentContext);
        }else{
            this.props.changeTypeView(viewType.MY, this.state.currentContext);
        }

    }

    selectLayerSign(){
        this.props.selectNode('signalements','layer', false);
    }
}
