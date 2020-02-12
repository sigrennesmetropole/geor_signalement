/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "form_definition")
public class FormDefinitionEntity implements LongId {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	/**
	 * Contient un flux json constitué par une liste de FieldDefinition
	 */
	@Lob
	@Column(name = "definition", nullable = false)
	private String definition;

}
