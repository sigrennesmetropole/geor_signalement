import {AfterViewInit, Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ToasterUtil} from "../../../utils/toaster.util";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'line-read-dialog',
   templateUrl: 'line-read-dialog.html',
   styleUrls: ['line-read-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class LineReadDialog implements AfterViewInit{
    public colorControl = new FormControl('', [Validators.required]);
    public opacityControl = new FormControl('', [Validators.required]);
    public weightControl = new FormControl('', [Validators.required]);
    public iconAnchorControl = new FormControl('', [Validators.required]);

    constructor(
        private dialogRef: MatDialogRef<LineReadDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        this.colorControl.setValue(this.data.target.style.color);
        this.opacityControl.setValue(this.data.target.style.opacity);
        this.weightControl.setValue(this.data.target.style.weight);
        this.iconAnchorControl.setValue(this.data.target.style.iconAnchor);
    }

    ngAfterViewInit(): void {
    }
}
