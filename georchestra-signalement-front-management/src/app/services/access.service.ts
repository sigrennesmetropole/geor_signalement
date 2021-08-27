import {Injectable} from '@angular/core';
import {forkJoin, Observable, of, Subject} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {ConfigurationData, User} from '../api/models';
import {AdministrationService, UserService} from '../api/services';

@Injectable({
  providedIn: 'root',
})
/**
* Service used to check access and to get the connected User
*/
export class AccessService {
    user: User|undefined;
    roleAdmin: string|undefined;
    userObservable: Subject<User>;
    allowedObservable: Subject<boolean>;
    /**
    * Constructor of the AccessService
    * @param {UserService} userService The API service UserService
    */
    constructor(private userService:UserService,
      private adminService:AdministrationService) {
      this.userObservable = new Subject();
      this.allowedObservable = new Subject();
    }

    /**
    * Check if the user is allowed to access the admin interface
    * @return {Observable<boolean>} User is allowed to acces the admin interface
    */
    isAllowed(): Observable<boolean> {
      return forkJoin([this.getUser(), this.getAdminRole()]).pipe(
          map((value)=>{
            return value[0].roles != undefined &&
          value[0].roles.indexOf(value[1]) != -1;
          }),
      );
    }

    /**
     * Get the user
     * @return {Observable<User>}
     */
    getUser(): Observable<User> {
      if (this.user != undefined) {
        return of(this.user);
      } else {
        return this.userService
            .getMe().pipe(tap((x : User)=>this.user = x));
      }
    }

    /**
     * Get the admin role
     * @return {Observable<String>}
     */
    getAdminRole(): Observable<string> {
      if (this.roleAdmin != undefined) {
        return of(this.roleAdmin);
      } else {
        return this.adminService.getConfiguration()
            .pipe(
                tap((config : ConfigurationData)=>{
                  this.roleAdmin= config.roleAdministrator;
                }),
                map((config : ConfigurationData)=>{
                  if (config.roleAdministrator) {
                    return config.roleAdministrator;
                  } else {
                    return '';
                  }
                }),
            );
      }
    }
}
