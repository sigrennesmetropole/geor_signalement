import {AfterContentInit, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";

 @Component({
   selector: 'line-add-dialog',
   templateUrl: 'line-add-dialog.html',
   styleUrls: ['line-add-dialog.scss'],
 })

 /**
  * Part for Line in the windows to add a style
  */
export class LineAddDialog implements AfterContentInit {
    @Input() parentForm!: FormGroup;

    public colorControl !:FormControl;
    public opacityControl !:FormControl;
    public weightControl !:FormControl;
    public iconAnchorControl !:FormControl;

    constructor(
        private dialogRef: MatDialogRef<LineAddDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
    }

    private addGroupToParent(): void {
        // @ts-ignore
        this.colorControl = this.parentForm.get("color") as FormControl;
        this.opacityControl = this.parentForm.get("opacity") as FormControl;
        this.weightControl = this.parentForm.get("weight") as FormControl;
        this.iconAnchorControl = this.parentForm.get("iconAnchor") as FormControl;
    }

    ngAfterContentInit(): void {
        this.addGroupToParent();
    }
}
