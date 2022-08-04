import {AfterViewInit, Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'point-read-dialog',
   templateUrl: 'point-read-dialog.html',
   styleUrls: ['point-read-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class PointReadDialog implements AfterViewInit{
    public iconGlyphControl = new FormControl('', [Validators.required]);
    public iconColorControl = new FormControl('', [Validators.required]);
    public iconShapeControl = new FormControl('', [Validators.required]);
    
    constructor(
        private dialogRef: MatDialogRef<PointReadDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        this.iconGlyphControl.setValue(this.data.target.style.iconGlyph);
        this.iconColorControl.setValue(this.data.target.style.iconColor);
        this.iconShapeControl.setValue(this.data.target.style.iconShape);
    }

    ngAfterViewInit(): void {
    }
}
