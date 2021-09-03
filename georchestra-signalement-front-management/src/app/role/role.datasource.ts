import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {delayWhen, map, tap} from 'rxjs/operators';
import {Observable,
  merge, Subject, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {ToasterUtil} from '../utils/toaster.util';
import {TranslateService} from '@ngx-translate/core';
import {Role} from '../api/models';
import {RoleService} from '../services/role.service';


@Injectable()
/**
  * Data source for the Role view. This class should
  * encapsulate all logic for fetching and manipulating the displayed data
  * (including sorting, pagination, and filtering).
  */
export class RoleDataSource extends DataSource<Role> {
    paginator: MatPaginator | undefined;
    sort: MatSort | undefined;
    data: Role[]=[];
    sortCriteria:string = '';
    totalItems:number=0;

    actualize: Subject<void> = new Subject<void>();

    /**
    * DataSource constructor
    * @param {roleService} roleService to interract with roles
    * @param {ToasterUtil} toasterService to display messages
    * @param {TranslateService} translateService to translate
    */
    constructor(private roleService: RoleService,
      private toasterService:ToasterUtil,
      public translateService: TranslateService) {
      super();

      this.loadData().pipe(tap(()=>{
        this.actualize.next();
      }));
    }

    /**
      * Connect this data source to the table. The table will only update when
      * the returned stream emits new items.
      * @return {Observable<Role[]>} A stream of the items to be rendered.
      */
    connect(): Observable<Role[]> {
      if (this.paginator && this.sort) {
        // Combine everything that affects the rendered data into one update
        // stream for the data-table to consume.
        return merge(of(this.data), this.paginator.page,
            this.sort.sortChange, this.actualize)
            .pipe(tap(() => {
              this.sortedParameters();
            }))
            .pipe(delayWhen((event)=>this.loadData()))
            .pipe(map(()=>{
              return this.data;
            }));
      } else {
        throw Error('Please set paginator and sort on the data source.');
      }
    }

    /**
      *  Called when the table is being destroyed. Use this function,
      * to clean up any open connections or free any held resources
      * that were set up during connect.
      */
    disconnect(): void {}


    /**
      * Set the parameter to be used for the sort (server-side)
      * of the data.
      */
    private sortedParameters(): void {
      this.sortCriteria = '';
      if (this.sort) {
        if (this.sort?.direction == 'desc') {
          this.sortCriteria = '-';
        }
        switch (this.sort?.active) {
          case 'name': {
            this.sortCriteria+='name';
            break;
          }
          default: this.sortCriteria='';
        }
      }
    }

    /**
      * Refresh the data in our datasource
      * @return {Observable<Role[]>} return an observable on data
      */
    private loadData(): Observable<Role[]> {
      return this.roleService
          .getRoles(this.paginator?.pageIndex ?? 0,
              this.paginator?.pageSize ?? 10,
              this.sortCriteria)
          .pipe(map(
              (response)=>{
                if (response.body) {
                  this.data = response.body.results ?? [];
                  this.totalItems = response.body.totalItems ?? 0;
                }
                return this.data;
              }),
          );
    }

    /**
     * Function called to ask a refresh of data
     */
    public refreshData() : void {
      this.actualize.next();
    }

    /**
     * Function used to remove a Role
     * @param {string} name the name of the role to remove
     */
    public deleteRole(name : string) : void {
      this.roleService.deleteRole(name).subscribe(
          ()=>{
            this.toasterService.sendSuccessMessage('role.delete.success');
          },
          (response)=>{
            console.log(response)
            this.toasterService.sendErrorMessage('role.delete.error',
                response.error);
          },
          ()=>{
            this.refreshData();
          },
      );
    }

    /**
     * Function called to create a role
     * @param {string} name The name for the role
     * @param {string} label The label for the role
     */
    createRole(name: string, label: string) : void {
      this.roleService.createRole(name, label).subscribe(
          (result)=>{
            if (result) {
              this.toasterService.sendSuccessMessage('role.add.success');
            } else {
              this.toasterService.sendErrorMessage('role.add.error');
            }
          },
          (response)=>{
            this.toasterService.sendErrorMessage('role.add.error',
                response.error);
          },
          ()=>{
            this.refreshData();
          },
      );
    }
}

