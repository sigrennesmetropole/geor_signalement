/**
 * 
 */
package org.georchestra.signalement.core.entity.styling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
@Table(name = "process_styling")
public class ProcessStylingEntity extends AbstractProcessLinkEntity {

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
	@JoinColumn(name = "styling_id")
	private StylingEntity styling;

}
