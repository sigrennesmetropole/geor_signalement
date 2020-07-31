/**
 * 
 */
package org.georchestra.signalement.service.helper.geojson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Feature;
import org.georchestra.signalement.core.dto.FeatureCollection;
import org.georchestra.signalement.core.dto.FeatureCollection.TypeEnum;
import org.georchestra.signalement.core.dto.FeatureProperty;
import org.georchestra.signalement.core.dto.FeatureType;
import org.georchestra.signalement.core.dto.FeatureTypeDescription;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class GeoJSonHelper {

	@Autowired
	private Environment environment;

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

	public void setGeometry(Feature feature, GeographicType type, List<PointG> points) {
		Geometry geometry = null;
		if (type == GeographicType.POLYGON) {
			geometry = new Polygon();
			geometry.setType(GeometryType.POLYGON);
			((Polygon) geometry).setCoordinates(new ArrayList<>());
			((Polygon) geometry).getCoordinates().add(convertPoints(type, points));
		} else if (type == GeographicType.LINE) {
			geometry = new LineString();
			geometry.setType(GeometryType.LINESTRING);
			((LineString) geometry).setCoordinates(convertPoints(type, points));
		} else {
			geometry = new Point();
			geometry.setType(GeometryType.POINT);
			List<Point2D> points2D = convertPoints(type, points);
			if (CollectionUtils.isNotEmpty(points2D)) {
				((Point) geometry).setCoordinates(points2D.get(0));
			}
		}
		feature.setGeometry(geometry);
	}

	public void setProperties(Feature feature, Task task) {
		Map<String, Object> properties = new HashMap<>();
		properties.put(GeoJSonConstants.UUID, feature.getId().toString());
		properties.put(GeoJSonConstants.ID, task.getId());
		properties.put(GeoJSonConstants.ASSIGNEE, task.getAssignee());
		properties.put(GeoJSonConstants.INITIATOR, task.getInitiator());
		properties.put(GeoJSonConstants.CREATION_DATE, task.getCreationDate());
		properties.put(GeoJSonConstants.UPDATED_DATE, task.getUpdatedDate());
		properties.put(GeoJSonConstants.STATUS, task.getStatus());
		properties.put(GeoJSonConstants.DESCRIPTION, task.getAsset().getDescription());
		properties.put(GeoJSonConstants.CONTEXT_DESCRIPTION_NAME, task.getAsset().getContextDescription().getName());
		properties.put(GeoJSonConstants.CONTEXT_DESCRIPTION_LABEL, task.getAsset().getContextDescription().getLabel());
		properties.put(GeoJSonConstants.CONTEXT_DESCRIPTION_TYPE,
				task.getAsset().getContextDescription().getContextType());
		properties.put(GeoJSonConstants.GEOGRAPHIC_TYPE, task.getAsset().getGeographicType());
		properties.put(GeoJSonConstants.DATA, task.getAsset().getDatas());
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

	public void setStyle(FeatureCollection featureCollection) {
		if (featureCollection != null) {
			featureCollection.setStyle(Collections.singletonList(new Style()));
		}
	}

	protected Style createPointStyle() {
		Style style = new Style();
		style.setIconGlyph("exclamation");
		style.setIconShape("square");
		style.setIconColor("orange");
		return style;
	}

	protected Style createTerminalPointStyle(boolean start) {
		Style style = new Style();
		style.setFiltering(true);
		style.setIconColor("blue");
		style.setIconGlyph("times");
		style.setIconShape("square");
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
		style.setOpacity(1.0);
		style.setWeight(3.0);
		style.setIconAnchor(Arrays.asList(0.5, 0.5));
		return style;
	}

	protected Style createPolygonStyle() {
		Style style = createLineStyle();
		style.setColor("#e29c10");
		style.setOpacity(1.0);
		style.setFillColor("#f6e5c1");
		style.setFillOpacity(0.5);
		style.setWeight(3.0);
		style.setDashArray(Arrays.asList(6.0, 6.0));
		return style;
	}

	protected List<Point2D> convertPoints(GeographicType type, List<PointG> points) {
		List<Point2D> result = null;
		if (CollectionUtils.isNotEmpty(points)) {
			result = new ArrayList<>();
			for (PointG point : points) {
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

	public FeatureTypeDescription getGeoJSonTaskFeatureTypeDescription(GeographicType geographicType) {
		FeatureTypeDescription result = createFeatureTypeDescription();
		result.addFeatureTypesItem(createMainFeatureType(geographicType));
		return result;
	}

	private FeatureType createMainFeatureType(GeographicType geographicType) {
		FeatureType result = new FeatureType();
		result.setTypeName(environment.getProperty(
				GeoJSonConstants.FEATURE_TYPE_DESCRIPTION_PREFIX + "." + GeoJSonConstants.TYPE_NAME_PROPERTY));
		result.addPropertiesItem(createOneRequiredStringPropertyDescription(GeoJSonConstants.UUID));
		result.addPropertiesItem(createOneRequiredNumberPropertyDescription(GeoJSonConstants.ID));
		result.addPropertiesItem(createOneStringPropertyDescription(GeoJSonConstants.ASSIGNEE, true));
		result.addPropertiesItem(createOneStringPropertyDescription(GeoJSonConstants.INITIATOR, true));
		result.addPropertiesItem(createOneRequiredDatePropertyDescription(GeoJSonConstants.CREATION_DATE));
		result.addPropertiesItem(createOneDatePropertyDescription(GeoJSonConstants.UPDATED_DATE, false));
		result.addPropertiesItem(createOneRequiredStringPropertyDescription(GeoJSonConstants.STATUS));
		result.addPropertiesItem(createOneStringPropertyDescription(GeoJSonConstants.DESCRIPTION, true));
		result.addPropertiesItem(createOneRequiredStringPropertyDescription(GeoJSonConstants.CONTEXT_DESCRIPTION_NAME));
		result.addPropertiesItem(
				createOneRequiredStringPropertyDescription(GeoJSonConstants.CONTEXT_DESCRIPTION_LABEL));
		result.addPropertiesItem(createOneRequiredStringPropertyDescription(GeoJSonConstants.CONTEXT_DESCRIPTION_TYPE));
		result.addPropertiesItem(createOneRequiredStringPropertyDescription(GeoJSonConstants.GEOGRAPHIC_TYPE));

		if (geographicType != null) {
			result.addPropertiesItem(createGeometryProperty(geographicType));
		}

		return result;
	}

	private FeatureProperty createGeometryProperty(GeographicType geographicType) {
		FeatureProperty result = null;
		if (geographicType == GeographicType.POLYGON) {
			result = createPropertyDescription(GeoJSonConstants.GEOMETRY, 0, 1, true, GeoJSonConstants.GML_POLYGON_TYPE,
					GeoJSonConstants.POLYGON_TYPE);
		} else if (geographicType == GeographicType.LINE) {
			result = createPropertyDescription(GeoJSonConstants.GEOMETRY, 0, 1, true, GeoJSonConstants.GML_LINE_TYPE,
					GeoJSonConstants.LINE_TYPE);
		} else {
			result = createPropertyDescription(GeoJSonConstants.GEOMETRY, 0, 1, true, GeoJSonConstants.GML_POINT_TYPE,
					GeoJSonConstants.POINT_TYPE);
		}
		return result;
	}

	private FeatureProperty createOneDatePropertyDescription(String name, boolean nillale) {
		return createOnePropertyDescription(name, nillale, GeoJSonConstants.XSD_DATE_TYPE, GeoJSonConstants.DATE_TYPE);
	}

	@SuppressWarnings("unused")
	private FeatureProperty createOneNumberPropertyDescription(String name, boolean nillale) {
		return createOnePropertyDescription(name, nillale, GeoJSonConstants.XSD_NUMBER_TYPE,
				GeoJSonConstants.NUMBER_TYPE);
	}

	private FeatureProperty createOneStringPropertyDescription(String name, boolean nillale) {
		return createOnePropertyDescription(name, nillale, GeoJSonConstants.XSD_STRING_TYPE,
				GeoJSonConstants.STRING_TYPE);
	}

	private FeatureProperty createOneRequiredDatePropertyDescription(String name) {
		return createOneRequiredPropertyDescription(name, GeoJSonConstants.XSD_DATE_TYPE, GeoJSonConstants.DATE_TYPE);
	}

	private FeatureProperty createOneRequiredNumberPropertyDescription(String name) {
		return createOneRequiredPropertyDescription(name, GeoJSonConstants.XSD_NUMBER_TYPE,
				GeoJSonConstants.NUMBER_TYPE);
	}

	private FeatureProperty createOneRequiredStringPropertyDescription(String name) {
		return createOneRequiredPropertyDescription(name, GeoJSonConstants.XSD_STRING_TYPE,
				GeoJSonConstants.STRING_TYPE);
	}

	private FeatureProperty createOneRequiredPropertyDescription(String name, String type, String localType) {
		return createPropertyDescription(name, 1, 1, false, type, localType);
	}

	private FeatureProperty createOnePropertyDescription(String name, boolean nillale, String type, String localType) {
		return createPropertyDescription(name, 0, 1, nillale, type, localType);
	}

	private FeatureProperty createPropertyDescription(String name, int minOccur, int maxOccur, boolean nillale,
			String type, String localType) {
		FeatureProperty featureProperty = new FeatureProperty();
		featureProperty.setName(name);
		featureProperty.setLocalType(localType);
		featureProperty.setType(type);
		featureProperty.setMinOccurs(minOccur);
		featureProperty.setMaxOccurs(maxOccur);
		featureProperty.setNillable(nillale);
		return featureProperty;
	}

	private FeatureTypeDescription createFeatureTypeDescription() {
		FeatureTypeDescription result = new FeatureTypeDescription();
		result.setElementFormDefault(environment.getProperty(GeoJSonConstants.FEATURE_TYPE_DESCRIPTION_PREFIX + "."
				+ GeoJSonConstants.ELEMENT_FORM_DEFAULT_PROPERTY));
		result.setTargetNamespace(environment.getProperty(
				GeoJSonConstants.FEATURE_TYPE_DESCRIPTION_PREFIX + "." + GeoJSonConstants.TARGET_NAME_SPACE_PROPERTY));
		result.setTargetPrefix(environment.getProperty(
				GeoJSonConstants.FEATURE_TYPE_DESCRIPTION_PREFIX + "." + GeoJSonConstants.TARGET_PREFIX_PROPERTY));
		return result;
	}

}
