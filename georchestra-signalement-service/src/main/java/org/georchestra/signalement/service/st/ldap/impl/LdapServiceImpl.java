/**
 * 
 */
package org.georchestra.signalement.service.st.ldap.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import org.georchestra.signalement.service.st.ldap.LdapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class LdapServiceImpl implements LdapService {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapServiceImpl.class);

	@Value("${ldap.attribute.login}")
	private String loginAttribute;

	@Value("${ldap.attribute.firstName}")
	private String firstNameAttribute;

	@Value("${ldap.attribute.lastName}")
	private String lastNameAttribute;

	@Value("${ldap.attribute.organization}")
	private String organizationAttribute;

	@Value("${ldap.attribute.email}")
	private String emailAttribute;

	@Value("${ldap.objectClass}")
	private String objectClass;

	@Value("${ldap.user.searchBase}")
	private String userSearchBase;

	@Autowired
	private AuthentificationHelper authentificationHelper;

	@Autowired
	private LdapTemplate ldapTemplate;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;
	
	@Autowired
	private ContextDescriptionMapper contextDescriptionMapper;

	private String[] attributes;

	private Map<String, String> attributeMappings;

	@Override
	public User getMe() {
		User result = getUserByLogin(authentificationHelper.getUsername());
		result.setRoles(authentificationHelper.getRoles());
		return result;
	}

	@Override
	public User getUserByLogin(String username) {
		LOGGER.info("Search user by login {}", username);
		User result = null;
		LdapQueryBuilder queryBuilder = LdapQueryBuilder.query().searchScope(SearchScope.SUBTREE).countLimit(5)
				.attributes(attributes);
		if (StringUtils.isNotEmpty(userSearchBase)) {
			queryBuilder.base(userSearchBase);
		}
		if (StringUtils.isNotEmpty(objectClass)) {
			queryBuilder.where("objectclass").is(objectClass).and(loginAttribute).is(username);
		} else {
			queryBuilder.where(loginAttribute).is(username);
		}

		List<User> users = ldapTemplate.search(queryBuilder, new UserAttributeMapper(attributeMappings));
		if (CollectionUtils.isNotEmpty(users)) {
			LOGGER.info("Search user by login {} found {}", username, users);
			result = users.get(0);
		}
		return result;
	}

	@PostConstruct
	public void initialize() {
		attributes = new String[] { loginAttribute, firstNameAttribute, lastNameAttribute, organizationAttribute,
				emailAttribute };
		attributeMappings = new HashMap<>();
		attributeMappings.put(UserAttributeMapper.LOGIN_FIELD, loginAttribute);
		attributeMappings.put(UserAttributeMapper.FIRSTNAME_FIELD, firstNameAttribute);
		attributeMappings.put(UserAttributeMapper.LASTNAME_FIELD, lastNameAttribute);
		attributeMappings.put(UserAttributeMapper.ORGANIZATION_FIELD, organizationAttribute);
		attributeMappings.put(UserAttributeMapper.EMAIL_FIELD, emailAttribute);
	}

	@Override
	public List<ContextDescription> getVisibleContexts() {
		return getVisibleContexts(authentificationHelper.getUsername());
	}

	@Override
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

}
