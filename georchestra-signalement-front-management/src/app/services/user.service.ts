import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {UserService} from '../api/services';
import {ToasterUtil} from '../utils/toaster.util';
import {UserItem} from '../user/user.datasource';
import {UserItemMapper} from '../mappers/user-item.mapper';
import {UserPageResult} from '../api/models';


@Injectable({
  providedIn: 'root',
})
/**
* Service to interact with API service to get, list and delete Workflows
*/
export class UserItemService {
  /**
  * Constructor of the WorkflowService
  * @param {UserService} userService The API service
  * @param {ToasterUtil} toasterService The ToasterUtil
  */
  constructor(private userService: UserService,
    private toasterService: ToasterUtil,
    private userItemMapper: UserItemMapper) {}

  /**
   * searchUser by calling the API
   * @param {string} mailFilter Filter on e-mail address
   * @param {string} loginFilter Filter on login
   * @param {number} offset Page to display
   * @param {number} limit Number of elements to display
   * @param {string} sortExpression Sort expression
   * @return {Observable<UserPageResult[]>}
   */
  searchUsers(mailFilter ?: string,
      loginFilter ?: string,
      offset ?: number,
      limit ?: number,
      sortExpression ?: string):
      Observable<UserPageResult> {
    const searchParameters = {
      loginUser: loginFilter,
      email: mailFilter,
      offset: offset,
      limit: limit,
      sortExpression: sortExpression,
    };
    return this.userService.searchUsers(searchParameters);
  }
  /**
      * Function to delete a UserItem
      * @param {UserItem} target The UserItem to delete
      * @return {Observable<boolean>} An observable about suppression success
      */
  deleteUser(target : UserItem) : Observable<null> {
    return this.userService.deleteUser(target.login);
  }

  /**
   * Function to create an user
   * @param {string} login User login
   * @param {string} email User e-mail
   * @return {Observable<UserItem>} an observable about the user created
   */
  createUser(login : string, email : string) : Observable<UserItem> {
    return this.userService.createUser({login: login, email: email})
        .pipe(map(this.userItemMapper.usertoUserItem));
  }
}
