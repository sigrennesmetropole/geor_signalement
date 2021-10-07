import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {ToasterUtil} from 'src/app/utils/toaster.util';

@Component({
  selector: 'role-add-dialog',
  templateUrl: 'role-add-dialog.html',
  styleUrls: ['role-add-dialog.scss'],
})
/**
* Dialog window to add a Role
*/
export class RoleAddDialog {
  /**
  * The constructor of the add dialog
  * @param {MatDialogRef} dialogRef Reference to the DialogUserAddDialog
  * @param {ToasterUtil} toaster The toaster to display messages
  */
  constructor(
    public dialogRef: MatDialogRef<RoleAddDialog>,
    public toasterUtil: ToasterUtil,
    private translateService: TranslateService) {}

    public nameControl = new FormControl('',
        [Validators.required]);
    public labelControl = new FormControl('',
        [Validators.required]);

    public roleAddForm = new FormGroup({
      name: this.nameControl,
      login: this.labelControl,
    });

    /**
    * When user clicks on submit, check if all fields are filled
    * Then call the service to upload the workflow
    */
    handleSubmitClick() {
      if (this.labelControl.value == '') {
        this.toasterUtil.sendErrorMessage('role.add.noName');
      } else if (this.nameControl.value == '') {
        this.toasterUtil.sendErrorMessage('role.add.noLabel');
      } else {
        this.dialogRef.close({name: this.nameControl.value,
          label: this.labelControl.value});
      }
    }

    /**
    * Check if data are valid
    * @return {boolean}
    */
    validData() : boolean {
      return this.roleAddForm.valid;
    }

    /**
     * Get the error message for the name field
     * @return {Observable<string>}
     */
    getNameError() : Observable<string> {
      if (this.nameControl.hasError('required')) {
        return this.translateService.get('role.add.noName');
      } else {
        return of('');
      }
    }

    /**
     * Get the error message for the label field
     * @return {Observable<string>}
     */
    getLabelError() : Observable<string> {
      if (this.labelControl.hasError('required')) {
        return this.translateService.get('role.add.noLabel');
      } else {
        return of('');
      }
    }
}
