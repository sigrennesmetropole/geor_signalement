package org.georchestra.signalement.service.sm.impl;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.georchestra.signalement.core.dao.acl.*;
import org.georchestra.signalement.core.dto.UserRoleContext;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.mapper.acl.UserRoleContextMapper;
import org.georchestra.signalement.service.sm.UserRoleContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleContextServiceImpl implements UserRoleContextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRoleContextServiceImpl.class);

    @Autowired
    UserRoleContextDao userRoleContextDao;
    @Autowired
    UserRoleContextCustomDao userRoleContextCustomDao;
    @Autowired
    UserRoleContextMapper userRoleContextMapper;

    @Autowired
    UserDao userDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    ContextDescriptionDao contextDescriptionDao;

    @Autowired
    GeographicAreaDao geographicAreaDao;

    @Autowired
    ProcessEngine processEngine;

    @Autowired
    UtilPageable utilPageable;

    @Override
    public Page<UserRoleContext> searchUserRoleContexts(Pageable pageable,
                                                        String userLogin, String roleName,
                                                        String contextDescriptionName, Long geographicAreaId) {

        UserRoleContextSearchCriteria searchCriteria = new UserRoleContextSearchCriteria();

        if (userLogin != null && userDao.findByLogin(userLogin) != null) {
            LOGGER.debug("User filter provided : {}", userLogin);
            searchCriteria.setUser(userDao.findByLogin(userLogin));
        }

        if (roleName != null && roleDao.findByName(roleName) != null) {
            LOGGER.debug("Role filter provided : {}", roleName);
            searchCriteria.setRole(roleDao.findByName(roleName));
        }

        if (contextDescriptionName != null && contextDescriptionDao.findByName(contextDescriptionName) != null) {
            LOGGER.debug("Context filter provided : {}", contextDescriptionName);
            searchCriteria.setContextDescription(contextDescriptionDao.findByName(contextDescriptionName));
        }

        if (geographicAreaId != null && geographicAreaDao.findById(geographicAreaId).isPresent()) {
            LOGGER.debug("Geographic Area filter provided : {}", geographicAreaId);
            searchCriteria.setGeographicArea(geographicAreaDao.findById(geographicAreaId).get());
        }

        return userRoleContextCustomDao
                .searchUserRoleContext(searchCriteria, pageable)
                .map(entity -> userRoleContextMapper.entityToDto(entity));
    }

    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, InvalidDataException.class})
    public void deleteUserRoleContext(Long userRoleContextId, Boolean force) throws InvalidDataException {
        if (userRoleContextId == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        UserRoleContextEntity entity = userRoleContextDao.findById(userRoleContextId).orElse(null);
        if (entity == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NOT_AVAILABLE);
        }

        TaskService taskService = processEngine.getTaskService();
        List<Task> assignedTasks = taskService.createTaskQuery().taskAssignee(entity.getUser().getLogin()).list();

        if (!assignedTasks.isEmpty() && force) {
            LOGGER.debug("{} task(s) found being assigned to the operator, they will be unclaimed", assignedTasks.size());
            for (Task task : assignedTasks) {
                taskService.unclaim(task.getId());
            }
        } else if (!assignedTasks.isEmpty()) {
            throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
        }

        userRoleContextDao.delete(entity);

    }

    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, InvalidDataException.class})
    public UserRoleContext createUserRoleContext(UserRoleContext userRoleContext) throws InvalidDataException {

        if (userRoleContext == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }

        if (userRoleContext.getRole().getName() == null
                || userRoleContext.getContextDescription().getName() == null
                || userRoleContext.getGeographicArea().getId() == null
                || userRoleContext.getUser().getLogin() == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_ATTRIBUTE);
        }

        if (!geographicAreaDao.findById(userRoleContext.getGeographicArea().getId()).isPresent()
                || roleDao.findByName(userRoleContext.getRole().getName()) == null
                || userDao.findByLogin(userRoleContext.getUser().getLogin()) == null
                || contextDescriptionDao.findByName(userRoleContext.getContextDescription().getName()) == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.ILLEGAL_ATTRIBUTE);
        }

        UserRoleContextEntity entity = new UserRoleContextEntity();
        UserRoleContextSearchCriteria searchCriteria = new UserRoleContextSearchCriteria();

        entity.setGeographicArea(geographicAreaDao.findById(userRoleContext.getGeographicArea().getId()).get());
        searchCriteria.setGeographicArea(entity.getGeographicArea());

        entity.setRole(roleDao.findByName(userRoleContext.getRole().getName()));
        searchCriteria.setRole(entity.getRole());

        entity.setUser(userDao.findByLogin(userRoleContext.getUser().getLogin()));
        searchCriteria.setUser(entity.getUser());

        entity.setContextDescription(contextDescriptionDao
                .findByName(userRoleContext.getContextDescription().getName()));
        searchCriteria.setContextDescription(entity.getContextDescription());
        if (userRoleContextCustomDao.searchUserRoleContext(searchCriteria,
                utilPageable.getPageable(0, 1, "")).getTotalElements() != 0) {
            throw new InvalidDataException(ErrorMessageConstants.ALREADY_EXISTS);
        }

        userRoleContextDao.save(entity);


        return userRoleContextMapper.entityToDto(entity);
    }

    @Override
    public UserRoleContext getUserRoleContext(Long id) {
        if (id == null) {
            return null;
        }

        return userRoleContextMapper.entityToDto(userRoleContextDao.findById(id).orElse(null));

    }
}
