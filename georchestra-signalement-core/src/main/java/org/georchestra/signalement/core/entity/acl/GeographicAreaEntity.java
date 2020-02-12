/**
 * 
 */
package org.georchestra.signalement.core.entity.acl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.geolatte.geom.Geometry;
import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name="geographic_area")
public class GeographicAreaEntity implements LongId {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

    @Column(name = "geometry",columnDefinition="Geometry")
    private Geometry geometry;
}
