/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import lombok.Data;

import jakarta.persistence.*;

/**
 * Représente l'association d'un utilisateur avec un rôle pour un contexte
 * (couche ou thème) donné dans une zone géographique donnée
 * 
 * Un utilisateur peut donc avoir des rôles différents pour des couches
 * différentes sur des zones différentes.
 * 
 * Il peut avoir le rôle A sur rennes pour la couche rva et le rôle B sur cesson pour la même couche 
 * 
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "user_role_context")
public class UserRoleContextEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleEntity role;

	@ManyToOne
	@JoinColumn(name = "context_description_id")
	private ContextDescriptionEntity contextDescription;

	@ManyToOne
	@JoinColumn(name = "geographic_area_id")
	private GeographicAreaEntity geographicArea;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserEntity user;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((getContextDescription() == null) ? 0 : getContextDescription().hashCode());
		result = prime * result + ((getGeographicArea() == null) ? 0 : getGeographicArea().hashCode());
		result = prime * result + ((getUser() == null) ? 0 : getUser().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof UserRoleContextEntity)) return false;

		UserRoleContextEntity that = (UserRoleContextEntity) o;

		if (!getId().equals(that.getId())) return false;
		if (getRole() != null ? !getRole().equals(that.getRole()) : that.getRole() != null) return false;
		if (getContextDescription() != null ? !getContextDescription().equals(that.getContextDescription()) : that.getContextDescription() != null)
			return false;
		if (getGeographicArea() != null ? !getGeographicArea().equals(that.getGeographicArea()) : that.getGeographicArea() != null)
			return false;
		return getUser() != null ? getUser().equals(that.getUser()) : that.getUser() == null;
	}
}
