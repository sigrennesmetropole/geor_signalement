import {Injectable} from '@angular/core';
import {ProcessDefinition} from '../api/models';
import {WorkflowItem} from '../workflow/workflow-datasource';

@Injectable()
/**
 * Util to convert datas into objects
 */
export class WorkflowItemMapper {
  /**
     * Constructor
     */
  constructor() {}

  /**
     * Mapper for ProcessDefinition to WorkflowItem
     * @param {ProcessDefinition} processDefinition ProcessDefinition to convert
     * @return {WorkflowItem} WorkflowItem converted from ProcessDefinition
     */
  processDefinitionToWorkflow(processDefinition : ProcessDefinition)
    : WorkflowItem {
    return {
      name: processDefinition.name ?? '',
      version: processDefinition.version ?? 0,
      key: processDefinition.key ?? '',
      resourceName: processDefinition.resourceName ?? '',
      description: processDefinition.description ?? '',
    };
  }
}
