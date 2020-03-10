/**
 * 
 */
package org.georchestra.signalement.service.st.ldap.impl;

import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.st.ldap.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private AuthentificationHelper authentificationHelper;

	@Override
	public User getMe() {
		String username = authentificationHelper.getUsername();
		User user = new User();
		user.setEmail("tom.bambadilum@rennesmetropole.fr");
		user.setFirstName("Tom");
		user.setLastName("Bombadilum");
		user.setOrganization("Service des données");
		user.setLogin(username);
		return user;
	}

}
