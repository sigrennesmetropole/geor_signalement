import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ToasterUtil} from "../../../utils/toaster.util";
import {TranslateService} from "@ngx-translate/core";
import {StyleService} from "../../../services/style.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ProcessStyling} from "../../../api/models";

 @Component({
   selector: 'style-process-add-dialog',
   templateUrl: 'style-process-add-dialog.html',
   styleUrls: ['style-process-add-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class StyleProcessAddDialog{

     public processDefinitionIdControl = new FormControl('', [Validators.required]);
     public userTaskIdControl = new FormControl('', [Validators.required]);
     public revisionControl = new FormControl('', );

     public StyleProcessAddForm = new FormGroup({
         processDefinitionId: this.processDefinitionIdControl,
         userTaskId: this.userTaskIdControl,
         revision: this.revisionControl,
     });

     constructor(
         private dialogRef: MatDialogRef<StyleProcessAddDialog>,
         private toaster: ToasterUtil,
         private translateService: TranslateService,
         private styleService: StyleService,
         @Inject(MAT_DIALOG_DATA) private data: any
     ) {
     }

     handleSubmitClick(): void{
         if (!this.StyleProcessAddForm.valid) {
             this.toaster.sendErrorMessage('style.process.add.formErrors.unknownError');
         } else {
             const processDefinitionId = this.processDefinitionIdControl.value;
             const userTaskId = this.userTaskIdControl.value;
             const revision = this.revisionControl.value;
             const res : ProcessStyling = {
                 processDefinitionId : processDefinitionId,
                 userTaskId : userTaskId,
                 revision: revision,
                 stylingId : this.data.id
             }
             this.dialogRef.close(res);
         }

     }

     /**
      * On click on the cancel button, close the window
      */
     handleCancelClick(): void {
         this.dialogRef.close();
     }
     
 }
