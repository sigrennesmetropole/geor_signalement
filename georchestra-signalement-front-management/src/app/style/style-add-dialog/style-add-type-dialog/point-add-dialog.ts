import {AfterContentInit, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";


 @Component({
   selector: 'point-add-dialog',
   templateUrl: 'point-add-dialog.html',
   styleUrls: ['point-add-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class PointAddDialog  implements AfterContentInit {


    @Input() parentForm!: FormGroup;

    public iconGlyphControl!:FormControl;
    public iconColorControl!: FormControl;
    public iconShapeControl!: FormControl;


    constructor(
        private dialogRef: MatDialogRef<PointAddDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
    }

    private addGroupToParent(): void {
        // @ts-ignore
        this.iconGlyphControl = this.parentForm.get("iconGlyph") as FormControl;
        this.iconColorControl = this.parentForm.get("iconColor") as FormControl;
        this.iconShapeControl = this.parentForm.get("iconShape") as FormControl;

    }

    ngAfterContentInit(): void {
        this.addGroupToParent();
    }
}
