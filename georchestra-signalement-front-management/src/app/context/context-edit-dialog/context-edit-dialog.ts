import {Component, Inject} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {ToasterUtil} from 'src/app/utils/toaster.util';
import {WorkflowItem} from 'src/app/workflow/workflow.datasource';

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'context-edit-dialog',
   templateUrl: 'context-edit-dialog.html',
   styleUrls: ['context-edit-dialog.scss'],
 })

/**
  * The dialog window to edit a context
  */
export class ContextEditDialog {
  public processDefinitionControl = new FormControl('', [Validators.required]);
  public versionControl = new FormControl('', [Validators.required]);
  public labelControl = new FormControl('', [Validators.required,
    Validators.maxLength(250)]);

  public contextEditForm = new FormGroup({
    label: this.labelControl,
    processDefinition: this.processDefinitionControl,
    version: this.versionControl,
  });
  private workflowItems : WorkflowItem[];
  private workflowKeys : string[] = [];
  private workflowKeyToName = new Map<string, string>();
  private workflowKeyToVersions = new Map<string, string[]>();

  private LAST_VERSION : string ='';

  /**
   * The constructor of the add window
   * @param {MatDialogRef<ContextEditDialog>} dialogRef
   */
  constructor(
    private dialogRef: MatDialogRef<ContextEditDialog>,
    private toaster: ToasterUtil,
    private translateService: TranslateService,
    @Inject(MAT_DIALOG_DATA) private data:any) {
    this.labelControl.setValue(data.target.label);
    this.processDefinitionControl.setValue(data.target.processDefinition);
    if (data.target.version != 0) {
      this.versionControl.setValue(String(data.target.version));
    } else {
      this.translateService.get('common.lastVersion').subscribe(
          (result)=>{
            this.versionControl.setValue(result);
          },
      );
    }

    this.workflowItems = data.workflows;

    this.workflowItems.forEach((workflow)=>{
      if (!this.workflowKeyToVersions.has(workflow.key)) {
        this.workflowKeyToName.set(workflow.key, workflow.name);
        this.workflowKeyToVersions.set(workflow.key, []);
        this.workflowKeys.push(workflow.key);
      }
      this.workflowKeyToVersions
          .get(workflow.key)?.push(String(workflow.version));
    });
  }

  /**
   * On click on the cancel button, close the window
   */
  handleCancelClick(): void {
    this.dialogRef.close();
  }

  /**
   * On click on the validation
   * - Check data validities,
   * - Close the window with data returned.
   */
  handleSubmitClick() : void {
    if (!this.contextEditForm.valid) {
      this.toaster.sendErrorMessage('context.edit.formErrors.unknownError');
    } else {
      let version : number;

      if (this.versionControl.value == this.LAST_VERSION) {
        version = 0;
      } else {
        version = this.versionControl.value;
      }
      const processDefinition = this.processDefinitionControl.value;
      const label = this.labelControl.value;
      this.dialogRef.close({label: label,
        process: processDefinition,
        version: version});
    }
  }

  /**
   * Verify if data are valid
   * @return {boolean}
   */
  validData() : boolean {
    return this.contextEditForm.valid;
  }

  /**
   * Return all workflow keys
   * @return {string[]}
   */
  getWorkflowKeys() : string[] {
    return this.workflowKeys;
  }

  /**
   * Return workflowName by his key
   * @param {string} key
   * @return {string}
   */
  getWorkflowName(key : string) : string {
    return this.workflowKeyToName.get(key) ?? '';
  }

  /**
   * Return versions of the current selected workflow
   * @return {string[]}
   */
  getVersionsOfWorkflow(): string[] {
    this.translateService.get('common.lastVersion').subscribe(
        (translation) =>{
          this.LAST_VERSION = translation;
        },
    );
    const versions = this.workflowKeyToVersions
        .get(this.processDefinitionControl.value);
    if (versions) {
      return versions.map(String).concat(this.LAST_VERSION);
    } else {
      return [];
    }
  }
  /**
 * Get a localized error for the label field
 * @return {Observable<string>}
 */
  getLabelError() : Observable<string> {
    if (this.labelControl.hasError('required')) {
      return this.translateService.get('context.edit.formErrors.noValue');
    } else {
      return of('');
    }
  }

  /**
   * Get a localized error for the processDefinition field
   * @return {Observable<string>}
   */
  getProcessDefinitionError() : Observable<string> {
    if (this.processDefinitionControl.hasError('required')) {
      return this.translateService.get('context.edit.formErrors.noValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the version field
   * @return {Observable<string>}
   */
  getVersionError(): Observable<string> {
    if (this.versionControl.hasError('required')) {
      return this.translateService.get('context.edit.formErrors.noValue');
    } else {
      return of('');
    }
  }
}
