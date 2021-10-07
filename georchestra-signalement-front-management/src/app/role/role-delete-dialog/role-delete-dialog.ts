import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {Role} from 'src/app/api/models';

 @Component({
   selector: 'role-delete-dialog',
   templateUrl: 'role-delete-dialog.html',
   styleUrls: ['role-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a role
  */
export class RoleDeleteDialog {
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<DialogUserDeleteDialog>} dialogRef
   * @param {Role} target The target to delete
   */
  constructor(
    private dialogRef: MatDialogRef<RoleDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private target: Role) {}

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
   * Get the targeted Role
   * @return {Role} The targeted Role
   */
  getTarget() : Role {
    return this.target;
  }
}
