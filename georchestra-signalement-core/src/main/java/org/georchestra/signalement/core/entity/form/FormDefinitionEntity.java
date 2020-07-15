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

import org.georchestra.signalement.core.common.LongId;

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
public class FormDefinitionEntity implements LongId {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof FormDefinitionEntity)) {
			return false;
		}
		FormDefinitionEntity other = (FormDefinitionEntity) obj;
		if (getId() != null && getId().equals(other.getId())) {
			return true;
		}
		if (getName() == null) {
			if (other.getName() != null) {
				return false;
			}
		} else if (!getName().equals(other.getName())) {
			return false;
		}
		return true;
	}
}
