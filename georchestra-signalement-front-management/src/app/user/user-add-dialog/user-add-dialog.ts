import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {ToasterUtil} from 'src/app/utils/toaster.util';

@Component({
  selector: 'user-add-dialog',
  templateUrl: 'user-add-dialog.html',
  styleUrls: ['user-add-dialog.scss'],
})
/**
* Dialog window to add a User
*/
export class UserAddDialog {
  /**
  * The constructor of the add dialog
  * @param {MatDialogRef} dialogRef Reference to the DialogUserAddDialog
  * @param {ToasterUtil} toaster The toaster to display messages
  */
  constructor(
    public dialogRef: MatDialogRef<UserAddDialog>,
    public toasterUtil: ToasterUtil,
    private translateService: TranslateService) {}

    public emailControl = new FormControl('',
        [Validators.required, Validators.email]);
    public loginControl = new FormControl('', [Validators.required]);

    public userAddForm = new FormGroup({
      email: this.emailControl,
      login: this.loginControl,
    });

    /**
    * When user clicks on submit, check if all fields are filled
    * Then call the service to upload the workflow
    */
    handleSubmitClick() {
      if (this.loginControl.value == '') {
        this.toasterUtil.sendErrorMessage('user.add.noLogin');
      } else if (this.emailControl.value == '') {
        this.toasterUtil.sendErrorMessage('user.add.noMail');
      } else {
        this.dialogRef.close({login: this.loginControl.value,
          email: this.emailControl.value});
      }
    }

    /**
    * Check if an operation is running
    * @return {boolean} Is an operation is in progress
    */
    validData() : boolean {
      return this.userAddForm.valid;
    }

    /**
     * Get the error message for the email field
     * @return {Observable<string>}
     */
    getEmailError() : Observable<string> {
      if (this.emailControl.hasError('required')) {
        return this.translateService.get('user.add.noMail');
      } else if (this.emailControl.hasError('email')) {
        return this.translateService.get('user.add.notValidEmail');
      } else {
        return of('');
      }
    }

    /**
     * Get the error message for the login field
     * @return {Observable<string>}
     */
    getLoginError() : Observable<string> {
      if (this.loginControl.hasError('required')) {
        return this.translateService.get('user.add.noLogin');
      } else {
        return of('');
      }
    }
}
