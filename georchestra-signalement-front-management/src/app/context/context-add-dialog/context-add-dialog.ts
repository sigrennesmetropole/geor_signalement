import {Component, Inject} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {ContextType, GeographicType} from 'src/app/api/models';
import {ToasterUtil} from 'src/app/utils/toaster.util';
import {WorkflowItem} from 'src/app/workflow/workflow.datasource';
import {ContextItem} from '../context.datasource';

/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'context-add-dialog',
   templateUrl: 'context-add-dialog.html',
   styleUrls: ['context-add-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class ContextAddDialog {
  public nameControl = new FormControl('', [Validators.required,
    Validators.maxLength(100)]);
  public labelControl = new FormControl('', [Validators.required,
    Validators.maxLength(250)]);
  public contextTypeControl = new FormControl('', [Validators.required]);
  public geographicTypeControl = new FormControl('', [Validators.required]);
  public processDefinitionControl = new FormControl('', [Validators.required]);
  public versionControl = new FormControl('', [Validators.required]);

  public contextAddForm = new FormGroup({
    name: this.nameControl,
    label: this.labelControl,
    contextType: this.contextTypeControl,
    geographicType: this.geographicTypeControl,
    processDefinition: this.processDefinitionControl,
    version: this.versionControl,
  });

  private LAST_VERSION = '';

  public contextTypes = ContextType.values();
  public geographicTypes = GeographicType.values();
  private workflowKeys : string[] = [];
  private workflowKeyToName = new Map<string, string>();
  private workflowKeyToVersions = new Map<string, number[]>();

  /**
   * The constructor of the add window
   * @param {MatDialogRef<DialogUserDeleteDialog>} dialogRef
   */
  constructor(
    private dialogRef: MatDialogRef<ContextAddDialog>,
    private toaster: ToasterUtil,
    private translateService: TranslateService,
    @Inject(MAT_DIALOG_DATA) private workflowItems: WorkflowItem[]) {
    this.workflowItems.map((workflow)=>{
      if (!this.workflowKeyToVersions.has(workflow.key)) {
        this.workflowKeyToName.set(workflow.key, workflow.name);
        this.workflowKeyToVersions.set(workflow.key, []);
        this.workflowKeys.push(workflow.key);
      }
      this.workflowKeyToVersions.get(workflow.key)?.push(workflow.version);
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
    if (this.contextTypes.map((x)=>x.toString())
        .indexOf(this.contextTypeControl.value) == -1) {
      this.toaster.sendErrorMessage('context.add.errors.wrongContextType');
    } else if (this.geographicTypes.map((x)=>x.toString())
        .indexOf(this.geographicTypeControl.value) == -1) {
      this.toaster.sendErrorMessage('context.add.errors.wrongGeographicType');
    } else if (!this.contextAddForm.valid) {
      this.toaster.sendErrorMessage('context.add.errors.unknownError');
    } else {
      let version : number;
      if (this.versionControl.value == this.LAST_VERSION) {
        version = 0;
      } else {
        version = this.versionControl.value;
      }

      const result : ContextItem = {
        name: this.nameControl.value,
        label: this.labelControl.value,
        contextType: this.contextTypeControl.value,
        geographicType: this.geographicTypeControl.value,
        processDefinition: this.processDefinitionControl.value,
        version: version,
      };
      this.dialogRef.close(result);
    }
  }

  /**
   * Verify if data are valid
   * @return {boolean}
   */
  validData() : boolean {
    return this.contextAddForm.valid;
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
  getWorflowsVersions(): string[] {
    this.translateService.get('common.lastVersion').subscribe(
        (translation) =>{
          this.LAST_VERSION = translation;
        },
    );
    const versions = this.workflowKeyToVersions
        .get(this.processDefinitionControl.value);
    const currentVersion = this.versionControl.value;
    if (versions) {
      if (!versions.map(String).includes(currentVersion) &&
    currentVersion != this.LAST_VERSION) {
        this.versionControl.reset();
      }
      return versions.map(String).concat(this.LAST_VERSION);
    } else {
      return [];
    }
  }

  /**
   * Get the error of the name field
   * @return {Observable<string>}
   */
  getNameError() : Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else if (this.nameControl.hasError('maxLength')) {
      return this.translateService.get('context.add.errors.tooLongValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the label field
   * @return {Observable<string>}
   */
  getLabelError() : Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else if (this.nameControl.hasError('maxLength')) {
      return this.translateService.get('context.add.errors.tooLongValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the ContexType field
   * @return {Observable<string>}
   */
  getContextTypeError() : Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the GeographicError field
   * @return {Observable<string>}
   */
  getGeographicTypeError() : Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the ProcessDefinition field
   * @return {Observable<string>}
   */
  getProcessDefinitionError() : Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else {
      return of('');
    }
  }

  /**
   * Get the error of the version field
   * @return {Observable<string>}
   */
  getVersionError(): Observable<string> {
    if (this.nameControl.hasError('required')) {
      return this.translateService.get('context.add.errors.noValue');
    } else {
      return of('');
    }
  }
}
