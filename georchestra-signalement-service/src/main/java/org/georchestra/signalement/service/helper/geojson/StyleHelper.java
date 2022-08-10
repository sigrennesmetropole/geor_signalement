package org.georchestra.signalement.service.helper.geojson;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Style;
import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.service.mapper.acl.StylingMapper;
import org.georchestra.signalement.service.mapper.acl.StylingMapperImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StyleHelper {

    private static final String ICONGLYPH = "iconGlyph";
    private static final String ICONSHAPE = "iconShape";
    private static final String ICONCOLOR = "iconColor";
    private static final String ICONANCHOR = "iconAnchor";
    private static final String FILTERING = "filtering";
    private static final String COLOR = "color";
    private static final String OPACITY = "opacity";
    private static final String WEIGHT = "weight";
    private static final String FILLCOLOR = "fillColor";
    private static final String FILLOPACITY = "fillOpacity";
    private static final String DASHARRAY = "dashArray";
    private final StylingMapper styleMapper = new StylingMapperImpl();


    /**
     * Get definition style object
     *
     * @param definitionStyle
     * @return
     */
    public static JSONObject getDefinitionStyleObj(String definitionStyle) {
        if (definitionStyle != null) {
            JSONObject definitionStyleObj = new JSONObject(definitionStyle);
            return definitionStyleObj;
        }
        return null;
    }

    /**
     * create default style for Point
     *
     * @return
     */
    private static Style createDefaultStylePoint() {
        Style style = new Style();

        style.setIconGlyph("exclamation");
        style.setIconShape("square");
        style.setIconColor("orange");

        return style;
    }

    /**
     * create style for Point from json
     *
     * @return
     */
    public static Style createPointStyle(String definitionStyle) {

        Style style = createDefaultStylePoint();

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("POINT")) {
            JSONObject point = definitionStyleObj.getJSONObject("POINT");
            if (point.has(ICONGLYPH)) {
                style.setIconGlyph(point.getString(ICONGLYPH));
            }
            if (point.has(ICONSHAPE)) {
                style.setIconShape(point.getString(ICONSHAPE));
            }
            if (point.has(ICONCOLOR)) {
                style.setIconColor(point.getString(ICONCOLOR));
            }
        }
        return style;
    }

    /**
     * create default style for terminal point
     *
     * @return
     */
    private static Style createDefaultTerminalPointStyle(boolean start) {
        Style style = new Style();

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

    /**
     * create style for terminal Point from json
     *
     * @return
     */
    protected static Style createTerminalPointStyle(boolean start, String definitionStyle) {
        Style style = createDefaultTerminalPointStyle(start);

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null) {

            JSONObject terminalPoint = definitionStyleObj.getJSONObject("LINE").getJSONObject("terminalPoint");
            if (terminalPoint != null) {
                if (terminalPoint.has(FILTERING)) {
                    style.setFiltering(terminalPoint.getBoolean(FILTERING));
                }
                if (terminalPoint.has(ICONCOLOR)) {
                    style.setIconColor(terminalPoint.getString(ICONCOLOR));
                }
                if (terminalPoint.has(ICONGLYPH)) {
                    style.setIconGlyph(terminalPoint.getString(ICONGLYPH));
                }
                if (terminalPoint.has(ICONSHAPE)) {
                    style.setIconShape(terminalPoint.getString(ICONSHAPE));
                }
                if (terminalPoint.has(ICONANCHOR)) {
                    style.setIconAnchor(jsonArrayToDoubleList(terminalPoint.getJSONArray(ICONANCHOR)));
                }

            }
        }

        return style;
    }

    /**
     * create default style for line
     *
     * @return
     */
    private static Style createDefaultLineStyle() {
        Style style = new Style();

        style.setColor("#ffcc33");
        style.setOpacity(1.0);
        style.setWeight(3.0);
        style.setIconAnchor(Arrays.asList(0.5, 0.5));

        return style;
    }


    private static List<Double> jsonArrayToDoubleList(JSONArray array) {
        List<Double> res = new ArrayList<>(array.length());
        for (int i = 0; i < array.length(); i++) {
            res.add(array.optDouble(i));
        }
        return res;
    }

    /**
     * create style for line from json
     *
     * @return
     */
    public static Style createLineStyle(String definitionStyle) {
        Style style = createDefaultLineStyle();

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("LINE")) {
            JSONObject line = definitionStyleObj.getJSONObject("LINE");

            if (line.has(COLOR)) {
                style.setColor(line.getString(COLOR));

            }
            if (line.has(OPACITY)) {
                style.setOpacity(line.getDouble(OPACITY));

            }
            if (line.has(WEIGHT)) {
                style.setWeight(line.getDouble(WEIGHT));

            }
            if (line.has(ICONANCHOR)) {
                style.setIconAnchor(jsonArrayToDoubleList(line.getJSONArray(ICONANCHOR)));
            }
        }
        return style;
    }


    /**
     * create default style for polygone
     *
     * @return
     */
    private static Style createDefaultPolygonStyle() {
        Style style = new Style();

        style.setColor("#e29c10");
        style.setOpacity(1.0);
        style.setFillColor("#f6e5c1");
        style.setFillOpacity(0.5);
        style.setWeight(3.0);
        style.setDashArray(Arrays.asList(6.0, 6.0));

        return style;
    }

    /**
     * create style for polygon from json
     *
     * @return
     */
    public static Style createPolygonStyle(String definitionStyle) {
        Style style = createDefaultPolygonStyle();


        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("POLYGON")) {
            JSONObject polygon = definitionStyleObj.getJSONObject("POLYGON");

            if (polygon.has(COLOR)) {
                style.setColor(polygon.getString(COLOR));
            }
            if (polygon.has(OPACITY)) {
                style.setOpacity(polygon.getDouble(OPACITY));
            }
            if (polygon.has(FILLCOLOR)) {
                style.setFillColor(polygon.getString(FILLCOLOR));
            }
            if (polygon.has(FILLOPACITY)) {
                style.setFillOpacity(polygon.getDouble(FILLOPACITY));
            }
            if (polygon.has(WEIGHT)) {
                style.setWeight(polygon.getDouble(WEIGHT));
            }
            if (polygon.has(DASHARRAY)) {
                style.setIconAnchor(jsonArrayToDoubleList(polygon.getJSONArray(DASHARRAY)));
            }
        }
        return style;
    }

    private String getTypeFromEntity(StylingEntity entity) {
        JSONObject description = new JSONObject(entity.getDefinition());
        return getTypeFromJson(description);
    }

    private String getTypeFromJson(JSONObject description) {
        return description.keys().hasNext() ? description.keys().next() : null;
    }

    /**
     * Intern parsing and mapping function for this special case
     * Problem solved : The data in the database are mostly JSON and could have different formats.
     * We need to parse them and then map them.  Default mapper cannot map all data.
     */
    public StylingEntity mappingStyleToEntity(StyleContainer styleContainer) {

        StylingEntity res = styleMapper.dtoToEntity(styleContainer);
        JSONArray dashArray = null;
        JSONArray iconAnchor = null;
        GeographicType type = styleContainer.getType();

        //In case of type is a Polygon or a Line we need to remove the arrays before JsonObject conversion
        if (type == GeographicType.POLYGON) {
            dashArray = new JSONArray(styleContainer.getStyle().getDashArray());
            styleContainer.getStyle().setDashArray(null);
        }

        if (type == GeographicType.LINE) {
            iconAnchor = new JSONArray(styleContainer.getStyle().getIconAnchor());
            styleContainer.getStyle().setIconAnchor(null);
        }


        JSONObject description = new JSONObject(styleContainer.getStyle());
        JSONObject arr = new JSONObject();

        //Final description which follow the type
        JSONObject descriptionType = new JSONObject();
        switch (type) {
            case POINT:
                Style defaultPointStyle = createDefaultStylePoint();
                assignPoint(descriptionType, description, defaultPointStyle);
                break;

            case LINE:
                Style defaultLineStyle = createDefaultLineStyle();
                assignLine(descriptionType, description, iconAnchor, defaultLineStyle);
                break;

            case POLYGON:
                Style defaultPolygonStyle = createDefaultPolygonStyle();
                assignPolygon(descriptionType, description, dashArray, defaultPolygonStyle);
                break;
        }
        arr.put(type.name(), descriptionType);
        res.setDefinition(arr.toString());
        return res;
    }

    private void assignPoint(JSONObject descriptionType, JSONObject descriptionOrigine, Style defaultPointStyle) {
        assignValueString(descriptionType, descriptionOrigine, "iconGlyph", defaultPointStyle.getIconGlyph());
        assignValueString(descriptionType, descriptionOrigine, "iconShape", defaultPointStyle.getIconShape());
        assignValueString(descriptionType, descriptionOrigine, "iconColor", defaultPointStyle.getIconColor());

    }

    private void assignLine(JSONObject descriptionType, JSONObject descriptionOrigine, JSONArray iconAnchor, Style defaultLineStyle) {
        assignValueDouble(descriptionType, descriptionOrigine, "weight", defaultLineStyle.getWeight());
        assignValueString(descriptionType, descriptionOrigine, "filtering", defaultLineStyle.getFillColor());

        assignValueDouble(descriptionType, descriptionOrigine, "opacity", defaultLineStyle.getOpacity());
        assignValueString(descriptionType, descriptionOrigine, "color", defaultLineStyle.getColor());
        if (iconAnchor.toList().contains(null)) {
            descriptionType.put("iconAnchor", defaultLineStyle.getIconAnchor());
        } else {
            descriptionType.put("iconAnchor", iconAnchor);
        }
    }

    private void assignPolygon(JSONObject descriptionType, JSONObject descriptionOrigine, JSONArray dashArray, Style defaultPolygonStyle) {
        assignValueDouble(descriptionType, descriptionOrigine, "weight", defaultPolygonStyle.getWeight());
        assignValueString(descriptionType, descriptionOrigine, "fillColor", defaultPolygonStyle.getFillColor());
        assignValueDouble(descriptionType, descriptionOrigine, "fillOpacity", defaultPolygonStyle.getFillOpacity());

        assignValueDouble(descriptionType, descriptionOrigine, "opacity", defaultPolygonStyle.getOpacity());
        assignValueString(descriptionType, descriptionOrigine, "color", defaultPolygonStyle.getColor());
        if (dashArray.toList().contains(null)) {
            descriptionType.put("dashArray", defaultPolygonStyle.getDashArray());
        } else {
            descriptionType.put("dashArray", dashArray);
        }
    }

    private void assignValueString(JSONObject descriptionType, JSONObject descriptionOrigine, String propertyName, String defaultValue) {
        if (descriptionOrigine.has(propertyName) && StringUtils.isNotEmpty(descriptionOrigine.getString(propertyName))) {
            descriptionType.put(propertyName, descriptionOrigine.getString(propertyName));
        } else {
            descriptionType.put(propertyName, defaultValue);
        }
    }

    private void assignValueDouble(JSONObject descriptionType, JSONObject descriptionOrigine, String propertyName, Double defaultValue) {
        if (descriptionOrigine.has(propertyName)) {
            descriptionType.put(propertyName, descriptionOrigine.getDouble(propertyName));
        } else {
            descriptionType.put(propertyName, defaultValue);
        }
    }

    public StyleContainer mappingStyleToDto(StylingEntity entity) {
        Style style = new Style();

        String definition = entity.getDefinition();
        JSONObject description = getDefinitionStyleObj(definition);
        String typeString = description.keys().hasNext() ? description.keys().next() : null;
        GeographicType type = GeographicType.fromValue(typeString);

        //Map with the default mapper
        StyleContainer res = styleMapper.entityToDto(entity);
        //Create a style from the type of the style (Line, Point or Polygon)
        if (type == GeographicType.POLYGON) {
            style = StyleHelper.createPolygonStyle(definition);
        } else if (type == GeographicType.LINE) {
            style = (StyleHelper.createLineStyle(definition));
        } else if (type == GeographicType.POINT) {
            style = (StyleHelper.createPointStyle(entity.getDefinition()));
        }
        //Finnish mapping style and type from StyleContainer
        res.setStyle(style);
        res.setType(type);
        return res;
    }
}
