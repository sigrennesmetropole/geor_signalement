import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {Role} from '../api/models';
import {RoleAddDialog} from './role-add-dialog/role-add-dialog';
import {RoleDeleteDialog} from './role-delete-dialog/role-delete-dialog';
import {RoleDataSource} from './role.datasource';

@Component({
  selector: 'app-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.scss'],
})
/**
 * Role Component
 * Used to display roles
 * Allow add and delete of roles
 */
export class RoleComponent implements AfterViewInit {
  displayedColumns = ['name', 'label', 'actions'];

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<Role>;
  dataSource: RoleDataSource;

  /**
   * Constructor of Role Component
   * @param {RoleDataSource} roleDataSource
   * @param {MatDialog} dialog
   */
  constructor(roleDataSource: RoleDataSource,
    public dialog: MatDialog) {
    this.dataSource = roleDataSource;
  }

  /**
   * Init
   */
  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
  }

  /**
   * When the user clicks on the delete button open the suprresion dialog
   * @param {Role} target The Role to delete
   */
  handleOpenSupressDialogClick(target: Role): void {
    const dialog = this.dialog.open(RoleDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: target,
    },
    );

    dialog.afterClosed().subscribe(
        (result)=>{
          if (result && result == 'Confirm' && target.name) {
            this.dataSource.deleteRole(target.name);
          }
        },
    );
  }

  /**
      * When the user clicks on the upload button, open the add Dialog
      * If a result is returned, add the user
      */
  handleOpenAddDialogClick(): void {
    const dialog = this.dialog.open(RoleAddDialog, {
      width: 'auto',
      height: 'auto',
    });
    dialog.afterClosed().subscribe(
        (result)=>{
          if (result) {
            this.dataSource.createRole(result.name, result.label);
          }
        });
  }
  /**
   * When the user clicks on the refresh button, refresh data
   */
  handleRefreshDataClick(): void {
    this.dataSource.refreshData();
  }
}
