import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {StyleDataSource, StyleItem} from "../style.datasource";

export interface SuppressionData{
  dataSource: StyleDataSource,
  target: StyleItem
}

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'style-delete-dialog',
   templateUrl: 'style-delete-dialog.html',
   styleUrls: ['style-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a workflow
  */
export class StyleDeleteDialog {
  private operation:boolean=false;
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<StyleDeleteDialog>}dialogRef
   * @param {SuppressionData} params
   */
  constructor(
    private dialogRef: MatDialogRef<StyleDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private params: SuppressionData) {}

  /**
   * On click on the cancel button, close the window
   */
  handleCancelClick(): void {
    this.dialogRef.close('Cancel');
  }

  /**
   * On workflow click call the service to delete the workflow
   */
  handleStyleClick() {
    this.dialogRef.close('Confirm');
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
  getTarget() : StyleItem {
    return this.params.target;
  }
}
