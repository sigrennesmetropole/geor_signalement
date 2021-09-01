/**
 *
 */
package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author FNI18300
 *
 */
public interface UserService {

	/**
	 * Retourne l'utilisateur courant stocké dans le security context
	 *
	 * @return l'utilisateur courant avec ses rôles
	 * @see UserService.getUserByLogin
	 */
	User getMe();

	/**
	 * Retoure un utilisateur par son login.
	 *
	 * @param login
	 * @return
	 */
	User getUserByLogin(String login);

	/**
	 * Retourne les contexts visibles par l'utilisateur courant
	 *
	 * @return la liste des contextes
	 */
	List<ContextDescription> getVisibleContexts();

	/**
	 * Retourne les contexts visibles par l'utilisateur dont le login est passé en
	 * paramètre
	 *
	 * @param login
	 * @return la liste des contextes
	 */
	List<ContextDescription> getVisibleContexts(String login);

	/**
	 * Créé un utilisateur
	 *
	 * @param user
	 * @return l'utilisateur créé
	 */
	User createUser(User user) throws InvalidDataException;

	/**
	 * Update un utilisateur
	 *
	 * @param user
	 * @return l'utilisateur mis à jour
	 */
	User updateUser(User user);

	/**
	 * Retourne la liste des utilisateurs avec un filtre possile sur le login et le mail
	 *
	 * @param mail     Filtre sur le mail
	 * @param login    Filtre sur le login
	 * @param pageable
	 * @return la liste des User respectant les filtres
	 */
	Page<User> searchUsers(String mail, String login, Pageable pageable);

	void deleteUser(String login) throws InvalidDataException;
}
