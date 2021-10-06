import {DataSource} from '@angular/cdk/collections';
import {MatPaginator} from '@angular/material/paginator';
import {MatSort} from '@angular/material/sort';
import {delayWhen, map, tap} from 'rxjs/operators';
import {Observable,
  merge, Subject, of} from 'rxjs';
import {Injectable} from '@angular/core';
import {ToasterUtil} from '../utils/toaster.util';
import {TranslateService} from '@ngx-translate/core';
import {UserItemService} from '../services/user.service';
import {UserItemMapper} from '../mappers/user-item.mapper';


export interface UserItem {
    login:string,
    email:string,
    lastname:string,
    firstname:string,
    organization: string,
  }

export interface UserFilter {
    mailFilter: string,
    loginFilter: string
  }

  @Injectable()
  /**
  * Data source for the User view. This class should
  * encapsulate all logic for fetching and manipulating the displayed data
  * (including sorting, pagination, and filtering).
  */
export class UserDataSource extends DataSource<UserItem> {
    paginator: MatPaginator | undefined;
    sort: MatSort | undefined;
    data: UserItem[]=[];
    sortCriteria:string = '';
    totalItems:number=0;
    loginFilter:string='';
    emailFilter:string='';

    actualize: Subject<void> = new Subject<void>();

    /**
    * DataSource constructor
    * @param {UserItemService} userService to interract with users
    * @param {ToasterUtil} toasterService to display messages
    * @param {TranslateService} translateService to translate
    */
    constructor(private userService: UserItemService,
      private toasterService:ToasterUtil,
      public translateService: TranslateService,
      private userItemMapper: UserItemMapper) {
      super();

      this.loadData().pipe(tap(()=>{
        this.actualize.next();
      }));
    }

    /**
      * Connect this data source to the table. The table will only update when
      * the returned stream emits new items.
      * @return {Observable<UserItem[]>} A stream of the items to be rendered.
      */
    connect(): Observable<UserItem[]> {
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
          case 'login': {
            this.sortCriteria+='login';
            break;
          }
          case 'email': {
            this.sortCriteria+='email';
            break;
          }
          case 'firstname': {
            this.sortCriteria+='firstName';
            break;
          }
          case 'lastname': {
            this.sortCriteria+='lastName';
            break;
          }
          case 'organization': {
            this.sortCriteria+='organization';
            break;
          }
          default: this.sortCriteria='';
        }
      }
    }

    /**
      * Refresh the data in our datasource
      * @return {Observable<UserItem[]>} return an observable on data
      */
    private loadData(): Observable<UserItem[]> {
      return this.userService
          .searchUsers(this.emailFilter,
              this.loginFilter,
              this.paginator?.pageIndex,
              this.paginator?.pageSize,
              this.sortCriteria)
          .pipe(map(
              (response)=>{
                this.data = (response.results ?? [])
                    .map(this.userItemMapper.usertoUserItem);

                this.totalItems = (response.totalItems ?? 0);

                return this.data;
              }),
          );
    }

    /**
     * Function called to ask a refresh of data
     */
    public refreshData() : void {
      this.paginator?.firstPage();
      this.actualize.next();
    }

    /**
     * Function used to remove an UserItem
     * @param {UserItem} target The UserItem to remove
     */
    public deleteUser(target : UserItem) : void {
      this.userService.deleteUser(target).subscribe(
          ()=>{
            this.toasterService.sendSuccessMessage('user.delete.success');
          },
          (response)=>{
            this.toasterService.sendErrorMessage('user.delete.error',
                response.error.code);
          },
          ()=>{
            this.refreshData();
          },
      );
    }

    /**
     * Function called to create an user
     * @param {string} login The login for the user
     * @param {string} email The e-mail for the user
     */
    createUser(login: string, email: string) : void {
      this.userService.createUser(login, email).subscribe(
          (result)=>{
            if (result) {
              this.toasterService.sendSuccessMessage('user.add.success');
            } else {
              this.toasterService.sendErrorMessage('user.add.error');
            }
          },
          (response)=>{
            this.toasterService.sendErrorMessage('user.add.error',
                response.error.code);
          },
          ()=>{
            this.refreshData();
          },
      );
    }

    /**
     * Set the login filter and actualize data
     * @param {string} loginFilter
     * @param {string} emailFilter
     */
    updateFilters(loginFilter: string, emailFilter:string) : void {
      this.loginFilter = loginFilter;
      this.emailFilter = emailFilter;
      this.paginator?.firstPage();
      this.refreshData();
    }
}

