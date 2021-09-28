import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {delayWhen, map, tap} from 'rxjs/operators';
import {Observable,
  merge, Subject, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {ContextDescription, GeographicArea, Role, UserRoleContext}
  from '../api/models';
import {OperatorService} from '../services/operator.service';
import {User} from '../api/models';
import {ToasterUtil} from '../utils/toaster.util';

  @Injectable()
  /**
  * Data source for the UserRoleContext view. This class should
  * encapsulate all logic for fetching and manipulating the displayed data
  * (including sorting, pagination, and filtering).
  */
export class UserRoleContextDataSource extends DataSource<UserRoleContext> {
    paginator: MatPaginator | undefined;
    sort: MatSort | undefined;
    data: UserRoleContext[]=[];
    sortCriteria:string = '';
    totalItems:number=0;
    userFilter:User|undefined;
    roleFilter:Role|undefined;
    geographicAreaFilter:GeographicArea|undefined;
    contextFilter:ContextDescription|undefined;

    actualize: Subject<void> = new Subject<void>();

    /**
    * DataSource constructor
    * @param {ToasterUtil} toasterService to display messages
    * @param {TranslateService} translateService to translate
    */
    constructor(
      public operatorService: OperatorService,
      public toaster: ToasterUtil) {
      super();

      this.loadData().pipe(tap(()=>{
        this.actualize.next();
      }));
    }

    /**
      * Connect this data source to the table. The table will only update when
      * the returned stream emits new items.
      * @return {Observable<UserRoleContext[]>}
      */
    connect(): Observable<UserRoleContext[]> {
      if (this.paginator && this.sort) {
        // Combine everything that affects the rendered data into one update
        // stream for the data-table to consume.
        return merge(of(this.data), this.paginator.page,
            this.sort.sortChange, this.actualize)
            .pipe(
                tap(() => {
                  this.sortedParameters();
                }),
                delayWhen((event)=>this.loadData()),
                map(()=>{
                  return this.data;
                }));
      } else {
        throw Error('Please set paginator and sort on the data source.');
      }
    }

    /**
     * Handle sort parameters management
     */
    private sortedParameters() : void {
      this.sortCriteria = '';
      if (this.sort) {
        if (this.sort?.direction == 'desc') {
          this.sortCriteria = '-';
        }
        switch (this.sort?.active) {
          case 'user': {
            this.sortCriteria+='user';
            break;
          }
          case 'role': {
            this.sortCriteria+='role';
            break;
          }
          case 'geographicArea': {
            this.sortCriteria+='geographicArea';
            break;
          }
          case 'context': {
            this.sortCriteria+='contextDescription';
            break;
          }
          default: this.sortCriteria='';
        }
      }
    }

    /**
      *  Called when the table is being destroyed. Use this function,
      * to clean up any open connections or free any held resources
      * that were set up during connect.
      */
    disconnect(): void {}

    /**
      * Refresh the data in our datasource
      * @return {Observable<UserRoleContext[]>} return an observable on data
      */
    private loadData(): Observable<UserRoleContext[]> {
      return this.operatorService.searchUserRoleContexts(
          this.userFilter?.login ?? '',
          this.roleFilter?.name ?? '',
          this.geographicAreaFilter?.id,
          this.contextFilter?.name ?? '',
          this.paginator?.pageIndex,
          this.paginator?.pageSize,
          this.sortCriteria,
      ).pipe(map(
          (response)=>{
            this.data= (response.results ?? []);
            this.totalItems = (response.totalItems ?? 0);

            return this.data;
          },
      ));
    }

    /**
     * Function called to ask a refresh of data
     */
    public refreshData() : void {
      this.paginator?.firstPage();
      this.actualize.next();
    }

    /**
     * Function used to remove an UserRoleContextItem
     * @param {UserRoleContext} target The UserRoleContextItem to remove
     * @param {boolean} force Force the remove if target has tasks
     * @return {Observable<null>}
     */
    public deleteUserRoleContext(target: UserRoleContext, force: boolean) :
    Observable<null> {
      return this.operatorService.deleteUserRoleContext(target.id ?? -1, force);
    }

    /**
     * Function to create an operator
     * @param {string} user User login
     * @param {string} role Role name
     * @param {number} geographicArea Geographic Area id
     * @param {string} context Context name
     */
    createUserRoleContext(user: string, role: string, geographicArea: number,
        context: string) : void {
      this.operatorService.createUser(user, role, geographicArea, context)
          .subscribe(
              ()=>{
                this.toaster.sendSuccessMessage('userRoleContext.add.success');
                this.refreshData();
              },
              (response)=>{
                this.toaster.sendErrorMessage('userRoleContext.add.error',
                    response.error.code);
              },
          );
    }

    /**
     * @param {User} userFilter
     */
    updateUserFilter(userFilter: User) : void {
      this.userFilter = userFilter;
      this.refreshData();
    }

    /**
     * @param {Role} roleFilter
     */
    updateRoleFilter(roleFilter: Role) : void {
      this.roleFilter = roleFilter;
      this.refreshData();
    }

    /**
     * @param {GeographicArea} geographicFilter
     */
    updateGeographicFilter(geographicFilter: GeographicArea) {
      this.geographicAreaFilter = geographicFilter;
      this.refreshData();
    }

    /**
     * @param {ContextDescritpion} contextFilter
     */
    updateContextDescriptionFilter(contextFilter: ContextDescription) {
      this.contextFilter = contextFilter;
      this.refreshData();
    }

    /**
     * Reset all filters
     */
    resetFilters() : void {
      this.userFilter = undefined;
      this.roleFilter = undefined;
      this.geographicAreaFilter = undefined;
      this.contextFilter = undefined;
      this.refreshData();
    }
}

