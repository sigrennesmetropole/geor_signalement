/**
 * 
 */
package org.georchestra.signalement.service.sm.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.mapper.acl.UserMapper;
import org.georchestra.signalement.service.sm.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Service
public class UserServiceImpl implements UserService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private AuthentificationHelper authentificationHelper;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private ContextDescriptionMapper contextDescriptionMapper;

	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional(readOnly = true)
	public User getMe() {
		return getUserByLogin(authentificationHelper.getUsername());
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByLogin(String username) {
		LOGGER.info("Search user by login {}", username);
		UserEntity userEntity = userDao.findByLogin(username);
		return userMapper.entityToDto(userEntity);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ContextDescription> getVisibleContexts() {
		return getVisibleContexts(authentificationHelper.getUsername());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ContextDescription> getVisibleContexts(String login) {
		Set<ContextDescriptionEntity> contexts = null;
		User user = getUserByLogin(login);
		UserEntity userEntity = userDao.findByLogin(login);
		if (user != null && userEntity != null) {
			contexts = new HashSet<>();
			if (CollectionUtils.isNotEmpty(userEntity.getUserRoles())) {
				for (UserRoleContextEntity userRoleContextEntity : userEntity.getUserRoles()) {
					if (userRoleContextEntity.getContextDescription() != null) {
						// le contexte n'est pas encore dans la liste on l'ajoute
						contexts.add(userRoleContextEntity.getContextDescription());
					} else if (userRoleContextEntity.getContextDescription() == null) {
						// si le contexte est vide, on considère que l'on peut avoir un rôle sur tous
						// les contextes
						contexts.addAll(contextDescriptionDao.findAll());
					}
				}
			}
		}

		return contextDescriptionMapper.entitiesToDtos(contexts);
	}

	@Override
	@Transactional(readOnly = false)
	public User createUser(User user) {
		if (user == null || StringUtils.isEmpty(user.getLogin())) {
			throw new IllegalArgumentException("Invaluder user : " + user);
		}
		UserEntity userEntity = userMapper.dtoToEntity(user);
		userDao.save(userEntity);
		return userMapper.entityToDto(userEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public User updateUser(User user) {
		if (user == null || StringUtils.isEmpty(user.getLogin())) {
			throw new IllegalArgumentException("Invaluder user : " + user);
		}
		UserEntity userEntity = userDao.findByLogin(user.getLogin());
		userMapper.dtoToEntity(user, userEntity);
		userDao.save(userEntity);
		return userMapper.entityToDto(userEntity);
	}

}
