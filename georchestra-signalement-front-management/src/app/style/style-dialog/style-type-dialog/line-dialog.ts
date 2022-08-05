import {AfterContentInit, AfterViewInit, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'line-dialog',
   templateUrl: 'line-dialog.html',
   styleUrls: ['line-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class LineDialog implements AfterContentInit {
    @Input() parentForm!: FormGroup;
    @Input() action!: String;

    public colorControl !:FormControl;
    public opacityControl !:FormControl;
    public weightControl !:FormControl;
    public iconAnchorControl !:FormControl;

    constructor(
        private dialogRef: MatDialogRef<LineDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
    }

    private updateGroupToParent(): void {
        // @ts-ignore
        this.colorControl = this.parentForm.get("color") as FormControl;
        this.opacityControl = this.parentForm.get("opacity") as FormControl;
        this.weightControl = this.parentForm.get("weight") as FormControl;
        this.iconAnchorControl = this.parentForm.get("iconAnchor") as FormControl;

    }

    ngAfterContentInit(): void {
        this.updateGroupToParent();
    }
}
