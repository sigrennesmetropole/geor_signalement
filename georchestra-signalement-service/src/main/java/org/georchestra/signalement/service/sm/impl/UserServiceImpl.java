/**
 *
 */
package org.georchestra.signalement.service.sm.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.mapper.acl.UserMapper;
import org.georchestra.signalement.service.sm.UserService;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author FNI18300
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

	@Autowired
	private UtilPageable utilPageable;

	@Autowired
	private UserRoleContextCustomDao userRoleContextCustomDao;

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
					} else {
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
	@Transactional(readOnly = false, rollbackFor = InvalidDataException.class)
	public User createUser(User user) throws InvalidDataException {
		if (user == null ||
				StringUtils.isEmpty(user.getLogin()) ||
				StringUtils.isEmpty(user.getEmail())) {
			throw new InvalidDataException("Invalid user : " + user);
		}
		EmailValidator emailValidator = new EmailValidator();
		if (!emailValidator.isValid(user.getEmail(), null)) {
			throw new InvalidDataException("Not a valid e-mail address");
		}
		UserEntity userEntity = userMapper.dtoToEntity(user);
		try {
			userDao.save(userEntity);
		} catch (DataIntegrityViolationException exception) {
			throw new InvalidDataException("Login is already used");
		}

		return userMapper.entityToDto(userEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public User updateUser(User user) {
		if (user == null || StringUtils.isEmpty(user.getLogin())) {
			throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
		}
		UserEntity userEntity = userDao.findByLogin(user.getLogin());
		userMapper.dtoToEntity(user, userEntity);
		userDao.save(userEntity);
		return userMapper.entityToDto(userEntity);
	}

	@Override
	public Page<User> searchUsers(String email, String login, Pageable pageable) {
		LOGGER.info("Recherche des utilisateurs avec e-mail : {} et login : {}", email, login);
		User user = new User();
		user.setLogin(login);
		user.setEmail(email);
		UserEntity userEntity = userMapper.dtoToEntity(user);

		ExampleMatcher matcher = ExampleMatcher.matching()
				.withIgnoreCase(true)
				.withIgnoreNullValues()
				.withMatcher("login", ExampleMatcher.GenericPropertyMatchers.contains())
				.withMatcher("email", ExampleMatcher.GenericPropertyMatchers.contains());

		Example<UserEntity> example = Example.of(userEntity, matcher);
		return userDao.findAll(example, pageable).map(userMapper::entityToDto);

	}

	@Override
	public void deleteUser(String login) throws InvalidDataException {
		UserEntity userEntity = userDao.findByLogin(login);
		if (userEntity == null) {
			throw new InvalidDataException(ErrorMessageConstants.NULL_OBJECT);
		}

		Pageable pageable = utilPageable.getPageable(0, 1, null);
		UserRoleContextSearchCriteria searchCriteria = new UserRoleContextSearchCriteria();
		searchCriteria.setUser(userEntity);
		if (userRoleContextCustomDao.searchUserRoleContext(searchCriteria, pageable).getTotalElements() != 0) {
			throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
		}

		userDao.delete(userEntity);

	}

}
