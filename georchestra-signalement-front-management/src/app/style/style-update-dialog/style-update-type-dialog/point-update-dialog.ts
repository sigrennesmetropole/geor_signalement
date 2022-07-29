import {AfterContentInit, AfterViewInit, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'point-update-dialog',
   templateUrl: 'point-update-dialog.html',
   styleUrls: ['point-update-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class PointUpdateDialog  implements AfterContentInit{

    @Input() parentForm!: FormGroup;

    public iconGlyphControl!:FormControl;
    public iconColorControl!: FormControl;
    public iconShapeControl!: FormControl;

    constructor(
        private dialogRef: MatDialogRef<PointUpdateDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {

    }
    private updateGroupToParent(): void {
        this.iconGlyphControl = this.parentForm.get("iconGlyph") as FormControl;
        this.iconColorControl = this.parentForm.get("iconColor") as FormControl;
        this.iconShapeControl = this.parentForm.get("iconShape") as FormControl;

    }

    ngAfterContentInit(): void {
        this.updateGroupToParent();
    }
}
