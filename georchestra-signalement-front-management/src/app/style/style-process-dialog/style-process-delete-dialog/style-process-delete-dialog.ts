import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {StyleDataSource, StyleItem} from "../../style.datasource";

export interface SuppressionData{
  dataSource: StyleDataSource,
  target: StyleItem
}

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'style-process-delete-dialog',
   templateUrl: 'style-process-delete-dialog.html',
   styleUrls: ['style-process-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a workflow
  */
export class StyleProcessDeleteDialog {
  private operation:boolean=false;
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<StyleProcessDeleteDialog>}dialogRef
   * @param {SuppressionData} params
   */
  constructor(
    private dialogRef: MatDialogRef<StyleProcessDeleteDialog>,
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
}
