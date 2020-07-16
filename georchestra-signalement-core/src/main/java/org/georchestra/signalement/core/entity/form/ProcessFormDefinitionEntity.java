/**
 * 
 */
package org.georchestra.signalement.core.entity.form;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

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
public class ProcessFormDefinitionEntity implements LongId {

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

	@Override
	@SuppressWarnings({"common-java:DuplicatedBlocks"})
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((getProcessDefinitionId() == null) ? 0 : getProcessDefinitionId().hashCode());
		result = prime * result + ((getRevision() == null) ? 0 : getRevision().hashCode());
		result = prime * result + ((getUserTaskId() == null) ? 0 : getUserTaskId().hashCode());
		return result;
	}

	@Override
	@SuppressWarnings({"squid:S3776","common-java:DuplicatedBlocks"})
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ProcessFormDefinitionEntity)) {
			return false;
		}
		ProcessFormDefinitionEntity other = (ProcessFormDefinitionEntity) obj;
		if (getId() != null && getId().equals(other.getId())) {
			return true;
		}
		if (getProcessDefinitionId() == null) {
			if (other.getProcessDefinitionId() != null) {
				return false;
			}
		} else if (!getProcessDefinitionId().equals(other.getProcessDefinitionId())) {
			return false;
		}
		if (getRevision() == null) {
			if (other.getRevision() != null) {
				return false;
			}
		} else if (!getRevision().equals(other.getRevision())) {
			return false;
		}
		if (getUserTaskId() == null) {
			if (other.getUserTaskId() != null) {
				return false;
			}
		} else if (!getUserTaskId().equals(other.getUserTaskId())) {
			return false;
		}
		return true;
	}

}
