import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {ToasterUtil} from 'src/app/utils/toaster.util';

@Component({
  selector: 'dialog-workflow-add-dialog',
  templateUrl: 'workflow-add-dialog.html',
  styleUrls: ['workflow-add-dialog.scss'],
})
/**
* Dialog window to add a Workflow version
*/
export class WorkflowAddDialog {
  /**
  * The constructor of the add dialog
  * @param {MatDialogRef} dialogRef Reference to the DialogWorflowAddDialog
  * @param {WorkflowDataSource} dataSource Reference to the datasource
  * @param {ToasterUtil} toaster The toaster to display messages
  * @param {TranslateService} translate The translate service
  */
  constructor(
    public dialogRef: MatDialogRef<WorkflowAddDialog>,
    private toaster: ToasterUtil) {}

    file:File|null = null;
    fileName = '';
    deploymentName ='';
    requiredFileType =['xml'] // TODO
    private operation=false;

    /**
    * When user clicks on submit, check if all fields are filled
    * Then call the service to upload the workflow
    */
    handleSubmitClick() {
      if (!this.operation && this.file!=null && this.deploymentName!='') {
        this.dialogRef.close({name: this.deploymentName, file: this.file});
      } else if (this.file==null) {
        // Toast an error message about no file
        this.toaster.sendErrorMessage('workflow.add.noFileYet');
      } else if (this.deploymentName=='') {
        // Toast an error message about deployment name is missing
        this.toaster.sendErrorMessage('workflow.add.noDeploymentName');
      }
    }

    /**
    * Update the file selected by the user
    * @param {Event} event The input event
    */
    handleFileSelected(event:Event) : void {
      const target = event.target as HTMLInputElement;

      if (target.files != null) {
        this.file = target.files[0];
        if (this.file != null) {
          this.fileName = this.file.name;
        }
      } else {
        this.file = null;
      }
    }

    /**
    * Check if an operation is running
    * @return {boolean} Is an operation is in progress
    */
    operationInProgress() : boolean {
      return this.operation;
    }
}
