import {Injectable} from '@angular/core';
import {User} from '../api/models';
import {UserItem} from '../user/user.datasource';

@Injectable()
/**
 * Util to convert between User and UserItem
 */
export class UserItemMapper {
  /**
     * Constructor
     */
  constructor() {}

  /**
     * Mapper for User to UserItem
     * @param {User} user User to convert
     * @return {UserItem} UserItem converted from User
     */
  usertoUserItem(user : User)
    : UserItem {
    return {
      login: user.login ?? '',
      email: user.email ?? '',
      firstname: user.firstName ?? '',
      lastname: user.lastName ?? '',
      organization: user.organization ?? '',
    };
  }
}
