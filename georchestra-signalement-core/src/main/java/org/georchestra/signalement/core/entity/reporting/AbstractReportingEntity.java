/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.geolatte.geom.Geometry;
import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.GeographicType;
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
	@Column(name = "id")
	private Long id;

	@Column(name = "uuid", nullable = false)
	private UUID uuid;

	@Column(name = "geographic_type", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private GeographicType geographicType;

	@Column(name = "initiator", nullable = false, length = 100)
	private String initiator;

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "updated_date", nullable = false)
	private Date updatedDate;

	@Lob
	@Column(name = "datas")
	private String datas;

	@ManyToOne
	@JoinColumn(name = "context_description_id")
	private ContextDescriptionEntity contextDescription;

	@OneToMany
	@JoinColumn(name = "abstract_reporting_id")
	private Set<ReportingComment> comments;

	@OneToMany
	@JoinColumn(name = "abstract_reporting_id")
	private Set<AttachmentEntity> attachments;

	public AbstractReportingEntity(GeographicType geographicType) {
		this.geographicType = geographicType;
	}

	public abstract void setGeometry(@SuppressWarnings("rawtypes") Geometry geometry);

	@SuppressWarnings("rawtypes")
	public abstract Geometry getGeometry();

}
