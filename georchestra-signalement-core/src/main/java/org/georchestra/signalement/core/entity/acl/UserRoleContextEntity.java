/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name="user_role_context")
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

}
