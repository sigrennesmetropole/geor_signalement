package org.georchestra.signalement.service.sm.impl;

import org.georchestra.signalement.core.dao.styling.StylingCustomDao;
import org.georchestra.signalement.core.dao.styling.StylingDao;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.helper.geojson.GeoJSonHelper;
import org.georchestra.signalement.service.helper.geojson.StyleHelper;
import org.georchestra.signalement.service.mapper.acl.StylingMapper;
import org.georchestra.signalement.service.sm.StyleService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
public class StyleServiceImpl  implements StyleService {

    @Autowired
    StylingDao styleDao;

    @Autowired
    StylingCustomDao styleCustomDao;
    @Autowired
    StylingMapper styleMapper;

    @Autowired
    UtilPageable utilPageable;

    @Autowired
    private GeoJSonHelper geoJSonHelper;

    public Page<StyleContainer> searchStyles(Pageable pageable) {
        Page<StyleContainer> results = getStyles(pageable);
        return results;
    }

    /** Intern parsing and mapping function for this special case
     * Problem : The data in the database are mostly JSON and could have different formats.
     * We need to parse them and then map them.  Default mapper cannot map all data.
     */
    private Page<StyleContainer> getStyles(Pageable pageable) {

        //Get data from StylingCustomDao
        Page<StylingEntity> result = styleCustomDao.searchStyling(null,pageable,null);
        return result.map(this::MappingStyle);

    }

    private StyleContainer MappingStyle( StylingEntity entity){
        Style style = new Style();
        JSONObject description = new JSONObject(entity.getDefinition());
        String type = description.keys().hasNext() ? description.keys().next() : null;


        //Map with the default mapper
        StyleContainer res = styleMapper.entityToDto(entity);
        //Create a style from the type of the style (Line, Point or Polygon)
        if (GeographicType.fromValue(type) == GeographicType.POLYGON) {
            style = (StyleHelper.createPolygonStyle(entity.getDefinition()));

        } else if (GeographicType.fromValue(type) == GeographicType.LINE) {
            style = (StyleHelper.createLineStyle(entity.getDefinition()));

        } else if (GeographicType.fromValue(type) == GeographicType.POINT) {
            style = (StyleHelper.createPointStyle(entity.getDefinition()));
        }
        //Finnish mapping style and type from StyleContainer
        res.setStyle(style);
        res.setType(type);
        return res;
    }
}
