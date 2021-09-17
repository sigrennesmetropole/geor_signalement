package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.RolesApi;
import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.core.dto.RolePageResult;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.sm.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

/**
 * Roles Controller.
 */
@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "roles")
public class RolesController implements RolesApi {

    @Autowired
    RoleService roleService;

    @Autowired
    UtilPageable utilPageable;

    @Override
    public ResponseEntity<Void> deleteRole(String name) throws Exception {
        roleService.deleteRole(name);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Role> getRole(String name) throws Exception {
        return ResponseEntity.ok(roleService.getRole(name));
    }

    @Override
    public ResponseEntity<RolePageResult> getRoles(Integer offset, Integer limit, String sortExpression) throws Exception {
        Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
        Page<Role> pageResult = roleService.getPageRole(pageable);
        RolePageResult resultObject = new RolePageResult();
        resultObject.setResults(pageResult.getContent());
        resultObject.setTotalItems(pageResult.getTotalElements());
        return ResponseEntity.ok(resultObject);
    }

    @Override
    public ResponseEntity<Role> createRole(Role role) throws Exception {
        return ResponseEntity.ok(roleService.createRole(role));
    }
}
