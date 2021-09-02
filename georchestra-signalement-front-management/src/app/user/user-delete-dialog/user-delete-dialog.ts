import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {UserItem} from '../user.datasource';

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'user-delete-dialog',
   templateUrl: 'user-delete-dialog.html',
   styleUrls: ['user-delete-dialog.scss'],
 })

/**
  * The dialog window to delete a user
  */
export class UserDeleteDialog {
  /**
   * The constructor of the delete window
   * @param {MatDialogRef<UserDeleteDialog>} dialogRef
   * @param {UserItem} target The target to delete
   */
  constructor(
    private dialogRef: MatDialogRef<UserDeleteDialog>,
    @Inject(MAT_DIALOG_DATA) private target: UserItem) {}

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
   * Get the targeted UserItem
   * @return {UserItem} The targeted UserItem
   */
  getTarget() : UserItem {
    return this.target;
  }
}
