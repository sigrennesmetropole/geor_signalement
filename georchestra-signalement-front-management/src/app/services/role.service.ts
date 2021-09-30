import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {RolesService} from '../api/services';
import {StrictHttpResponse} from '../api/strict-http-response';
import {Role, RolePageResult} from '../api/models';


@Injectable({
  providedIn: 'root',
})
/**
* Service to interact with API service to get, list and delete Roles
*/
export class RoleService {
  /**
  * Constructor of the RoleService
  * @param {RolesService} rolesService The API service
  */
  constructor(private rolesService: RolesService) {}

  /**
   * Get the roles
   * @param {number} offset
   * @param {number} limit
   * @param {string} sortCriteria
   * @return {Observable<StrictHttpResponse<Role[]>>}
   */
  getRoles(offset: number, limit: number, sortCriteria: string)
  : Observable<StrictHttpResponse<RolePageResult>> {
    return this.rolesService.searchRolesResponse({
      offset: offset,
      limit: limit,
      sortExpression: sortCriteria});
  }
  /**
      * Function to delete a Role
      * @param {string} name The name of the role to delete
      * @return {Observable<boolean>} An observable about suppression success
      */
  deleteRole(name : string) : Observable<null> {
    return this.rolesService.deleteRole(name);
  }

  /**
   * Function to create a role
   * @param {string} name Role name
   * @param {string} label Role label
   * @return {Observable<UserItem>} an observable about the role created
   */
  createRole(name : string, label : string) : Observable<Role> {
    return this.rolesService.createRole({name: name, label: label});
  }
}
