import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MatTable} from "@angular/material/table";
import {StyleContainer} from "../../api/models/style-container";
import {MatInput} from "@angular/material/input";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ToasterUtil} from "../../utils/toaster.util";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'style-read-dialog',
   templateUrl: 'style-read-dialog.html',
   styleUrls: ['style-read-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class StyleReadDialog implements AfterViewInit{
    public nameControl = new FormControl('', [Validators.required]);
    public typeControl = new FormControl('', [Validators.required]);

    public StyleReadForm = new FormGroup({
        name: this.nameControl,
        type: this.typeControl,
    });


    constructor(
        private dialogRef: MatDialogRef<StyleReadDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        this.nameControl.setValue(this.data.target.name);
        this.typeControl.setValue(this.data.target.type);
    }

    ngAfterViewInit(): void {
    }
}
