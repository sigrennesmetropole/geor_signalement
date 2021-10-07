import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {ContextItem} from '../context.datasource';

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'context-delete-dialog',
   templateUrl: 'context-delete-dialog.html',
   styleUrls: ['context-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a context
  */
export class ContextDeleteDialog {
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<DialogUserDeleteDialog>} dialogRef
   * @param {ContextItem} target The target to delete
   */
  constructor(
    private dialogRef: MatDialogRef<ContextDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private target: ContextItem) {}

  /**
   * On click on the cancel button, close the window
   */
  handleCancelClick(): void {
    this.dialogRef.close('Cancel');
  }

  /**
   * On click return a confirmation
   */
  handleConfirmClick() : void {
    this.dialogRef.close('Confirm');
  }

  /**
   * Get the targeted ContextItem
   * @return {ContextItem} The targeted ContextItem
   */
  getTarget() : ContextItem {
    return this.target;
  }
}
