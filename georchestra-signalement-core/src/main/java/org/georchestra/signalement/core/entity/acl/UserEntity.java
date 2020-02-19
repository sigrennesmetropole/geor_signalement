/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * Représente un user pour le workflow
 * 
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "user_")
public class UserEntity implements LongId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * Addresse email permettant de faire le lien avec la personne authentifiée
	 */
	@Column(nullable = false, length = 150)
	private String email;

	@Column(name = "first_name", length = 150)
	private String firstName;

	@Column(name = "last_name", length = 150)
	private String lastName;

	/**
	 * Définition du tryptique utilisateur/role/context de couche
	 */
	@OneToMany
	@JoinColumn(name = "user_id")
	private Set<UserRoleContextEntity> userRoles;

}
