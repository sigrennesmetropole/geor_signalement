import React from 'react';
import {PropTypes} from 'prop-types';
import {ControlLabel, Form, FormControl, FormGroup, Button, Col} from "react-bootstrap";
import Message from '@mapstore/components/I18N/Message';
import LoadingSpinner from '@mapstore/components/misc/LoadingSpinner';

export class SignalementTaskViewer extends React.Component {


    static propTypes = {
        features: PropTypes.array,
        task: PropTypes.object,
        user: PropTypes.object,
        viewType: PropTypes.string,
        errorTask: PropTypes.object,
        getTask: PropTypes.func,
        downloadAttachment: PropTypes.func,
        claimTask: PropTypes.func,
        updateTask: PropTypes.func,
        updateDoAction: PropTypes.func,
        closeIdentify: PropTypes.func,
    };

    static defaultProps = {
        features: [],
        task: null,
        user: {},
        viewType: "",
        errorTask: null,
        getTask: () => {
        },
        downloadAttachment: () => {
        },
        claimTask: () => {
        },
        updateTask: () => {
        },
        updateDoAction: () => {
        }

    }

    constructor(props) {
        super(props);
        this.state= {
            errorFields: {},
            index: this.props.index,
            action: null
        }
    }

    componentWillMount() {
        window.signalementMgmt.debug("sigm task  get");
        let index = this.state.index;
        let id = this.props.features.length > 0 ? this.props.features[index]?.properties?.id : null;
        if(id){
            window.signalementMgmt.debug("sigm task  get");
            this.props.getTask(id);
            this.setState({task: null});
        }

    }

    componentDidUpdate(prevProps, prevState, snapshot) {
        window.signalementMgmt.debug("sigm task  recupered before", );
        if (this.props.task !== null && this.state.task === null) {
            window.signalementMgmt.debug("sigm task  recupered");
            this.state.task = this.props.task;
            this.setState(this.state);
        }

        if(this.state.task !== null){
            this.state.task = this.props.task;
        }

        if (this.props.index !== prevState.index) {
            let id = this.props.features.length > 0 ? this.props.features[this.props.index]?.properties?.id : null;
            if(id){
                this.props.getTask(id);
                this.setState({task: null, index: this.props.index});
            }
        }

    }


    render() {
        if (this.state.task) {
            return (
                <Form model={this.state.task}>
                    {this.renderMessage()}
                    {this.renderSignalementManagementInfo()}
                    {this.renderSignalementManagementForm()}
                    {this.renderSignalementManagementClaim()}
                    {this.renderSignalementManagementActions()}
                    {this.renderSignalementManagementValidate()}
                </Form>
            )
        } else {
            return this.renderLoading();
        }
    }

    renderLoading() {
        return (
            <div className="plui-loading-container">
                <div className="plui-loading">
                    <LoadingSpinner />
                </div>
            </div>
        );
    }

    /**
     * La rendition du message d'erreur
     */
    renderMessage() {
        if( this.props.errorTask ){
            return (
                <div className="errorTask">
                    <span className="text-danger"><Message msgId={this.props.errorTask.message}/></span>
                </div>
            );
        } else {
            return null;
        }
    }


    /**
     * La rendition de la partie info du ignalement
     */
    renderSignalementManagementInfo() {
        return (
            <div>
                <fieldset>
                    <Col md={6}>
                        <FormGroup controlId="signalement-management.info.date">
                            <ControlLabel><Message msgId="signalement-management.date"/></ControlLabel>
                            <FormControl type="datetime-local" readOnly
                                         value={this.state.task.creationDate !== null ? new Date(this.state.task.creationDate).toISOString().slice(0, -1) : ''}/>
                        </FormGroup>
                    </Col>
                    <Col md={6}>
                        <FormGroup controlId="signalement-management.info.statut">
                            <ControlLabel><Message msgId="signalement-management.statut"/></ControlLabel>
                            <FormControl type="text" readOnly
                                         value={this.state.task.functionalStatus !== null ? this.state.task.functionalStatus : ''}/>
                        </FormGroup>
                    </Col>
                </fieldset>
                <fieldset>
                    <Col md={6}>
                        <FormGroup controlId="signalement-management.info.layer">
                            <ControlLabel><Message msgId="signalement-management.layer"/></ControlLabel>
                            <FormControl type="text" readOnly
                                         value={this.state.task.asset.contextDescription.label !== null ? this.state.task.asset.contextDescription.label : ''}/>
                        </FormGroup>
                    </Col>
                    <Col md={12}>
                        <FormGroup controlId="signalement-management.info.object">
                            <ControlLabel><Message msgId="signalement-management.object"/></ControlLabel>
                            <FormControl componentClass="textarea"
                                         readOnly
                                         defaultValue={this.state.task.asset.description !== null ? this.state.task.asset.description : ''}
                            />
                        </FormGroup>
                    </Col>
                    {this.renderFiles()}
                </fieldset>
            </div>
        )
    }

    /**
     * La rendition de la partie des pièce jointes du signalement
     */
    renderFiles() {
        if (this.state.task.asset.attachments){
            return (
                <Col md={6}>
                    <FormGroup controlId="signalement-management.info.files">
                        <ControlLabel><Message msgId="signalement-management.files"/></ControlLabel>
                        <ul>
                            {this.state.task.asset.attachments.map(attachment=>
                                <li key={attachment.id} onClick={() => this.handleClickFile(attachment)}><a>{attachment.name}</a></li>)}
                        </ul>
                    </FormGroup>
                </Col>
            )
        }
    }

    /**
     * La rendition du bouton Assigner
     */
    renderSignalementManagementClaim(){
        if(!this.props.task.assignee || this.props.user.roles.find(role => role === "ADMIN"))
            return (
                <div>
                    <FormGroup controlId="signalement-management.info.claim">
                        <Button className="claimButton" key="claimer" bsStyle="info" onClick={() => this.handleClickButtonClaim()}>
                            <Message msgId="signalement-management.affect"/>
                        </Button>
                    </FormGroup>
                </div>
            )
    }

    /**
     * La rendition d'etape suivante pour faire une action
     */
    renderSignalementManagementActions() {
        if (this.state.task.actions && this.props.task.assignee && this.props.task.assignee === this.props.user.login) {
            return (
                <div className ="actionsList">
                    <Col md={12}>
                        <FormGroup controlId="signalement-management.info.actions">
                            <ControlLabel className="col-sm-4"><Message msgId="signalement-management.actions"/></ControlLabel>
                            <div className="col-sm-5">
                                <FormControl componentClass="select"
                                             onChange={this.handleChangeSelectAction}
                                >
                                    <option></option>
                                    {
                                        this.state.task.actions.map((option) => {
                                            return <option key={option.label} value={option.name}>{option.label}</option>
                                        })
                                    }
                                </FormControl>
                            </div>
                        </FormGroup>
                    </Col>
                </div>
            )
        }
    }

    /**
     * La rendition des buttons d'actions
     */
    renderSignalementManagementValidate() {
        if (this.state.task.actions && this.props.task.assignee && this.props.task.assignee === this.props.user.login) {
            return (
                <div className="validation-buttons">
                    <FormGroup controlId="signalement-management.info.cancel">
                        <Button className="cancelButton" bsStyle="default"
                                onClick={() => this.handleClickButtonCancelTask()}>
                            <Message msgId="signalement-management.cancel"/>
                        </Button>
                    </FormGroup>

                    <FormGroup controlId="signalement-management.info.update">
                        <Button className="updateButton" bsStyle="info"
                                onClick={() => this.handleClickButtonUpdateTask()}>
                            <Message msgId="signalement-management.validate"/>
                        </Button>
                    </FormGroup>
                </div>
            )
        }
    }



    /**
     * La rendition du formulaire du signalement
     */
    renderSignalementManagementForm() {
        if(this.state.task && this.state.task.form && this.state.task.form.sections) {
            return (
                <div>
                    <fieldset>
                        <div>
                            {this.state.task.form.sections.map((section, index) => {
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
                            <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}
                            </ControlLabel>
                            <div className="col-sm-5">
                                <FormControl type="number"
                                             readOnly={field.definition.readOnly || sectionReadOnly}
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
                            <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
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
                            <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
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
                            <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
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
                            <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
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
                window.signalementMgmt.debug("Type of definition undefined");
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
                        <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
                        <div className="col-sm-7">
                            <FormControl componentClass="textarea"
                                         readOnly={field.definition.readOnly  || sectionReadOnly}
                                         required={field.definition.required}
                                         name={field.definition.name}
                                         maxLength={field.definition.validators[0].attribute}
                                         multiple ={field.definition.multiple}
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
                        <ControlLabel className="col-sm-4">{field.definition.label} {field.definition.required ? "*" : ""}</ControlLabel>
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

        let field = this.state.task.form.sections[idSection].fields[idField];

        // valider le changement après modification du champs
        // pour s'assurer qu'il est en format correct avec le validateur de chaque champs
        let errorFields = {};
        if(field.definition.validators[0]){
            errorFields = this.getErrorFields(e.target.value , field.definition.validators[0], e.target.id);
        }

        // verifier si la liste des erreurs est vide sinon on affecte le changement des valeurs
        if (errorFields && Object.keys(errorFields).length === 0 && errorFields.constructor === Object) {

            if(field.definition.type === "BOOLEAN"){
                field.values = [e.target.checked];
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
     * L'action pour charger une pièce jointe
     */
    handleClickFile(attachment) {
        let uuid = this.state.task.asset.uuid;
        let file = {uuid: uuid, id: attachment.id};
        this.props.downloadAttachment(file);
    }

    /**
     * L'action pour s'assigner une tâche
     */
    handleClickButtonClaim(){
        let idTask = this.state.task.id;
        this.props.claimTask(idTask);
    }

    /**
     * L'action pour faire la mise à jour d'une tâche
     */
    handleClickButtonUpdateTask() {
        if(this.state.action && this.state.action !== "" ){
            this.props.updateDoAction(this.state.action, this.state.task, this.props.viewType);

        }else if(this.state.action === "" || this.state.action === null){
            this.props.updateTask(this.state.task);
        }
    }

    /**
     * L'action pour fermer la view
     */
    handleClickButtonCancelTask() {
        this.props.closeViewer();
    }

    /**
     * changement de la select pour l'etape suivante
     */
    handleChangeSelectAction= (e) => {
        this.setState({action: e.target.value})
    }



    handleClickButtonDisplayTaskAfter(){
        let index = this.state.index;
        if(index < this.props.response.features.length -1){
            let id = this.props.response.features[index+1].properties.id;
            if(id){
                window.signalementMgmt.debug("sigm task  get");
                this.props.getTask(id);
                this.setState({task: null, index : (index+1)});
            }
        }
    }

    handleClickButtonDisplayTaskBefore(){
        let index = this.state.index;
        if(index > 0){
            let id = this.props.response.features[index-1].properties.id;
            if(id){
                window.signalementMgmt.debug("sigm task  get");
                this.props.getTask(id);
                this.setState({task: null, index : (index-1)});
            }
        }
    }
}
