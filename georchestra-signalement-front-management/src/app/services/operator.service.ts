import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {UserRoleContextService} from '../api/services';
import {ToasterUtil} from '../utils/toaster.util';
import {UserRoleContext, UserRoleContextPageResult} from '../api/models';


@Injectable({
  providedIn: 'root',
})
/**
* Service to interact with API service to get, list and delete userRoleContexts
*/
export class OperatorService {
  /**
  * Constructor of the UserRoleContextItemService
  * @param {UserRoleContextService} userRoleContextService The API service
  * @param {ToasterUtil} toasterService The ToasterUtil
  */
  constructor(private userRoleContextService: UserRoleContextService,
    private toasterService: ToasterUtil) {}

  /**
   * searchUserRoleContexts by calling the API
   * @param {string} user User login
   * @param {string} role Role name
   * @param {number} geographicArea GeographicArea id
   * @param {string} contextDescription Context Description name
   * @param {number} offset Page to display
   * @param {number} limit Number of elements to display
   * @param {string} sortExpression Sort expression
   * @return {Observable<StrictHttpResponse<User[]>>}
   */
  searchUserRoleContexts(user ?: string,
      role ?: string,
      geographicArea ?: number,
      contextDescription ?: string,
      offset ?: number,
      limit ?: number,
      sortExpression ?: string):
      Observable<UserRoleContextPageResult> {
    const searchParameters = {
      user: user,
      role: role,
      geographicArea: geographicArea,
      contextDescription: contextDescription,
      offset: offset,
      limit: limit,
      sortExpression: sortExpression,
    };
    return this.userRoleContextService.searchUserRoleContexts(searchParameters);
  }
  /**
      * Function to delete a UserRoleContext
      * @param {number} targetId The id of the UserRoleContext to delete
      * @param {boolean} force Ignore tasks of the UserRoleContext
      * @return {Observable<null>}
      */
  deleteUserRoleContext(targetId : number, force : boolean) : Observable<null> {
    const params = {
      id: targetId,
      force: force,
    };
    return this.userRoleContextService.deleteUserRoleContext(params);
  }

  /**
   * Function to create an UserRoleContext
   * @param {string} user User login
   * @param {string} role Role name
   * @param {number} geographicArea GeographicArea id
   * @param {string} context ContextDescription name
   * @return {Observable<UserRoleContext>}
   */
  createUser(user : string, role : string,
      geographicArea: number, context : string) : Observable<UserRoleContext> {
    const data : UserRoleContext = {
      user: {
        login: user,
      },
      role: {
        name: role,
      },
      geographicArea: {
        id: geographicArea,
      },
      contextDescription: {
        name: context,
      },
    };
    return this.userRoleContextService.createUserRoleContext(data);
  }
}
