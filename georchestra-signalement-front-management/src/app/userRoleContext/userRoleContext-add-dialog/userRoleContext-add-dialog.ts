import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {MatDialogRef} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {Observable, of} from 'rxjs';
import {ContextDescription, GeographicArea, Role, User}
  from 'src/app/api/models';
import {ContextDescriptionsService, GeographicAreasService,
  RolesService, UserService} from 'src/app/api/services';
import {ToasterUtil} from 'src/app/utils/toaster.util';

@Component({
  selector: 'userRoleContext-add-dialog',
  templateUrl: 'userRoleContext-add-dialog.html',
  styleUrls: ['userRoleContext-add-dialog.scss'],
})
/**
* Dialog window to add a UserRoleContext
*/
export class UserRoleContextAddDialog {
  NUMBER_ELEMENTS_IN_FILTER = 10;

  public filteredUsers:User[]=[];
  userLogin:string = '';
  totalUsers:number = 0;
  public userFormControl = new FormControl('', [Validators.required]);

  public filteredRoles:Role[]=[];
  totalRoles:number = 0;
  public roleFormControl = new FormControl('', [Validators.required]);

  public filteredGeographics:GeographicArea[]=[];
  geographicName:string='';
  totalGeographics:number=0;
  public geographicFormControl = new FormControl('', [Validators.required]);

  public filteredContexts:ContextDescription[]=[];
  contextLabel:string = '';
  totalContexts:number=0;
  public contextFormControl = new FormControl('', [Validators.required]);

  public userRoleContextAddForm = new FormGroup({
    user: this.userFormControl,
    role: this.roleFormControl,
    geographic: this.geographicFormControl,
    context: this.contextFormControl,
  });

  /**
  * The constructor of the add dialog
  * @param {MatDialogRef} dialogRef Reference to the UserRoleContextAddDialog
  * @param {ToasterUtil} toaster The toaster to display messages
  */
  constructor(
    public dialogRef: MatDialogRef<UserRoleContextAddDialog>,
    public toasterUtil: ToasterUtil,
    public userService: UserService,
    private roleService: RolesService,
    private geographicAreaService: GeographicAreasService,
    private contextDescriptionService: ContextDescriptionsService,
    private translateService: TranslateService) {
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
    * When user clicks on submit, check if all fields are filled
    * Then call the service to create the UserRoleContext
    */
  handleSubmitClick() {
    if (this.userFormControl.value == null) {
      this.toasterUtil.sendErrorMessage('userRoleContext.add.noUser');
    } else if (this.roleFormControl.value == null) {
      this.toasterUtil.sendErrorMessage('userRoleContext.add.noRole');
    } else if (this.geographicFormControl.value == null) {
      this.toasterUtil.sendErrorMessage('userRoleContext.add.noGeographic');
    } else if (this.roleFormControl.value == null) {
      this.toasterUtil.sendErrorMessage('userRoleContext.add.noContext');
    } else {
      const result = {
        user: this.userFormControl.value,
        role: this.roleFormControl.value,
        geographicArea: this.geographicFormControl.value,
        contextDescription: this.contextFormControl.value,
      };
      this.dialogRef.close(result);
    }
  }

  /**
    * Check if data are valid
    * @return {boolean}
    */
  validData() : boolean {
    return this.userRoleContextAddForm.valid;
  }

  /**
   * load the next users in select
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
          this.filteredUsers = this.filteredUsers
              .concat(data.results ?? []),
          this.totalUsers = data.totalItems ?? 0;
        },
    );
  }

    userTimeout:any;
    /**
     * @param {any} target the select
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
     * load next roles in the select
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
     * @param {any} target the select
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
            },
        );
      }, 250);
    }

    /**
     * Load next geographics in the select
     */
    getNextGeographics():void {
      const params = {
        name: this.geographicName,
        offset: Math.ceil(this.filteredGeographics.length/
          this.NUMBER_ELEMENTS_IN_FILTER),
        limit: this.NUMBER_ELEMENTS_IN_FILTER,
      };
      this.geographicAreaService.searchGeographicAreas(params).subscribe(
          (data)=>{
            this.filteredGeographics = this.filteredGeographics
                .concat(data.results ?? []),
            this.totalGeographics = data.totalItems ?? 0;
          },
      );
    }

    geographicsTimeout:any;
    /**
     * @param {any} target the select
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
     * @return {Observable<String>}
     */
    getUserError(): Observable<String> {
      if (this.userFormControl.hasError('required')) {
        return this.translateService.get('userRoleContext.add.noUser');
      } else {
        return of('');
      }
    }

    /**
     * @return {Observable<String>}
     */
    getRoleError(): Observable<String> {
      if (this.roleFormControl.hasError('required')) {
        return this.translateService.get('userRoleContext.add.noRole');
      } else {
        return of('');
      }
    }

    /**
     * @return {Observable<String>}
     */
    getGeographicError(): Observable<String> {
      if (this.geographicFormControl.hasError('required')) {
        return this.translateService
            .get('userRoleContext.add.noGeographicArea');
      } else {
        return of('');
      }
    }

    /**
     * @return {Observable<String>}
     */
    getContextError(): Observable<String> {
      if (this.contextFormControl.hasError('required')) {
        return this.translateService
            .get('userRoleContext.add.noContextDescription');
      } else {
        return of('');
      }
    }
}
