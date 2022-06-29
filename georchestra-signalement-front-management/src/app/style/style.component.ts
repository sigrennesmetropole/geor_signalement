import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatTable} from "@angular/material/table";
import {Style} from "../api/models/style";
import {StyleDataSource} from "../style/style.datasource";
import {MatDialog} from "@angular/material/dialog";
import {StyleContainer} from "../api/models/style-container";
import {ContextItem} from "../context/context.datasource";
import {ContextEditDialog} from "../context/context-edit-dialog/context-edit-dialog";

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
}