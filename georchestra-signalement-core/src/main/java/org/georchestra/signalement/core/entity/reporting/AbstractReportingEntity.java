/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.locationtech.jts.geom.Geometry;

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

	@Column(name = "functional_status", length = 100)
	private String functionalStatus;

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

	public AbstractReportingEntity() {

	}

	public abstract void setGeometry(Geometry geometry);

	public abstract Geometry getGeometry();

}
