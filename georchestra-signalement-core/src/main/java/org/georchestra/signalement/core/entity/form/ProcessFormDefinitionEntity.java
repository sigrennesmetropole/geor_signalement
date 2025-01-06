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

import org.georchestra.signalement.core.common.AbstractProcessLinkEntity;

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
@Table(name = "process_form_definition")
public class ProcessFormDefinitionEntity extends AbstractProcessLinkEntity {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "process_definition_id", nullable = false, length = 64)
	private String processDefinitionId;

	@Column(name = "revision")
	private Integer revision;

	@Column(name = "user_task_id", length = 64)
	private String userTaskId;

	@ManyToOne
	@JoinColumn(name = "form_definition_id")
	private FormDefinitionEntity formDefinition;

	
}
