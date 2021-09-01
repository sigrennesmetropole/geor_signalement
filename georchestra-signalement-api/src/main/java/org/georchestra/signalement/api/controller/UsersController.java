package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.UsersApi;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.dto.UserPageResult;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.mapper.acl.UserMapper;
import org.georchestra.signalement.service.sm.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlleur pour les users.
 */
@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "user")
public class UsersController implements UsersApi {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<Void> deleteUser(String login) throws Exception {
        userService.deleteUser(login);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<User> getUser(String login) throws Exception {
        User result = userService.getUserByLogin(login);
        if (result != null) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<UserPageResult> searchUsers(String email, String login, Integer offset, Integer limit, String sortExpression) throws Exception {
        UtilPageable utilPageable = new UtilPageable(limit);
        Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
        Page<User> pageResult = userService.searchUsers(email, login, pageable);
        long totalItems = pageResult.getTotalElements();
        List<User> results = pageResult.getContent();

        UserPageResult resultObject = new UserPageResult();
        resultObject.setResults(results);
        resultObject.setTotalItems(new BigDecimal(totalItems));
        return ResponseEntity.ok(resultObject);
    }

    @Override
    public ResponseEntity<User> createUser(User user) throws Exception {
        User userCreated = userService.createUser(user);
        return ResponseEntity.ok(userCreated);
    }
}
