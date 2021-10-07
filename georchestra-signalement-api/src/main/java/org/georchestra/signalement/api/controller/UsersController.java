package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.UsersApi;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.dto.UserPageResult;
import org.georchestra.signalement.core.dto.UserSearchCriteria;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.mapper.acl.UserMapper;
import org.georchestra.signalement.service.sm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlleur pour les users.
 */
@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "user")
public class UsersController implements UsersApi {
	
	@Autowired
	private UserService userService;

	@Autowired
	private UtilPageable utilPageable;

	@Override
	public ResponseEntity<Void> deleteUser(String login) throws Exception {
		userService.deleteUser(login);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<User> getUser(String login) throws Exception {
		return ResponseEntity.ok(userService.getUserByLogin(login));
	}

	@Override
	public ResponseEntity<UserPageResult> searchUsers(String email, String login, Integer offset, Integer limit,
			String sortExpression) throws Exception {

		Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
		UserSearchCriteria searchCriteria = UserSearchCriteria.builder().email(email).login(login).build();
		Page<User> pageResult = userService.searchUsers(searchCriteria, pageable);

		UserPageResult resultObject = new UserPageResult();
		resultObject.setResults(pageResult.getContent());
		resultObject.setTotalItems(pageResult.getTotalElements());
		return ResponseEntity.ok(resultObject);
	}

	@Override
	public ResponseEntity<User> createUser(User user) throws Exception {
		return ResponseEntity.ok(userService.createUser(user));
	}
}
