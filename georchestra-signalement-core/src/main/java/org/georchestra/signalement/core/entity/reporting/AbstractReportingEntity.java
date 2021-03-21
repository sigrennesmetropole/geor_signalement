/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vividsolutions.jts.geom.Geometry;
import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.StatusFonctionnel;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "abstract_reporting")
public abstract class AbstractReportingEntity implements LongId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "uuid", nullable = false)
	private UUID uuid;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Column(name = "status_fonctionnel")
	@Enumerated(EnumType.STRING)
	private StatusFonctionnel statusFonctionnel;

	@Column(name = "geographic_type", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private GeographicType geographicType;

	@Column(name = "initiator", nullable = false, length = 100)
	private String initiator;

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "assignee", length = 100)
	private String assignee;

	@Column(name = "updated_date", nullable = false)
	private Date updatedDate;

	@Column(name = "description", nullable = false, length = 1024)
	private String description;

	/**
	 * C'est un flux json qui contient les données saisies lors des différentes
	 * étapes
	 */
	@Lob
	@Column(name = "datas")
	private String datas;

	@ManyToOne
	@JoinColumn(name = "context_description_id")
	private ContextDescriptionEntity contextDescription;

	public AbstractReportingEntity(GeographicType geographicType) {
		this.geographicType = geographicType;
	}

	public abstract void setGeometry(Geometry geometry);

	public abstract Geometry getGeometry();

}
