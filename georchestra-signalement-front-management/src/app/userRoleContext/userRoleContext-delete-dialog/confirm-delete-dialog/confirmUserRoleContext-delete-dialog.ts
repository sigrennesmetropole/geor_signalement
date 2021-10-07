import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {UserRoleContext} from 'src/app/api/models';

 @Component({
   selector: 'confirmUserRoleContext-delete-dialog',
   templateUrl: 'confirmUserRoleContext-delete-dialog.html',
   styleUrls: ['confirmUserRoleContext-delete-dialog.scss'],
 })

/**
  * The dialog window to confirm delete of an used operator
  */
export class ConfirmUserRoleContextDeleteDialog {
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<ConfirmUserRoleContextDeleteDialog>} dialogRef
   * @param {UserRoleContextItem} target The target to delete
   */
  constructor(
    private dialogRef: MatDialogRef<ConfirmUserRoleContextDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private target: UserRoleContext) {}

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
}
