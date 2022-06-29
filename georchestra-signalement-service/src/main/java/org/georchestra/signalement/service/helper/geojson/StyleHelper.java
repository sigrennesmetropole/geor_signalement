package org.georchestra.signalement.service.helper.geojson;

import org.georchestra.signalement.core.dto.Style;
import org.json.JSONObject;

import java.util.Arrays;

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


    /**
     *  Get definition style object
     * @param definitionStyle
     * @return
     */
    public static JSONObject getDefinitionStyleObj(String definitionStyle) {
        if (definitionStyle != null ) {
            JSONObject definitionStyleObj = new JSONObject(definitionStyle);
            return definitionStyleObj;
        }
        return null;
    }

    /**
     *  create default style for Point
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
     *  create style for Point from json
     * @return
     */
    public static Style createPointStyle(String definitionStyle) {

        Style style = createDefaultStylePoint();

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("POINT")) {
            JSONObject point = definitionStyleObj.getJSONObject("POINT");
            if(point.has(ICONGLYPH)){
                style.setIconGlyph(point.getString(ICONGLYPH));
            }
            if(point.has(ICONSHAPE)){
                style.setIconShape(point.getString(ICONSHAPE));
            }
            if(point.has(ICONCOLOR)){
                style.setIconColor(point.getString(ICONCOLOR));
            }
        }

        return style;
    }

    /**
     *  create default style for terminal point
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
     *  create style for terminal Point from json
     * @return
     */
    protected static Style createTerminalPointStyle(boolean start, String definitionStyle) {
        Style style = createDefaultTerminalPointStyle(start);

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null) {

            JSONObject terminalPoint = definitionStyleObj.getJSONObject("LINE").getJSONObject("terminalPoint");
            if(terminalPoint != null) {
                if(terminalPoint.has(FILTERING)){
                    style.setFiltering(terminalPoint.getBoolean(FILTERING));
                }
                if(terminalPoint.has(ICONCOLOR)) {
                    style.setIconColor(terminalPoint.getString(ICONCOLOR));
                }
                if(terminalPoint.has(ICONGLYPH)) {
                    style.setIconGlyph(terminalPoint.getString(ICONGLYPH));
                }
                if(terminalPoint.has(ICONSHAPE)) {
                    style.setIconShape(terminalPoint.getString(ICONSHAPE));
                }
                if(terminalPoint.has(ICONANCHOR)) {
                    style.setIconAnchor(Arrays.asList(terminalPoint.getJSONObject(ICONANCHOR).getDouble("x"),
                            terminalPoint.getJSONObject(ICONANCHOR).getDouble("y")));
                }

            }
        }

        return style;
    }

    /**
     *  create default style for line
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

    /**
     *  create style for line from json
     * @return
     */
    public static Style createLineStyle(String definitionStyle) {
        Style style = createDefaultLineStyle();

        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("LINE")) {
            JSONObject line = definitionStyleObj.getJSONObject("LINE");

            if(line.has(COLOR)) {
                style.setColor(line.getString(COLOR));

            }
            if(line.has(OPACITY)) {
                style.setOpacity(line.getDouble(OPACITY));

            }
            if(line.has(WEIGHT)) {
                style.setWeight(line.getDouble(WEIGHT));

            }
            if(line.has(ICONANCHOR)) {
                style.setIconAnchor(Arrays.asList(line.getJSONObject(ICONANCHOR).getDouble("x"),
                        line.getJSONObject(ICONANCHOR).getDouble("y")));
            }
        }


        return style;
    }

    /**
     *  create default style for polygone
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
     *  create style for polygon from json
     * @return
     */
    public static Style createPolygonStyle(String definitionStyle) {
        Style style = createDefaultPolygonStyle();


        JSONObject definitionStyleObj = getDefinitionStyleObj(definitionStyle);

        if (definitionStyleObj != null && definitionStyleObj.has("POLYGON")) {
            JSONObject polygon = definitionStyleObj.getJSONObject("POLYGON");

            if(polygon.has(COLOR)) {
                style.setColor(polygon.getString(COLOR));
            }
            if(polygon.has(OPACITY)) {
                style.setOpacity(polygon.getDouble(OPACITY));
            }
            if(polygon.has(FILLCOLOR)) {
                style.setFillColor(polygon.getString(FILLCOLOR));
            }
            if(polygon.has(FILLOPACITY)) {
                style.setFillOpacity(polygon.getDouble(FILLOPACITY));
            }
            if(polygon.has(WEIGHT)) {
                style.setWeight(polygon.getDouble(WEIGHT));
            }
            if(polygon.has(DASHARRAY)) {
                style.setDashArray(Arrays.asList(polygon.getJSONObject(DASHARRAY).getDouble("x"),
                        polygon.getJSONObject(DASHARRAY).getDouble("y")));
            }

        }


        return style;
    }
}
