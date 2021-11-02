import React from "react";
import {PropTypes} from 'prop-types';
import {
    Col,
    Form,
    Glyphicon,
    Grid,
    Row
} from 'react-bootstrap';
import {SignalementTaskViewer} from "@js/extension/components/SignalementTaskViewer";
import {
    viewType
} from "@js/extension/actions/signalement-management-action";


/**
 * Composant englobant le viewer SignalementTaskViewer
 * Contient un header et des boutons de navigation pour controler le viewer
 */
export class SignalementViewer extends React.Component {
    static propTypes = {
        closeViewer: PropTypes.func,
        features: PropTypes.array,
        clickedPoint: PropTypes.object,
        task: PropTypes.object,
        user: PropTypes.object,
        viewType: PropTypes.string,
        errorTask: PropTypes.object,
        getTask: PropTypes.func,
        downloadAttachment: PropTypes.func,
        claimTask: PropTypes.func,
        updateTask: PropTypes.func,
        updateDoAction: PropTypes.func,
        closeIdentify: PropTypes.func
    }

    static defaultProps = {
        closeViewer: () => {},
        features: [],
        clickedPoint: {
            lng: 0.0,
            lat: 0.0
        },
        task: null,
        user: {},
        viewType: "",
        errorTask: null,
        getTask: () => {},
        downloadAttachment: () => {},
        claimTask: () => {},
        updateTask: () => {},
        updateDoAction: () => {},
        closeIdentify: () => {}
    }

    constructor(props) {
        super(props);
        this.state = {
            index: 0
        }
    }

    render() {
        return (
            <div className="signalement-management-viewer">
                <Form>
                    {this.renderHeader()}
                    {this.renderCoordinates()}
                    {this.props.viewType===viewType.MY && this.renderPluiRequestsNavigation()}
                    <SignalementTaskViewer features={this.props.features}
                                           index={this.state.index}
                                           closeViewer={this.props.closeViewer}
                                           task={this.props.task}
                                           user={this.props.user}
                                           viewType={this.props.viewType}
                                           errorTask={this.props.errorTask}
                                           getTask={this.props.getTask}
                                           downloadAttachment={this.props.downloadAttachment}
                                           claimTask={this.props.claimTask}
                                           updateTask={this.props.updateTask}
                                           updateDoAction={this.props.updateDoAction}
                                           closeIdentify={this.props.closeIdentify}
                    />
                </Form>
            </div>
        )
    }

    /**
     * La rendition de l'entête
     */
    renderHeader() {
        return (
            <Grid fluid className="ms-header ms-primary" style={{ width: '100%', boxShadow: 'none'}}>
                <Row>
                    <Col xs={2}>
                        <button className="square-button bg-primary no-border no-events btn btn-primary">
                            <Glyphicon glyph="map-marker"/>
                        </button>
                    </Col>
                    <Col xs={8}>
                    </Col>
                    <Col xs={2}>
                        <button className="square-button no-border bg-primary btn btn-primary" onClick={() => this.close()} >
                            <Glyphicon glyph="1-close"/>
                        </button>
                    </Col>
                </Row>
            </Grid>
        );
    }

    /**
     * Permet la rendition de la barre d'affiche de la coordonnée cliquée
     * @returns {JSX.Element}
     */
    renderCoordinates() {
        return (
            <div className="coordinates-text">
                <span>
                    <Glyphicon glyph="map-marker"/>
                </span>
                <span> Lat: {this.props.clickedPoint.lat.toLocaleString(undefined, {maximumFractionDigits:3})}</span>
                <span>  -  Long: {this.props.clickedPoint.lng.toLocaleString(undefined, {maximumFractionDigits:3})}</span>
            </div>
        )
    }

    /**
     * Affichage des boutons de navigations (précédent et suivant)
     * permettant de naviguer entre les demandes plui
     * @return {Element}
     */
    renderPluiRequestsNavigation() {
        return(
            <div className="button-navigation">
                <button
                    className="square-button-md btn btn-primary"
                    disabled={this.state.index === 0}
                    onClick={this.handleClickButtonDisplayTaskBefore}>
                    <Glyphicon glyph="glyphicon glyphicon-chevron-left" />
                </button>
                <h4>
                    {this.state.index + 1} / {this.props.features?.length}
                </h4>
                <button
                    className="square-button-md btn btn-primary"
                    disabled={this.state.index === this.props.features?.length - 1}
                    onClick={this.handleClickButtonDisplayTaskAfter}>
                    <Glyphicon glyph="glyphicon glyphicon-chevron-right" />
                </button>
            </div>
        )
    }

    /**
     * Action pour afficher la demande plui suivante
     */
    handleClickButtonDisplayTaskAfter = () => {
        this.setState({index : ++this.state.index});
    }

    /**
     * Action pour afficher la demande plui précédente
     */
    handleClickButtonDisplayTaskBefore = () => {
        this.setState({index : --this.state.index});
    }

    /**
     * Permet de fermer le viewer
     */
    close = () => {
        this.props.closeViewer();
    }
}
