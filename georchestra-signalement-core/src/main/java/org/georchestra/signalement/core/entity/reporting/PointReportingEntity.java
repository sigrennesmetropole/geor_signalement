/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.geolatte.geom.Geometry;
import org.georchestra.signalement.core.common.LongId;
import org.georchestra.signalement.core.dto.GeographicType;

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
}
