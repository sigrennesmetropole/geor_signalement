/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "process_form_definition")
public class ProcessFormDefinitionEntity implements LongId {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "process_definition_id", nullable = false, length = 64)
	private String processDefinitionId;

	@Column(name = "revision")
	private Integer revision;

	@Column(name = "service_task_id", length = 64)
	private String serviceTaskId;

	@ManyToOne
	@JoinColumn(name = "form_definition_id")
	private FormDefinitionEntity formDefinition;

}
