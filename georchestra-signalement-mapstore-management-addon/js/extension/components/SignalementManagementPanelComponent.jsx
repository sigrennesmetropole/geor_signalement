import React, {useState, useEffect} from 'react';
import Dock from 'react-dock';
import {pick} from 'lodash';
import assign from 'object-assign';
import ContainerDimensions from 'react-container-dimensions';
import {connect} from 'react-redux';
import {PropTypes} from 'prop-types';
import {Grid, Col, Row, Glyphicon, Button, Form, FormControl, ControlLabel, Tooltip, FormGroup} from 'react-bootstrap';
import Select from 'react-select';
import Message from '@mapstore/components/I18N/Message';
import { setViewer, getViewer } from '@mapstore/utils/MapInfoUtils';
import {closeIdentify} from '@mapstore/actions/mapInfo';
import {SignalementTaskViewer} from './SignalementTaskViewer';
import {
    changeTypeView,
    closeTabularView,
    getMe,
    getTask,
    loadContexts,
    openTabularView,
    downloadAttachment,
    claimTask,
    updateTask,
    updateDoAction,
    status,
    viewType
} from '../actions/signalement-management-action';
import {
    signalementManagementContextsSelector,
    signalementManagementMeSelector, signalementManagementTaskSelector
} from "../selectors/signalement-management-selector";

export class SignalementManagementPanelComponent extends React.Component {
	 static propTypes = {
        id: PropTypes.string,
        status: PropTypes.string,
        // config
        panelClassName: PropTypes.string,
        dockProps: PropTypes.object,
        width: PropTypes.number,
        // data
		contexts: PropTypes.array,
        user: PropTypes.object,
        error: PropTypes.object,
        tabularViewOpen: PropTypes.bool,
        viewType: PropTypes.string,
        // redux
		initSignalementManagement: PropTypes.func,
        loadContexts: PropTypes.func,
        getMe: PropTypes.func,
        openTabularView: PropTypes.func,
        closeTabularView: PropTypes.func,
        changeTypeView: PropTypes.func,
        selectNode: PropTypes.func
    };

    static defaultProps = {
        id: "signalement-panel",
        status: status.NOOP,
        // config
        panelClassName: "signalement-management-panel",
        // side panel properties
        width: 660,
        dockProps: {
            dimMode: "none",
            size: 0.2,
            fluid: true,
            position: "top",
            zIndex: 1030,
            width:"25%"
        },
        dockStyle: {
            zIndex: 1030,
        },
        // data
		contexts: null,
        user: null,
        error: null,
        tabularViewOpen: false,
        viewType: null,
        // misc
		initSignalementManagement: ()=>{},
        loadContexts: ()=>{},
        getMe: ()=>{},
        openTabularView: ()=>{},
        closeTabularView: ()=>{},
        changeTypeView: ()=>{},
        selectNode: ()=>{}
    };

    constructor(props) {
        super(props);
        console.log("sigm constructor...");
        this.handleContextChange = this.handleContextChange.bind(this);
		this.props.initSignalementManagement(this.props.backendurl);
        this.state = {
            viewAdmin : false
        }
        console.log("sigm constructor done.");
    }

    componentWillMount() {
        console.log("sigm willmount...");
        this.setState({initialized: false, currentContext: null});
        this.props.loadContexts();
        this.props.getMe();
        const Connected = connect((state) => ({
            task: signalementManagementTaskSelector(state),
            user: signalementManagementMeSelector(state),
            viewType: state.signalementManagement.viewType,
            errorTask: state.signalementManagement.errorTask,
            // debug
            state : state
        }), {
            getTask: getTask,
            downloadAttachment: downloadAttachment,
            claimTask: claimTask,
            updateTask: updateTask,
            updateDoAction: updateDoAction,
            closeIdentify: closeIdentify
        })(SignalementTaskViewer);
        setViewer("TaskViewer", Connected);
        console.log("sigm willmount done.");
    }

    componentDidUpdate(prevProps, prevState) {
        console.log("sigm didUpdate...");
        console.log(this.props);
        this.state.initialized = this.props.contexts !== null && 
            this.props.contexts.length > 0 && this.props.user !== null; 

        if( this.state.initialized && this.state.currentContext === null ) {
            this.state.currentContext = this.props.contexts[0];
            this.props.changeTypeView(viewType.MY,this.state.currentContext);
        }
        console.log("sigm viewer=>" + getViewer('TaskViewer'));
        console.log("sigm didUpdate done.");
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
        console.log("sigm render");

        if( this.state.initialized) {
            return (<ContainerDimensions>
                        { ({ width }) =>
                            <span>
                                <span className="ms-signalement-management-panel ">
                                    <div className={this.props.panelClassName}>
                                        {this.renderHeader()}
                                        {
                                            !this.state.initialized ? 
                                                this.renderLoading() : 
                                                this.renderForm()
                                        }
                                    </div>
                                </span>
                            </span>
                        }
                    </ContainerDimensions>
                );
        } else {
            return null;
        }
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
                    <Grid fluid className="ms-header" style={this.props.styling || this.props.mode !== "list" ? { width: '100%', boxShadow: 'none'} : { width: '100%' }}>
                        <Row>
                            <Col xs={4}>
                                <Button key="signalement-admin-view" bsStyle={this.props.tabularViewOpen ? 'success' : 'primary'} 
                                    className="square-button-md" onClick={()=> this.toggleTabularView()}>
                                    <Glyphicon glyph="features-grid" />
                                </Button>
                            </Col>
                            <Col xs={4}>
                                <Button key="signalement-my-view" bsStyle={(this.props.state.layers.selected && this.props.state.layers.selected.includes("signalements")) ? 'success' : 'primary'}  className="square-button-md"
                                        onClick={ () => this.selectLayerSign()}>
                                    <Glyphicon glyph="exclamation-sign" />
                                </Button>
                            </Col>
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
                <Col xs={4}>
                    <Button key="signalement-tabular-view" bsStyle={(this.state.viewAdmin === true) ? 'success' : 'primary'} className="square-button-md"
                            onClick={() => this.displayAdminView()}>
                        <Glyphicon glyph="cog" />
                    </Button>
                </Col>
            )
        }

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
};
