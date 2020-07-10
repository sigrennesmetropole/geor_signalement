/**
 * 
 */
package org.georchestra.signalement.service.helper.geojson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Feature;
import org.georchestra.signalement.core.dto.FeatureCollection;
import org.georchestra.signalement.core.dto.FeatureCollection.TypeEnum;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Geometry;
import org.georchestra.signalement.core.dto.GeometryType;
import org.georchestra.signalement.core.dto.LineString;
import org.georchestra.signalement.core.dto.Point;
import org.georchestra.signalement.core.dto.Point2D;
import org.georchestra.signalement.core.dto.PointG;
import org.georchestra.signalement.core.dto.Polygon;
import org.georchestra.signalement.core.dto.Style;
import org.georchestra.signalement.core.dto.Task;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class GeoJSonHelper {

	public FeatureCollection createFeatureCollection() {
		FeatureCollection result = new FeatureCollection();
		result.setType(TypeEnum.FEATURECOLLECTION);
		return result;
	}

	public Feature createFeature() {
		Feature feature = new Feature();
		feature.setType(Feature.TypeEnum.FEATURE);
		return feature;
	}

	public void addFeature(FeatureCollection featureCollection, Feature feature) {
		if (featureCollection != null) {
			if (featureCollection.getFeatures() == null) {
				featureCollection.setFeatures(new ArrayList<>());
			}
			featureCollection.getFeatures().add(feature);
		}
	}

	public void setGeometry(Feature feature, GeographicType type, List<Point> points) {
		Geometry geometry = null;
		if (type == GeographicType.POLYGON) {
			geometry = new Polygon();
			geometry.setType(GeometryType.POLYGON);
			List<List<Point2D>> polygons = new ArrayList<>();
			polygons.add(convertPoints(type, points));
			((Polygon) geometry).setCoordinates(new ArrayList<>());
		} else if (type == GeographicType.LINE) {
			geometry = new LineString();
			geometry.setType(GeometryType.LINESTRING);
			((LineString) geometry).setCoordinates(convertPoints(type, points));
		} else {
			geometry = new PointG();
			geometry.setType(GeometryType.POINT);
			List<Point2D> points2D = convertPoints(type, points);
			if (CollectionUtils.isNotEmpty(points2D)) {
				((PointG) geometry).setCoordinates(points2D.get(0));
			}
		}
		feature.setGeometry(geometry);
	}

	public void setProperties(Feature feature, Task task) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("assignee", task.getAssignee());
		properties.put("initiator", task.getInitiator());
		properties.put("creationDate", task.getCreationDate());
		properties.put("updatedDate", task.getUpdatedDate());
		properties.put("status", task.getStatus());
		properties.put("description", task.getAsset().getDescription());
		properties.put("contextDescription", task.getAsset().getContextDescription());
		properties.put("geographicType", task.getAsset().getGeographicType());
		properties.put("data", task.getAsset().getDatas());
		feature.setProperties(properties);
	}

	public void setStyle(Feature feature, Task task) {
		GeographicType type = task.getAsset().getGeographicType();
		List<Style> styles = new ArrayList<>();

		if (type == GeographicType.POLYGON) {
			styles.add(createPolygonStyle());
		} else if (type == GeographicType.LINE) {
			styles.add(createLineStyle());
			styles.add(createTerminalPointStyle(true));
			styles.add(createTerminalPointStyle(false));
		} else {
			styles.add(createPointStyle());
		}
		feature.setStyle(styles);
	}

	protected Style createPointStyle() {
		Style style = new Style();
		style.setIconGlyph("comment");
		style.setIconShape("square");
		style.setIconColor("#ffcc33");
		return style;
	}

	protected Style createTerminalPointStyle(boolean start) {
		Style style = createPointStyle();
		style.setIconAnchor(Arrays.asList(0.5, 0.5));
		style.setType("Point");
		if (start) {
			style.setGeometry("startPoint");
		} else {
			style.setGeometry("endPoint");
		}
		return style;

	}

	protected Style createLineStyle() {
		Style style = new Style();
		style.setColor("#ffcc33");
		style.setOpcacity(1.0);
		return style;
	}

	protected Style createPolygonStyle() {
		Style style = createLineStyle();
		style.setFillColor("#ffffff");
		style.setFillOpacity(0.2);
		return style;
	}

	protected List<Point2D> convertPoints(GeographicType type, List<Point> points) {
		List<Point2D> result = null;
		if (CollectionUtils.isNotEmpty(points)) {
			result = new ArrayList<>();
			for (Point point : points) {
				Point2D point2D = new Point2D();
				point2D.add(new BigDecimal(point.getX()));
				point2D.add(new BigDecimal(point.getY()));
				result.add(point2D);
			}
			if (type == GeographicType.POLYGON) {
				result.add(result.get(0));
			}
		}
		return result;
	}
}
