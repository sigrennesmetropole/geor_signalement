import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {WorkflowDataSource, WorkflowItem} from '../workflow-datasource';

export interface SuppressionData{
  dataSource: WorkflowDataSource,
  target: WorkflowItem
}

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'dialog-workflow-delete-dialog',
   templateUrl: 'workflow-delete-dialog.html',
   styleUrls: ['workflow-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a workflow
  */
export class DialogWorkflowDeleteDialog {
  private operation:boolean=false;
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<DialogWorkflowDeleteDialog>}dialogRef
   * @param {SuppressionData} params
   */
  constructor(
    private dialogRef: MatDialogRef<DialogWorkflowDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private params: SuppressionData) {}

  /**
   * On click on the cancel button, close the window
   */
  handleCancelClick(): void {
    this.dialogRef.close();
  }

  /**
   * On workflow click call the service to delete the workflow
   */
  handleWorkflowClick() {
    this.dialogRef.close('workflow');
  }

  /**
   * On version click call the service to delete the workflow version
   */
  handleVersionClick() {
    this.dialogRef.close('version');
  }

  /**
   * Is the an operation in progress
   * @return {boolean} An operation is running
   */
  operationInProgress() : boolean {
    return this.operation;
  }

  /**
   * Get the targeted WorkflowItem
   * @return {WorkflowItem} The targeted Workflow item
   */
  getTarget() : WorkflowItem {
    return this.params.target;
  }
}
