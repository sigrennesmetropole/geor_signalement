/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;

import lombok.Data;

/**
 * Description d'un contexte de création d'un signalement
 * 
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "context_description")
public class ContextDescriptionEntity implements LongId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	/**
	 * Le nom du context c'est à dire le nom de la couche ou le nom du thème en
	 * fonction du {contextType}
	 */
	@Column(length = 100, nullable = false, unique = true)
	private String name;

	/**
	 * L'identifiant de la définition du processus associé
	 */
	@Column(name = "process_definition_key", length = 64)
	private String processDefinitionKey;

	/**
	 * La version de la définition du processus Chaque fois que la version change il
	 * faut mettre à jour cette révision pour pointer sur la nouvelle version
	 */
	@Column(name = "revision")
	private Integer revision;

	/**
	 * Le type de context
	 */
	@Column(name = "context_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private ContextType contextType;

	/**
	 * le type de selection point, ligne, polygon
	 */
	@Column(name = "geographic_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private GeographicType geographicType;
}
