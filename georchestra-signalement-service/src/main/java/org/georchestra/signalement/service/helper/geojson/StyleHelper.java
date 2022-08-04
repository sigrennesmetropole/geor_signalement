package org.georchestra.signalement.service.helper.geojson;

import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.Style;
import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.service.mapper.acl.StylingMapper;
import org.georchestra.signalement.service.mapper.acl.StylingMapperImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    //Pattern in order to detect in a string a dashArray, example : ,"dashArray":[0.2,3]
    private static final Pattern dashArrayPattern = Pattern.compile(",\\\"dashArray\\\":\\[((([0-9]*(\\.[0-9]*)?(,[0-9]*(\\.[0-9]*)?)))|(null))\\]");

    //Pattern in order to detect in a string an iconAnchor example : "iconANCHOR":[0.2,3]?
    private static final Pattern iconAnchorPattern = Pattern.compile("\\\"iconAnchor\\\":\\[((([0-9]*(\\.[0-9]*)?(,[0-9]*(\\.[0-9]*)?)))|(null))\\],");
    //The comma could become a problem if the order of data are updated

    private StylingMapper styleMapper = new StylingMapperImpl();


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

    private String getTypeFromEntity(StylingEntity entity){
        JSONObject description = new JSONObject(entity.getDefinition());
        return getTypeFromJson(description);
    }
    private String getTypeFromJson(JSONObject description){
        return description.keys().hasNext() ? description.keys().next() : null;
    }

    /** Intern parsing and mapping function for this special case
     * Problem solved : The data in the database are mostly JSON and could have different formats.
     * We need to parse them and then map them.  Default mapper cannot map all data.
     */
    public StylingEntity mappingStyleToEntity(StyleContainer styleContainer) {

        StylingEntity res = styleMapper.dtoToEntity(styleContainer);
        JSONArray dashArray = null;
        JSONArray iconAnchor = null;
        String type = styleContainer.getType().toString();

        //In case of type is a Polygon or a Line we need to remove the arrays before JsonObject conversion
        if(type == "POLYGON"){
            dashArray = new JSONArray(styleContainer.getStyle().getDashArray());
            styleContainer.getStyle().setDashArray(null);
        }

        if(type == "LINE"){
            iconAnchor = new JSONArray(styleContainer.getStyle().getIconAnchor());
            styleContainer.getStyle().setIconAnchor(null);
        }


        JSONObject description = new JSONObject(styleContainer.getStyle());
        JSONObject arr = new JSONObject();

        //Final description which follow the type
        JSONObject descriptionType = new JSONObject();
        switch (type){
            case "POINT":
                Style defaultPointStyle = createDefaultStylePoint();
                descriptionType.put("iconGlyph",description.has("iconGlyph")&& !description.getString("iconGlyph").equals("")?description.get("iconGlyph"):defaultPointStyle.getIconGlyph());
                descriptionType.put("iconShape",description.has("iconShape")&& !description.getString("iconShape").equals("")?description.get("iconShape"):defaultPointStyle.getIconShape());
                descriptionType.put("iconColor",description.has("iconColor")&& !description.getString("iconColor").equals("")?description.get("iconColor"):defaultPointStyle.getIconColor());
                break;

            case "LINE":
                Style defaultLineStyle = createDefaultLineStyle();
                descriptionType.put("filtering",description.has("filtering")?description.get("filtering"):null);
                descriptionType.put("weight",description.has("weight")?description.get("weight"):defaultLineStyle.getWeight());
                descriptionType.put("opacity",description.has("opacity")?description.get("opacity"):defaultLineStyle.getFillColor());
                if(iconAnchor.toList().contains(null)){
                    descriptionType.put("iconAnchor",defaultLineStyle.getIconAnchor());
                }else{
                    descriptionType.put("iconAnchor",iconAnchor);
                }
                descriptionType.put("color",description.has("color")&& !description.getString("color").equals("")?description.get("color"):defaultLineStyle.getColor());
                break;

            case "POLYGON":
                Style defaultPolygonStyle = createDefaultPolygonStyle();
                descriptionType.put("weight",description.has("weight")?description.get("weight"):defaultPolygonStyle.getWeight());
                descriptionType.put("fillColor",description.has("fillColor")&& !description.getString("color").equals("")?description.get("fillColor"):defaultPolygonStyle.getFillColor());
                descriptionType.put("fillOpacity",description.has("fillOpacity")?description.get("fillOpacity"):defaultPolygonStyle.getFillOpacity());
                if(dashArray.toList().contains(null)){
                    descriptionType.put("dashArray",defaultPolygonStyle.getDashArray());
                }else{
                    descriptionType.put("dashArray",dashArray);
                }
                descriptionType.put("opacity",description.has("opacity")?description.get("opacity"):defaultPolygonStyle.getOpacity());
                descriptionType.put("color",description.has("color")&& !description.getString("color").equals("")?description.get("color"):defaultPolygonStyle.getColor());
                break;
        }
        arr.put(type, descriptionType);
        res.setDefinition(arr.toString());
        return res;
    }

    public StyleContainer mappingStyleToDto(StylingEntity entity){
        Style style = new Style();

        String definition = entity.getDefinition();
        String dashArrayValues = "";
        String iconAnchorValues = "";

        Matcher m = dashArrayPattern.matcher(definition);
        Matcher m2 = iconAnchorPattern.matcher(definition);
        if (m2.find()) {
            String iconAnchorString = m2.group(0);
            definition = definition.replace(iconAnchorString,"");
            iconAnchorValues = m2.group(1);
        }else if(m.find()) {
            String dashArrayString = m.group(0);
            definition = definition.replace(dashArrayString,"");
            dashArrayValues = m.group(1);
        }
        JSONObject description = new JSONObject(definition);
        String type = description.keys().hasNext() ? description.keys().next() : null;


        //Map with the default mapper
        StyleContainer res = styleMapper.entityToDto(entity);
        //Create a style from the type of the style (Line, Point or Polygon)
        if (GeographicType.fromValue(type) == GeographicType.POLYGON) {

            style = StyleHelper.createPolygonStyle(definition);
            List<Double> dashArray = new LinkedList<>();
            for (String value : dashArrayValues.split(",")){
                try {
                    dashArray.add(Double.parseDouble(value));
                }catch (NumberFormatException e){
                    dashArray.add(null);
                }
            }
            style.setDashArray(dashArray);

        } else if (GeographicType.fromValue(type) == GeographicType.LINE) {


            style = (StyleHelper.createLineStyle(definition));
            List<Double> iconAnchor = new LinkedList<>();
            for (String value : iconAnchorValues.split(",")){
                try {
                    iconAnchor.add(Double.parseDouble(value));
                }catch (NumberFormatException e){
                    iconAnchor.add(null);
                }
            }
            style.setIconAnchor(iconAnchor);

        } else if (GeographicType.fromValue(type) == GeographicType.POINT) {
            style = (StyleHelper.createPointStyle(entity.getDefinition()));
        }
        //Finnish mapping style and type from StyleContainer
        res.setStyle(style);
        res.setType(GeographicType.valueOf(type));
        return res;
    }
}
