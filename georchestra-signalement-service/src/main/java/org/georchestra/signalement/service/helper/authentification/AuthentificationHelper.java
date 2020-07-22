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

	public static final String ADMINISTRATOR_ROLE = "ADMINISTRATOR";

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

	/**
	 * 
	 * @return vrai si l'utilisateur connecté est administrateur
	 */
	public boolean isAdmin() {
		return hasRole(ADMINISTRATOR_ROLE);
	}
	
	/**
	 * 
	 * @param roleName
	 * @return
	 */
	public boolean hasRole(String roleName) {
		boolean result = false;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			result = authentication.getAuthorities().stream()
					.filter(authority -> authority.getAuthority().equalsIgnoreCase(roleName)).count() > 0;
		}
		return result;
	}
}
