import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {ToasterUtil} from "../../utils/toaster.util";
import {TranslateService} from "@ngx-translate/core";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {GeographicType, Style, StyleContainer, Validator} from "../../api/models";

/**
 * @title Dialog Overview
 */



 @Component({
   selector: 'style-add-dialog',
   templateUrl: 'style-add-dialog.html',
   styleUrls: ['style-add-dialog.scss'],
 })

/**
  * The dialog window to add a style
  */
export class StyleAddDialog{

    public nameControl = new FormControl('', [Validators.required]);
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

    private patternDouble = "([1-9][0-9]*|[0-9])([.][0-9]*)?"

    public StyleAddForm = new FormGroup({
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

    public styleTypeEnum= GeographicType;

    constructor(
        private dialogRef: MatDialogRef<StyleAddDialog>,
        private toaster: ToasterUtil,
        private translateService: TranslateService,
        @Inject(MAT_DIALOG_DATA) private data:any
    )
    {
        // @ts-ignore
       /*this.StyleAddForm.get('type').valueChanges.subscribe(val => {
            if (val == "POINT") {
                this.StyleAddForm.controls['iconGlyph'].setValidators([Validators.required]);
                this.StyleAddForm.controls['iconColor'].setValidators([Validators.required]);
                this.StyleAddForm.controls['iconShape'].setValidators([Validators.required]);
            } else {
                this.StyleAddForm.controls['iconGlyph'].clearValidators();
                this.StyleAddForm.controls['iconColor'].clearValidators();
                this.StyleAddForm.controls['iconShape'].clearValidators();
            }
            this.StyleAddForm.controls['iconGlyph'].updateValueAndValidity();
            this.StyleAddForm.controls['iconColor'].updateValueAndValidity();
            this.StyleAddForm.controls['iconShape'].updateValueAndValidity();

           if (val == "POLYGON" || val == "LINE") {
               this.StyleAddForm.controls['color'].setValidators([Validators.required]);
               this.StyleAddForm.controls['opacity'].setValidators([Validators.required,Validators.pattern(this.patternDouble)]);
               this.StyleAddForm.controls['weight'].setValidators([Validators.required]);
           } else {
               this.StyleAddForm.controls['color'].clearValidators();
               this.StyleAddForm.controls['opacity'].clearValidators();
               this.StyleAddForm.controls['weight'].clearValidators();
           }
           this.StyleAddForm.controls['color'].updateValueAndValidity();
           this.StyleAddForm.controls['opacity'].updateValueAndValidity();
           this.StyleAddForm.controls['weight'].updateValueAndValidity();

           if (val == "LINE") {
               this.StyleAddForm.controls['iconAnchor'].setValidators([Validators.required]);
           } else {
               this.StyleAddForm.controls['iconAnchor'].clearValidators();
           }
           this.StyleAddForm.controls['iconAnchor'].updateValueAndValidity();

           if (val == "POLYGON") {
               this.StyleAddForm.controls['fillColor'].setValidators([Validators.required]);
               this.StyleAddForm.controls['fillOpacity'].setValidators([Validators.required,Validators.pattern(this.patternDouble)]);
               this.StyleAddForm.controls['dashArray'].setValidators([Validators.required]);
           } else {
               this.StyleAddForm.controls['fillColor'].clearValidators();
               this.StyleAddForm.controls['fillOpacity'].clearValidators();
               this.StyleAddForm.controls['dashArray'].clearValidators();
           }
           this.StyleAddForm.controls['fillColor'].updateValueAndValidity();
           this.StyleAddForm.controls['fillOpacity'].updateValueAndValidity();
           this.StyleAddForm.controls['dashArray'].updateValueAndValidity();
        });*/
    }

    private isNumberOrNull(value: string | number): boolean
    {
        return ((value == null) ||
            (value == '') ||
            !isNaN(Number(value.toString())));
    }

    handleSubmitClick() : void {
        if (!this.StyleAddForm.valid) {
            this.toaster.sendErrorMessage('style.edit.formErrors.unknownError');
        } else {
            const name = this.nameControl.value;
            const type = this.typeControl.value;

            //POINT
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
                if(this.isNumberOrNull(weight)){
                    this.toaster.sendErrorMessage('style.add.formErrors.weight');
                    errorType = true;
                }
                if(this.isNumberOrNull(opacity) && !errorType){
                    this.toaster.sendErrorMessage('style.add.formErrors.opacity');
                    errorType = true;
                }
            }

            if(type == "POLYGON"){
                if(this.isNumberOrNull(fillOpacity) && !errorType){
                    this.toaster.sendErrorMessage('style.add.formErrors.fillOpacity');
                    errorType = true;
                }
            }

            if(!errorType){
                const resStyle : Style = {
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
                const res : StyleContainer = {
                    name : name,
                    type : type,
                    style: resStyle

                }
                this.dialogRef.close(res);
            }
        }
    }
}
