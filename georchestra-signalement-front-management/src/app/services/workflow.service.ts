import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {AdministrationService} from '../api/services';
import {WorkflowItemMapper} from '../mappers/workflow-item.mapper';
import {ToasterUtil} from '../utils/toaster.util';
import {WorkflowItem} from '../workflow/workflow-datasource';


@Injectable({
  providedIn: 'root',
})
/**
* Service to interact with API service to get, list and delete Workflows
*/
export class WorkflowService {
  /**
  * Constructor of the WorkflowService
  * @param {AdministrationService} administrationService The API service
  * @param {TranslateService} translateService The TranslateService
  * @param {ToasterUtil} toasterService The ToasterUtil
  */
  constructor(private administrationService: AdministrationService,
    private toasterService: ToasterUtil,
    private workflowItemMapper : WorkflowItemMapper) {}

  /**
    * Get the workflows
    * @return {Observable<WorkflowItem[]>} A subject about the WorkflowItems
    */
  getWorkflows(): Observable<WorkflowItem[]> {
    return this.administrationService
        .searchProcessDefinition()
        .pipe(
            map((processes) => {
              if (processes === null) return [];
              return processes.map(this.workflowItemMapper
                  .processDefinitionToWorkflow);
            }),
        );
  }
  /**
      * Function to delete a WorkflowItem
      * @param {WorkflowItem} target The WorkflowItem to delete
      * @return {Observable<boolean>} A promise about suppression success
      */
  deleteProcessDefinition(target : WorkflowItem) : Observable<boolean> {
    return this.administrationService
        .deleteProcessDefinition({name: target.name});
  }

  /**
      * Function to delete a version of a WorflowItem
      * @param {WorkflowItem} target The WorkflowItem version to delete
      * @return {Observable<boolean>} A promise about suppression success
      */
  deleteProcessDefinitionVersion(target : WorkflowItem)
      : Observable<boolean> {
    return this.administrationService
        .deleteProcessDefinition({name: target.name, version: target.version});
  }

  /**
      * Upload a new version of a WorkflowItem
      * @param {Blob} file The file of the processDefinition
      * @param {string} deploymentName The Deployment Name
      * @return {Observable<boolean>} A promise about addition success
      */
  updateProcessDefinition(file: Blob, deploymentName: string)
      :Observable<boolean> {
    const params = {
      file: file,
      deploymentName: deploymentName,
    };
    return this.administrationService
        .updateProcessDefinition(params);
  }
}
