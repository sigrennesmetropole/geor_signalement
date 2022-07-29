import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {ToasterUtil} from "../../utils/toaster.util";
import {TranslateService} from "@ngx-translate/core";
import { ProcessStyling} from "../../api/models";
import {StyleService} from "../../services/style.service";
import {catchError} from "rxjs/operators";
import { EMPTY, Observable} from 'rxjs';
import {StyleProcessAddDialog} from "./style-process-add-dialog/style-process-add-dialog";
import {StyleDataSource} from "../style.datasource";
import {StyleProcessDeleteDialog} from "./style-process-delete-dialog/style-process-delete-dialog";

 @Component({
   selector: 'style-process-dialog',
   templateUrl: 'style-process-dialog.html',
   styleUrls: ['style-process-dialog.scss'],
 })

/**
  * The dialog window to add a context
  */
export class StyleProcessDialog implements OnInit {
     tasks: Observable<Array<ProcessStyling>>;
     error: boolean;
     values: Array<ProcessStyling>

     dataSource: StyleDataSource;

     constructor(
         private dialogRef: MatDialogRef<StyleProcessDialog>,
         private toaster: ToasterUtil,
         private translateService: TranslateService,
         public dialog: MatDialog,
         private styleService: StyleService,
         StyleDataSource: StyleDataSource,
         @Inject(MAT_DIALOG_DATA) private data: any
     ) {
        this.dataSource = StyleDataSource;
        this.error = false;
        this.tasks = new Observable<Array<ProcessStyling>>();
        this.values = Array<ProcessStyling>();
     }

     ngOnInit(): void {
         this.tasks = this.styleService.getListProcessStyling(this.data.target.id).pipe(
             catchError(error => {
                 this.error = true;
                 console.error(error);
                 return EMPTY;
             })
         );
     }

     handleRefreshDataClick(): void {
         this.refreshData()
     }

     refreshData():void{
         this.ngOnInit();
     }

     handleOpenAddDialogClick() {
         this.dialog.open(StyleProcessAddDialog, {
             width: 'auto',
             height: 'auto',
             data: {id: this.data.target.id},
         }).afterClosed().subscribe(
             (data) => {
                 if (data) {
                     this.dataSource.postStyleProcess(data, this);
                 }
             },
         );
     }

     handleOpenDeleteDialogClick(target : ProcessStyling) {
         this.dialog.open(StyleProcessDeleteDialog, {
             width: 'auto',
             height: 'auto',
             data: {target: target},
         }).afterClosed().subscribe((result)=>{
             if (result && result === 'Confirm') {
                 this.dataSource.deleteStyleProcess(target, this);
             }
         });
     }
 }
