package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.UserRoleContextsApi;
import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.dto.UserRoleContextPageResult;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.sm.UserRoleContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserRoleContext Controller.
 */
@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "userRoleContext")
public class UserRoleContextController implements UserRoleContextsApi {

	@Autowired
	UserRoleContextService userRoleContextService;

	@Autowired
	UtilPageable utilPageable;

	@Override
	public ResponseEntity<UserRoleContextPageResult> searchUserRoleContexts(String userLogin, String roleName,
			Long geographicAreaId, String contextDescriptionName, Integer offset, Integer limit, String sortExpression)
			throws Exception {

		UserRoleContextSearchCriteria searchCriteria = UserRoleContextSearchCriteria.builder().userLogin(userLogin)
				.roleName(roleName).geographicAreaId(geographicAreaId).contextDescriptionName(contextDescriptionName)
				.build();
		Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
		Page<UserRoleContext> pageResult = userRoleContextService.searchUserRoleContexts(searchCriteria, pageable);

		UserRoleContextPageResult resultObject = new UserRoleContextPageResult();
		resultObject.setResults(pageResult.getContent());
		resultObject.setTotalItems(pageResult.getTotalElements());

		return ResponseEntity.ok(resultObject);
	}

	@Override
	public ResponseEntity<UserRoleContext> createUserRoleContext(UserRoleContext userRoleContext)
			throws InvalidDataException {
		return ResponseEntity.ok(userRoleContextService.createUserRoleContext(userRoleContext));
	}

	@Override
	public ResponseEntity<Void> deleteUserRoleContext(Long id, Boolean force) throws Exception {
		userRoleContextService.deleteUserRoleContext(id, force);
		return ResponseEntity.ok().build();
	}

	@Override
	public ResponseEntity<UserRoleContext> getUserRoleContext(Long id) throws Exception {
		return ResponseEntity.ok(userRoleContextService.getUserRoleContext(id));

	}
}
