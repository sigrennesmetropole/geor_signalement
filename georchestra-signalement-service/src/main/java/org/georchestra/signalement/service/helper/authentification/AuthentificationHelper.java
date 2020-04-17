/**
 * 
 */
package org.georchestra.signalement.service.helper.authentification;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class AuthentificationHelper {

	/**
	 * 
	 * @return l'username de la personne authentifiée
	 */
	public String getUsername() {
		String username = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() != null) {
			username = authentication.getPrincipal().toString();
		}
		return username;
	}
}
