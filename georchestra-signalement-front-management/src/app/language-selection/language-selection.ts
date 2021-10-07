import {Component, Inject} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AppComponent} from '../app.component';

/**
 * @title Dialog Overview
 */

 @Component({
   selector: 'language-selector-dialog',
   templateUrl: 'language-selection.html',
   styleUrls: ['language-selection.scss'],
 })
 /**
  * Dialog window to select language
  */
export class LanguageSelectionDialog {
  /**
   * Constructor of the language selection window
   * @param {MatDialogRef<LanguageSelectionDialog>} dialogRef
   * @param {AppComponent} caller The App which called the language selection
   */
  constructor(
    private dialogRef: MatDialogRef<LanguageSelectionDialog>,
    @Inject(MAT_DIALOG_DATA) private caller: AppComponent) {}

  /**
   * On click on the cancel button just close the window
   */
  handleCancelClick(): void {
    this.dialogRef.close();
  }

  /**
   * On change of the language update
   * @param {string} code The IETF language tag
   */
  handleLanguageChange(code:string) : void {
    this.caller.setCurrentLanguage(code);
  }

  /**
   * Get the current lang of the app
   * @return {string} the IETF code of the current app lang
   */
  getCurrentLang() : string {
    return this.caller.getCurrentLang();
  }

  /**
   * Get the availables langs in the app
   * @return {string[]} Available langs
   */
  getLangs() : string[] {
    return this.caller.getLangs();
  }
}
