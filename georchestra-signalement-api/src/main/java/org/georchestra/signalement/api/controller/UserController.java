/**
 * 
 */
package org.georchestra.signalement.api.controller;

import org.georchestra.signalement.api.UserApi;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.service.st.ldap.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

/**
 * @author FNI18300
 *
 */
public class UserController implements UserApi {

	@Autowired
	private UserService userService;

	@Override
	public ResponseEntity<User> getMe() throws Exception {
		return ResponseEntity.ok(userService.getMe());
	}

}
