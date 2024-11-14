/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.common.Ordered;

import lombok.Data;

/**
 * Défini l'association entre un formulaire et une section avec l'ordre
 * d'affichage et si la section est en lecture seule ou non
 * 
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "form_section_definition")
public class FormSectionDefinitionEntity implements LongId, Ordered {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "read_only", nullable = false)
	private boolean readOnly;

	@Column(name = "order_", nullable = false)
	private int order;

	@ManyToOne
	@JoinColumn(name = "section_definition_id")
	private SectionDefinitionEntity sectionDefinition;

}
