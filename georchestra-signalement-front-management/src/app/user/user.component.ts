import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {UserAddDialog} from './user-add-dialog/user-add-dialog';
import {UserDeleteDialog} from './user-delete-dialog/user-delete-dialog';
import {UserDataSource, UserItem} from './user.datasource';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
/**
* User component
* Used to display users
* Allow add, delete and search of users
*/
export class UserComponent implements AfterViewInit {
  /** Columns displayed in the table*/
  displayedColumns = ['login', 'email', 'firstname',
    'lastname', 'organization', 'actions'];

  emailFilter = '';
  loginFilter = '';

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<UserItem>;
  dataSource: UserDataSource;

  /**
  * User Component Constructor
  * @param {UserDataSource} userDataSource
  * @param {MatDialo} dialog
  */
  constructor(userDataSource :UserDataSource,
    public dialog: MatDialog) {
    this.dataSource = userDataSource;
    this.loginFilter = this.dataSource.loginFilter;
    this.emailFilter = this.dataSource.emailFilter;
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
    * @param {UserItem} target The UserItem to delete
    */
  handleOpenSupressDialogClick(target: UserItem): void {
    const dialog = this.dialog.open(UserDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: target,
    },
    );

    dialog.afterClosed().subscribe(
        (result)=>{
          if (result && result === 'Confirm') {
            this.dataSource.deleteUser(target);
          }
        },
    );
  }

  /**
      * When the user clicks on the upload button, open the add Dialog
      * If a result is returned, add the user
      */
  handleOpenAddDialogClick(): void {
    const dialog = this.dialog.open(UserAddDialog, {
      width: 'auto',
      height: 'auto',
      data: this.dataSource,
    });
    dialog.afterClosed().subscribe(
        (result)=>{
          if (result) {
            this.dataSource.createUser(result.login, result.email);
          }
        });
  }

  /**
        * When the user clicks on the refresh button, refresh data
        */
  handleRefreshDataClick(): void {
    this.dataSource.refreshData();
  }

  /**
        * Function not developed yet
        * @param {UserItem} target The user to open
        */
  handleOpenUserView(target: UserItem): void {
    throw new Error('Function not implemented.');
  }

  timeoutFilters:any;
  /**
   * Update search filters
   */
  updateFilters(): void {
    clearTimeout(this.timeoutFilters);
    this.timeoutFilters = setTimeout(()=>{
      this.dataSource.updateFilters(this.loginFilter, this.emailFilter);
    }, 250);
  }
}

