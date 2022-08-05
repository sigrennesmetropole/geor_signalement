import {AfterViewInit, Component, Inject, ViewChild} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ToasterUtil} from "../../utils/toaster.util";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GeographicType} from "../../api/models/geographic-type";
import {Style} from "../../api/models/style";
import {StyleContainer} from "../../api/models/style-container";
/**
 * @title Dialog Overview
 */


 @Component({
   selector: 'style-update-dialog',
   templateUrl: 'style-update-dialog.html',
   styleUrls: ['style-update-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class StyleUpdateDialog{ public nameControl = new FormControl('', [Validators.required]);
    public typeControl = new FormControl(GeographicType.POINT, [Validators.required]);


    //POINT
    public iconGlyphControl = new FormControl('', );
    public iconColorControl = new FormControl('', );
    public iconShapeControl = new FormControl('', );

    //LINE + POLYGON
    public colorControl = new FormControl('', );
    public opacityControl = new FormControl('', );
    public weightControl = new FormControl('', );

    //LINE
    public iconAnchorControl = new FormControl('', );

    //POLYGON
    public fillColorControl = new FormControl('', );
    public fillOpacityControl = new FormControl('', );
    public dashArrayControl = new FormControl('', );

    public StyleUpdateForm = new FormGroup({
        name: this.nameControl,
        type: this.typeControl,
        iconGlyph: this.iconGlyphControl,
        iconColor: this.iconColorControl,
        iconShape: this.iconShapeControl,
        color: this.colorControl,
        opacity: this.opacityControl,
        weight: this.weightControl,
        iconAnchor: this.iconAnchorControl,
        fillColor: this.fillColorControl,
        fillOpacity: this.fillOpacityControl,
        dashArray: this.dashArrayControl,


    });

    public styleTypeEnum = GeographicType;

    constructor(
        private dialogRef: MatDialogRef<StyleUpdateDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        this.nameControl.setValue(this.data.target.name);
        this.typeControl.setValue(this.data.target.type);

        if(this.data.target.type.toString() == "POINT"){
            this.iconGlyphControl.setValue(this.data.target.style.iconGlyph);
            this.iconColorControl.setValue(this.data.target.style.iconColor);
            this.iconShapeControl.setValue(this.data.target.style.iconShape);
            this.StyleUpdateForm.controls['iconGlyph'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['iconColor'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['iconShape'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['iconGlyph'].updateValueAndValidity();
            this.StyleUpdateForm.controls['iconColor'].updateValueAndValidity();
            this.StyleUpdateForm.controls['iconShape'].updateValueAndValidity();

        }else if(this.data.target.type.toString() == "LINE"){
            this.colorControl.setValue(this.data.target.style.color);
            this.opacityControl.setValue(this.data.target.style.opacity);
            this.weightControl.setValue(this.data.target.style.weight);
            this.iconAnchorControl.setValue(this.data.target.style.iconAnchor);

            this.StyleUpdateForm.controls['color'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['opacity'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['weight'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['color'].updateValueAndValidity();
            this.StyleUpdateForm.controls['opacity'].updateValueAndValidity();
            this.StyleUpdateForm.controls['weight'].updateValueAndValidity();

            this.StyleUpdateForm.controls['iconAnchor'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['iconAnchor'].updateValueAndValidity();


        }else if(this.data.target.type.toString() == "POLYGON"){
            this.colorControl.setValue(this.data.target.style.color);
            this.opacityControl.setValue(this.data.target.style.opacity);
            this.weightControl.setValue(this.data.target.style.weight);
            this.fillColorControl.setValue(this.data.target.style.fillColor);
            this.fillOpacityControl.setValue(this.data.target.style.fillOpacity);
            this.dashArrayControl.setValue(this.data.target.style.dashArray);

            this.StyleUpdateForm.controls['color'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['opacity'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['weight'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['color'].updateValueAndValidity();
            this.StyleUpdateForm.controls['opacity'].updateValueAndValidity();
            this.StyleUpdateForm.controls['weight'].updateValueAndValidity();

            this.StyleUpdateForm.controls['fillColor'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['fillOpacity'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['dashArray'].setValidators([Validators.required]);
            this.StyleUpdateForm.controls['fillColor'].updateValueAndValidity();
            this.StyleUpdateForm.controls['fillOpacity'].updateValueAndValidity();
            this.StyleUpdateForm.controls['dashArray'].updateValueAndValidity();

        }
    }

    private isNumberOrNull(value: string | number): boolean
    {
        return ((value == null) ||
            (value == '') ||
            !isNaN(Number(value.toString())));
    }

    handleSubmitClick() : void {
        if (!this.StyleUpdateForm.valid) {
            this.toaster.sendErrorMessage('context.update.formErrors.unknownError');
        } else {
            const name = this.nameControl.value;
            const type = this.typeControl.value;

            //point
            const iconGlyph = this.iconGlyphControl.value;
            const iconColor = this.iconColorControl.value;
            const iconShape = this.iconShapeControl.value;

            //LINE + POLYGON
            const color = this.colorControl.value;
            const opacity = this.opacityControl.value;
            const weight = this.weightControl.value;

            //LINE
            const iconAnchor = this.iconAnchorControl.value;

            //POLYGON
            const fillColor = this.fillColorControl.value;
            const fillOpacity = this.fillOpacityControl.value;
            const dashArray= this.dashArrayControl.value;

            let errorType = false;

            if(type == "POLYGON" || type == "LINE"){
                if(!this.isNumberOrNull(weight)){
                    this.toaster.sendErrorMessage('style.update.formErrors.weight');
                    errorType = true;
                }
                if(!this.isNumberOrNull(opacity) && !errorType){
                    this.toaster.sendErrorMessage('style.update.formErrors.opacity');
                    errorType = true;
                }
            }

            if(type == "POLYGON"){
                if(!this.isNumberOrNull(fillOpacity) && !errorType){
                    this.toaster.sendErrorMessage('style.update.formErrors.fillOpacity');
                    errorType = true;
                }
            }

            if(!errorType) {
                const resStyle: Style = {
                    color: color,
                    dashArray: dashArray.split(','),
                    fillColor: fillColor,
                    fillOpacity: fillOpacity,
                    filtering: true,
                    geometry: "",
                    iconAnchor: iconAnchor.split(','),
                    iconColor: iconColor,
                    iconGlyph: iconGlyph,
                    iconShape: iconShape,
                    opacity: opacity,
                    type: type,
                    weight: weight,
                };

                const res: StyleContainer = {
                    name: name,
                    type: type,
                    style: resStyle

                }
                this.dialogRef.close(res);
            }
        }
    }
}
