/**
 * 
 */
package org.georchestra.signalement.core.entity.styling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.AbstractNamedLongId;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "styling")
public class StylingEntity extends AbstractNamedLongId {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;
	
	/**
	 * Contient un flux json constitué par une liste de FieldDefinition
	 */
	@Column(name = "definition", nullable = false, columnDefinition = "text", length = 4086)
	private String definition;

}
