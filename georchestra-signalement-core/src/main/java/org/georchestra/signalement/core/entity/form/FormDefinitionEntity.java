/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.AbstractNamedLongId;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Décrit un formulaire composé de sections
 * 
 * @author FNI18300
 *
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "form_definition")
public class FormDefinitionEntity extends AbstractNamedLongId  {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@OneToMany
	@JoinColumn(name = "form_definition_id")
	@OrderBy("order_ ASC")
	private Set<FormSectionDefinitionEntity> formSectionDefinitions;

}
