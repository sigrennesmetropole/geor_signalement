import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {UserRoleContext} from 'src/app/api/models';

/**
 * @title Delete UserRoleContext Dialog
 */


 @Component({
   selector: 'userRoleContext-delete-dialog',
   templateUrl: 'userRoleContext-delete-dialog.html',
   styleUrls: ['userRoleContext-delete-dialog.scss'],
 })

/**
  * The dialog window to delete an operator
  */
export class UserRoleContextDeleteDialog {
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<UserRoleContextDeleteDialog>} dialogRef
   * @param {UserRoleContextItem} target The target to delete
   */
  constructor(
    private dialogRef: MatDialogRef<UserRoleContextDeleteDialog>,
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

  /**
   * Get the targeted UserRoleContext
   * @return {UserRoleContext} The targeted UserRoleContext
   */
  getTargetInformations() : String {
    return '(' +
    this.target.user?.login + ', ' +
    this.target.role?.label + ', ' +
    (this.target.geographicArea?.nom ?? ' ') + ', ' +
    this.target.contextDescription?.label + ')';
  }
}
