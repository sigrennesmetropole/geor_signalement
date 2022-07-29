import {AfterViewInit, Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'polygon-read-dialog',
   templateUrl: 'polygon-read-dialog.html',
   styleUrls: ['polygon-read-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class PolygonReadDialog implements AfterViewInit{
    public colorControl = new FormControl('', [Validators.required]);
    public opacityControl = new FormControl('', [Validators.required]);
    public weightControl = new FormControl('', [Validators.required]);
    public fillColorControl = new FormControl('', [Validators.required]);
    public fillOpacityControl = new FormControl('', [Validators.required]);
    public dashArrayControl = new FormControl('', [Validators.required]);




    constructor(
        private dialogRef: MatDialogRef<PolygonReadDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        this.colorControl.setValue(this.data.target.style.color);
        this.opacityControl.setValue(this.data.target.style.opacity);
        this.weightControl.setValue(this.data.target.style.weight);
        this.fillColorControl.setValue(this.data.target.style.fillColor);
        this.fillOpacityControl.setValue(this.data.target.style.fillOpacity);
        this.dashArrayControl.setValue(this.data.target.style.dashArray);
    }

    ngAfterViewInit(): void {
    }
}
