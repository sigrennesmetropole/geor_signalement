/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.AbstractNamedLongId;
import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.entity.ListToStringConverter;

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

	@Column(name = "login", nullable = false, length = 100)
	private String login;

	/**
	 * Addresse email permettant de faire le lien avec la personne authentifiée
	 */
	@Column(nullable = false, length = 150)
	private String email;

	@Column(name = "first_name", length = 150)
	private String firstName;

	@Column(name = "last_name", length = 150)
	private String lastName;

	@Column(name = "organization", length = 150)
	private String organization;

	@Column(name = "roles", length = 1024)
	@Convert(converter = ListToStringConverter.class)
	private List<String> roles;

	/**
	 * Définition du tryptique utilisateur/role/context de couche
	 */
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Set<UserRoleContextEntity> userRoles;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((getLogin() == null) ? 0 : getLogin().hashCode());
		result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
		result = prime * result + ((getOrganization() == null) ? 0 : getOrganization().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserEntity)) return false;

		UserEntity that = (UserEntity) o;

		if (!getId().equals(that.getId())) return false;
		if (getLogin() != null ? !getLogin().equals(that.getLogin()) : that.getLogin() != null) return false;
		if (getEmail() != null ? !getEmail().equals(that.getEmail()) : that.getEmail() != null) return false;
		if (getFirstName() != null ? !getFirstName().equals(that.getFirstName()) : that.getFirstName() != null)
			return false;
		if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null)
			return false;
		return getOrganization() != null ? getOrganization().equals(that.getOrganization()) : that.getOrganization() == null;
	}
}
