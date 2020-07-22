/**
 * 
 */
package org.georchestra.signalement.service.st.ldap;

import java.util.List;

import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.User;

/**
 * @author FNI18300
 *
 */
public interface UserService {

	/**
	 * Retourne l'utilisateur courant stocké dans le security context
	 * @return l'utilisateur courant
	 */
	User getMe();

	/**
	 * Retoure un utilisateur par son login
	 * @param login
	 * @return
	 */
	User getUserByLogin(String login);

	/**
	 * Retourne les contexts visibles par l'utilisateur courant
	 * @return la liste des contextes
	 */
	List<ContextDescription> getVisibleContexts();

	/**
	 * Retourne les contexts visibles par l'utilisateur dont le login est passé en paramètre
	 * @param login
	 * @return la liste des contextes
	 */
	List<ContextDescription> getVisibleContexts(String login);
}
