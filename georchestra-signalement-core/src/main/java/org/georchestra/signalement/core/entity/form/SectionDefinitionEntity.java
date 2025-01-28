/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "section_definition")
public class SectionDefinitionEntity extends AbstractNamedLongId {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "label", nullable = false, length = 150)
	private String label;

	/**
	 * Contient un flux json constitué par une liste de FieldDefinition
	 */
	@Column(name = "definition", nullable = false, columnDefinition = "text", length = 4086)
	private String definition;

}
