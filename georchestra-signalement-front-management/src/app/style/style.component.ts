import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTable} from "@angular/material/table";
import {StyleDataSource} from "../style/style.datasource";
import {MatDialog} from "@angular/material/dialog";
import {StyleContainer} from "../api/models/style-container";
import {StyleReadDialog} from "./style-read-dialog/style-read-dialog";
import {StyleUpdateDialog} from "./style-update-dialog/style-update-dialog";
import {StyleDeleteDialog} from "./style-delete-dialog/style-delete-dialog";
import {StyleAddDialog} from "./style-add-dialog/style-add-dialog";
import {StyleProcessDialog} from "./style-process-dialog/style-process-dialog";
@Component({
  selector: 'app-style',
  templateUrl: './style.component.html',
  styleUrls: ['./style.component.scss']
})

export class StyleComponent implements AfterViewInit {

  displayedColumns = ['id', 'name', 'type', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<StyleContainer>;
  dataSource: StyleDataSource;

  constructor(StyleDataSource: StyleDataSource,
              public dialog: MatDialog) {
    this.dataSource = StyleDataSource;
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

  handleRefreshDataClick(): void {
    this.dataSource.refreshData();
  }

  handleOpenReadDialogClick(target: StyleContainer) {
    this.dialog.open(StyleReadDialog, {
      width: 'auto',
      height: 'auto',
      data: { target: target},
    });
  }
  handleOpenLinkDialogClick(target: StyleContainer) {
    this.dialog.open(StyleProcessDialog, {
      width: 'auto',
      height: 'auto',
      data: { target: target},
    });
  }

  handleOpenUpdateDialogClick(target: StyleContainer) {
    this.dialog.open(StyleUpdateDialog, {
      width: 'auto',
      height: 'auto',
      data: {target: target},
    }).afterClosed().subscribe(
        (data)=>{
          if (data) {
            this.dataSource.updateStyle(target, data);
          }
        },
    );
  }

  handleOpenDeleteDialogClick(target: StyleContainer) {
    const dialog = this.dialog.open(StyleDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: { target: target},
    })

    dialog.afterClosed().subscribe((result)=>{
      if (result && result === 'Confirm') {
        this.dataSource.deleteStyle(target);
      }
    });
  }

  handleOpenAddDialogClick() {
    this.dialog.open(StyleAddDialog, {
      width: 'auto',
      height: 'auto',
    }).afterClosed().subscribe(
        (data)=>{
          if (data) {
            this.dataSource.postStyle(data);
          }
        },
    );
  }
}

