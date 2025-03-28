/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.georchestra.signalement.core.dto.GeographicType;
import org.locationtech.jts.geom.Geometry;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name="point_reporting")
public class PointReportingEntity extends AbstractReportingEntity {

    @Column(name = "geometry",columnDefinition="Geometry")
    private Geometry geometry;

	public PointReportingEntity() {
		super(GeographicType.POINT);
	}

	@Override
	public String toString() {
		return "PointReportingEntity [getId()=" + getId() + ", getUuid()=" + getUuid() + ", getStatus()=" + getStatus()
				+ ", getGeographicType()=" + getGeographicType() + ", getInitiator()=" + getInitiator()
				+ ", getCreationDate()=" + getCreationDate() + ", getUpdatedDate()=" + getUpdatedDate()
				+ ", getDescription()=" + getDescription() + ", getContextDescription()=" + getContextDescription()
				+ ", geometry=" + geometry + "]";
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
