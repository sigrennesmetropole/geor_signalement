import {AfterContentInit, Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";

 @Component({
   selector: 'polygon-add-dialog',
   templateUrl: 'polygon-add-dialog.html',
   styleUrls: ['polygon-add-dialog.scss'],
 })

/**
  * Part for Polygon in the windows to add a style
  */
export class PolygonAddDialog implements AfterContentInit {
    @Input() parentForm!: FormGroup;

    public colorControl !:FormControl;
    public opacityControl !:FormControl;
    public weightControl !:FormControl;
    public fillColorControl !:FormControl;
    public fillOpacityControl !:FormControl;
    public dashArrayControl !:FormControl;

    constructor(
        private dialogRef: MatDialogRef<PolygonAddDialog>,
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
        this.fillColorControl = this.parentForm.get("fillColor") as FormControl;
        this.fillOpacityControl = this.parentForm.get("fillOpacity") as FormControl;
        this.dashArrayControl = this.parentForm.get("dashArray") as FormControl;
    }

    ngAfterContentInit(): void {
        this.addGroupToParent();
    }
}
