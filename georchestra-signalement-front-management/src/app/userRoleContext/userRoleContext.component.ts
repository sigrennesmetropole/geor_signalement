import {AfterViewInit, Component, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {MatTable} from '@angular/material/table';
import {of} from 'rxjs';
import {catchError, mergeMap} from 'rxjs/operators';
import {ContextDescription, GeographicArea, Role, User, UserRoleContext}
  from '../api/models';
import {ContextDescriptionsService, GeographicAreasService,
  RolesService, UserService} from '../api/services';
import {ToasterUtil} from '../utils/toaster.util';
import {UserRoleContextAddDialog}
  from './userRoleContext-add-dialog/userRoleContext-add-dialog';
import {ConfirmUserRoleContextDeleteDialog}
  from './userRoleContext-delete-dialog/confirm-delete-dialog/confirmUserRoleContext-delete-dialog';
import {UserRoleContextDeleteDialog}
  from './userRoleContext-delete-dialog/userRoleContext-delete-dialog';
import {UserRoleContextDataSource} from './userRoleContext.datasource';

@Component({
  selector: 'app-user',
  templateUrl: './userRoleContext.component.html',
  styleUrls: ['./userRoleContext.component.scss'],
})
/**
* UserRoleContext component
* Used to display UserRoleContext
* Allow add, delete and search of UserRoleContext
*/
export class UserRoleContextComponent implements AfterViewInit {
  /** Columns displayed in the table*/
  displayedColumns = ['user', 'role', 'geographicArea',
    'context', 'actions'];

  NUMBER_ELEMENTS_IN_FILTER = 10;

  public filteredUsers:User[]=[];
  userLogin:string = '';
  totalUsers:number = 0;
  public userFormControl = new FormControl();

  public filteredRoles:Role[]=[];
  totalRoles:number = 0;
  public roleFormControl = new FormControl()

  public filteredGeographics:GeographicArea[]=[];
  geographicName:string='';
  totalGeographics:number=0;
  public geographicFormControl = new FormControl();

  public filteredContexts:ContextDescription[]=[];
  contextLabel:string = '';
  totalContexts:number=0;
  public contextFormControl = new FormControl();

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;
  @ViewChild(MatTable) table!: MatTable<UserRoleContext>;
  dataSource: UserRoleContextDataSource;

  /**
  * UserRoleContext Component Constructor
  * @param {UserRoleContextDataSource} userDataSource
  * @param {MatDialo} dialog
  */
  constructor(userDataSource :UserRoleContextDataSource,
    public toaster: ToasterUtil,
    private userService: UserService,
    private roleService: RolesService,
    private geographicAreaService: GeographicAreasService,
    private contextDescriptionService: ContextDescriptionsService,
    public dialog: MatDialog) {
    this.dataSource = userDataSource;
    this.userService.searchUsers({limit: this.NUMBER_ELEMENTS_IN_FILTER})
        .subscribe(
            (page) =>{
              this.filteredUsers = page.results ?? [];
              this.totalUsers = page.totalItems ?? 0;
            },
        );
    this.roleService.getRoles({limit: this.NUMBER_ELEMENTS_IN_FILTER})
        .subscribe(
            (page) => {
              this.filteredRoles = page.results ?? [];
              this.totalRoles = page.totalItems ?? 0;
            },
        );
    this.geographicAreaService
        .searchGeographicAreas({limit: this.NUMBER_ELEMENTS_IN_FILTER})
        .subscribe(
            (page) => {
              this.filteredGeographics = page.results ?? [];
              this.totalRoles = page.totalItems ?? 0;
            },
        );
    this.contextDescriptionService
        .searchContextDescriptions({limit: this.NUMBER_ELEMENTS_IN_FILTER})
        .subscribe(
            (page)=>{
              this.filteredContexts = page.results ?? [];
            },
        );
  }
  /**
    * Init
    */
  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.table.dataSource = this.dataSource;
    this.resetFilters();
  }

  /**
    * When the user clicks on the delete button open the suprresion dialog
    * @param {UserRoleContext} target The UserRoleContextItem to delete
    */
  handleOpenSupressDialogClick(target: UserRoleContext): void {
    const dialog = this.dialog.open(UserRoleContextDeleteDialog, {
      width: 'auto',
      height: 'auto',
      data: target,
    },
    );

    dialog.afterClosed().pipe(
        mergeMap(
            (result)=>{
              if (result == 'Confirm') {
                return this.dataSource.deleteUserRoleContext(target, false);
              } else {
                return of('Canceled');
              }
            }),
        catchError(
            (response)=>{
              if (response.error.code == 'errors.usedObject') {
                return this.dialog.open(ConfirmUserRoleContextDeleteDialog, {
                  width: 'auto',
                  height: 'auto',
                },
                ).afterClosed().pipe(
                    mergeMap(
                        (result)=>{
                          if (result == 'Confirm') {
                            return this.dataSource
                                .deleteUserRoleContext(target, true);
                          } else {
                            return of('Canceled');
                          }
                        },
                    ),
                );
              } else {
                return of(response);
              }
            },
        ),
    ).subscribe(
        (result)=>{
          if (!result) {
            this.toaster.sendSuccessMessage('userRoleContext.delete.success');
            this.dataSource.refreshData();
          } else if (result !='Canceled') {
            this.toaster.sendErrorMessage('userRoleContext.delete.failed',
                result);
          }
        },
        (response)=>{
          this.toaster.sendErrorMessage('userRoleContext.delete.failed',
              response.error.code);
        },
    );
  }

  /**
      * When the user clicks on the upload button, open the add Dialog
      * If a result is returned, add the UserRoleContext
      */
  handleOpenAddDialogClick(): void {
    const dialog = this.dialog.open(UserRoleContextAddDialog, {
      width: 'auto',
      height: 'auto',
      data: this.dataSource,
    });
    dialog.afterClosed().subscribe(
        (result)=>{
          if (result) {
            const userLogin = result.user.login;
            const roleName = result.role.name;
            const geographicAreaId = result.geographicArea.id;
            const contextName = result.contextDescription.name;
            this.dataSource.createUserRoleContext(userLogin, roleName,
                geographicAreaId, contextName);
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
   * Load next users for the filter
   */
  getNextUsers():void {
    const params = {
      login: this.userLogin,
      offset: Math.floor(this.filteredUsers.length/
        this.NUMBER_ELEMENTS_IN_FILTER),
      limit: this.NUMBER_ELEMENTS_IN_FILTER,
    };
    this.userService.searchUsers(params).subscribe(
        (data)=>{
          this.filteredUsers = this.filteredUsers.concat(data.results ?? []),
          this.totalUsers = data.totalItems ?? 0;
        },
    );
  }

  userTimeout:any;
  /**
   * @param {any} target The select to get t
   */
  updateFilteredUsers(target:any): void {
    clearTimeout(this.userTimeout);
    this.userTimeout = setTimeout(()=>{
      this.userLogin = target.value;
      const params = {
        login: target.value,
        offset: 0,
        limit: this.NUMBER_ELEMENTS_IN_FILTER,
      };
      this.userService.searchUsers(params).subscribe(
          (data)=>{
            this.filteredUsers = data.results ?? [];
            this.totalUsers = data.totalItems ?? 0;
          },
      );
    }, 250);
  }

  /**
   * Update User Filter
   */
  updateUserFilter(): void {
    this.dataSource.updateUserFilter(this.userFormControl.value);
  }

  /**
   * Load next roles for role select
   */
  getNextRoles():void {
    const params = {
      offset: Math.floor(this.filteredRoles.length/
        this.NUMBER_ELEMENTS_IN_FILTER),
      limit: this.NUMBER_ELEMENTS_IN_FILTER,
    };
    this.roleService.getRoles(params).subscribe(
        (data)=>{
          this.filteredRoles = this.filteredRoles
              .concat(data.results ?? []),
          this.totalRoles = data.totalItems ?? 0;
        },
    );
  }

  roleTimeout:any;
  /**
   * @param {any} target select
   */
  updateFilteredRoles(target:any): void {
    clearTimeout(this.roleTimeout);
    this.roleTimeout = setTimeout(()=>{
      const params = {
        limit: this.NUMBER_ELEMENTS_IN_FILTER,
      };
      this.roleService.getRoles(params).subscribe(
          (data)=>{
            this.filteredRoles = data.results ?? [];
            this.totalRoles = data.totalItems ?? 0;
          },
      );
    }, 250);
  }

  /**
   * Update the role filter
   */
  updateRoleFilter(): void {
    this.dataSource.updateRoleFilter(this.roleFormControl.value);
  }

  /**
   * Get the next geographics to add in select
   */
  getNextGeographics():void {
    const params = {
      name: this.geographicName,
      offset: Math.ceil(this.filteredGeographics.length/
        this.NUMBER_ELEMENTS_IN_FILTER),
      limit: this.NUMBER_ELEMENTS_IN_FILTER,
    };
    this.geographicAreaService.searchGeographicAreas(params)
        .subscribe(
            (data)=>{
              this.filteredGeographics = this.filteredGeographics
                  .concat(data.results ?? []),
              this.totalGeographics = data.totalItems ?? 0;
            },
        );
  }

  geographicsTimeout:any;
  /**
   * @param {any} target The select
   */
  updateFilteredGeographics(target:any): void {
    clearTimeout(this.geographicsTimeout);
    this.geographicsTimeout = setTimeout(()=>{
      this.geographicName = target.value;
      const params = {
        name: target.value,
        limit: this.NUMBER_ELEMENTS_IN_FILTER,
      };
      this.geographicAreaService.searchGeographicAreas(params)
          .subscribe(
              (data)=>{
                this.filteredGeographics = data.results ?? [];
                this.totalGeographics = data.totalItems ?? 0;
              },
          );
    }, 250);
  }

  /**
   * Update the goegraphic filter
   */
  updateGeographicFilter(): void {
    this.dataSource
        .updateGeographicFilter(this.geographicFormControl.value);
  }

  /**
   * Load next contexts in the select
   */
  getNextContexts():void {
    const params = {
      description: this.contextLabel,
      offset: this.filteredContexts.length/
      this.NUMBER_ELEMENTS_IN_FILTER,
      limit: this.NUMBER_ELEMENTS_IN_FILTER,
    };
    this.contextDescriptionService.searchContextDescriptions(params)
        .subscribe(
            (data)=>{
              this.filteredContexts = this.filteredContexts
                  .concat(data.results ?? []),
              this.totalContexts = data.totalItems ?? 0;
            },
        );
  }

  contextTimeout:any;
  /**
   * @param {any} target the select
   */
  updateFilteredContexts(target:any): void {
    clearTimeout(this.geographicsTimeout);
    this.contextTimeout = setTimeout(()=>{
      this.contextLabel = target.value;
      const params = {
        limit: this.NUMBER_ELEMENTS_IN_FILTER,
        description: this.contextLabel,
      };
      this.contextDescriptionService.searchContextDescriptions(params)
          .subscribe(
              (data)=>{
                this.filteredContexts = data.results ?? [];
                this.totalContexts = data.totalItems ?? 0;
              },
          );
    }, 250);
  }

  /**
   * Update the context filter
   */
  updateContextFilter(): void {
    this.dataSource
        .updateContextDescriptionFilter(this.contextFormControl.value);
  }

  /**
   * Reset the filters and reload data
   */
  resetFilters(): void {
    this.dataSource.resetFilters();
    this.userFormControl.reset();
    this.roleFormControl.reset();
    this.geographicFormControl.reset();
    this.contextFormControl.reset();
  }
}

