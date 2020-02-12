/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.geolatte.geom.Geometry;
import org.georchestra.signalement.core.dto.GeographicType;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name="polygon_reporting")
public class PolygonReportingEntity extends AbstractReportingEntity {

    @Column(name = "geometry",columnDefinition="Geometry")
    private Geometry geometry;
    
    public PolygonReportingEntity() {
		super(GeographicType.POLYGON);
	}
}
