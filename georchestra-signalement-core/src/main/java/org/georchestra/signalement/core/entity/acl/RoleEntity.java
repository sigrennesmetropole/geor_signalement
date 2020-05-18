/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.Labelized;
import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * Réprésente un rôle pour un workflow
 * 
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "role")
public class RoleEntity implements LongId, Labelized {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(length = 50, nullable = false, unique = true)
	private String name;

	@Column(length = 255, nullable = false)
	private String label;

}
