/**
 *
 */
package org.georchestra.signalement.service.helper.authentification;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author FNI18300
 *
 */
@Slf4j
@Component(value = "authentificationHelper")
public class AuthentificationHelper {

	@Value("${georchestra.role.administrator}")
	private String georchestraAdministrator;

	/**
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
		return hasRole(georchestraAdministrator);
	}

	/**
	 * @return la liste des rôles
	 */
	public List<String> getRoles() {
		List<String> result = null;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			result = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		}
		return result;
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
			log.info("hasRole {} ? {}", roleName,
					authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
		}
		return result;
	}
}
