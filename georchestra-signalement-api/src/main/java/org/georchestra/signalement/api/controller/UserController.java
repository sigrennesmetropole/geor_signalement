/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.util.List;

import org.georchestra.signalement.api.UserApi;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.service.sm.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

/**
 * @author FNI18300
 *
 */
@RestController
@Api(tags = "user")
@RequiredArgsConstructor
public class UserController implements UserApi {

	private final UserService userService;

	@Override
	public ResponseEntity<User> getMe() throws Exception {
		return ResponseEntity.ok(userService.getMe());
	}

	@Override
	public ResponseEntity<List<ContextDescription>> getVisibleContexts() throws Exception {
		return ResponseEntity.ok(userService.getVisibleContexts());
	}

}
