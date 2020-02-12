/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "context_description")
public class ContextDescriptionEntity implements LongId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(length = 100, nullable = false, unique = true)
	private String name;

	@Column(name = "context_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private ContextType contextType;

	@Column(name = "geographic_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private GeographicType geographicType;
}
