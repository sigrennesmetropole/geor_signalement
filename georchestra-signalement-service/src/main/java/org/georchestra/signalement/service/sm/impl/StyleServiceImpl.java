package org.georchestra.signalement.service.sm.impl;

import org.georchestra.signalement.core.dao.styling.ProcessStylingCustomDao;
import org.georchestra.signalement.core.dao.styling.ProcessStylingDao;
import org.georchestra.signalement.core.dao.styling.StylingCustomDao;
import org.georchestra.signalement.core.dao.styling.StylingDao;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.helper.geojson.GeoJSonHelper;
import org.georchestra.signalement.service.helper.geojson.StyleHelper;
import org.georchestra.signalement.service.mapper.acl.ProcessStylingMapper;
import org.georchestra.signalement.service.mapper.acl.StylingMapper;
import org.georchestra.signalement.service.sm.StyleService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Transactional(readOnly = true)
public class StyleServiceImpl  implements StyleService {

    @Autowired
    StylingDao styleDao;

    @Autowired
    ProcessStylingDao processStylingDao;

    @Autowired
    ProcessStylingCustomDao processStylingCustomDao;

    @Autowired
    StylingCustomDao styleCustomDao;

    @Autowired
    ProcessStylingMapper processMapper;

    @Autowired
    UtilPageable utilPageable;

    private StyleHelper styleHelper = new StyleHelper();

    private static final GeographicType POLYGON = GeographicType.POLYGON;
    private static final GeographicType LINE = GeographicType.LINE;
    private static final GeographicType POINT = GeographicType.POINT;
    public Page<StyleContainer> searchStyles(Pageable pageable) {
        Page<StyleContainer> results = getStyles(pageable);
        return results;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteStyle(long id) throws InvalidDataException {
        StylingEntity style = styleDao.findById(id);
        if (style == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        processStylingCustomDao.deleteByStylingId(id);
        styleDao.delete(style);
    }

    @Override
    @Transactional(readOnly = false)
    public StyleContainer createStyle(StyleContainer style) throws InvalidDataException {
        if (style == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        StylingEntity s = styleHelper.mappingStyleToEntity(style);
        styleDao.save(s);
        return style;
    }

    @Override
    @Transactional(readOnly=false)
    public StyleContainer updateStyle(StyleContainer style) throws Exception {

        long id = style.getId();
        StylingEntity oldEntity = styleDao.findById(id);
        StyleContainer oldContainer;
        if(oldEntity != null) {
            oldContainer = styleHelper.mappingStyleToDto(oldEntity);
            if (oldContainer.getType().toString().equals(style.getType().toString())) {
                StylingEntity res = styleHelper.mappingStyleToEntity(style);
                styleDao.save(res);
                return style;
            }
        }
        return null;
    }

    @Override
    public List<ProcessStyling> getLinkById(Long id) throws Exception {
        List<ProcessStylingEntity> entities = processStylingDao.findByStylingId(id);
        List<ProcessStyling> res = new ArrayList<>();
        for(ProcessStylingEntity entitie: entities){
            res.add(processMapper.entityToDto(entitie));
        }
        return res;
    }

    @Override
    @Transactional(readOnly=false)
    public ProcessStyling createStyleProcess(ProcessStyling processStyling){
        if (processStyling == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        ProcessStylingEntity s = processMapper.dtoToEntity(processStyling);

        //Verify if the processStyling already exist
        ProcessStylingSearchCriteria searchCriteria = new ProcessStylingSearchCriteria();
        searchCriteria.setRevision(processStyling.getRevision());
        searchCriteria.setUserTaskId(processStyling.getUserTaskId());
        searchCriteria.setProcessDefinitionId(processStyling.getProcessDefinitionId());

        List<ProcessStylingEntity> styleProcess = processStylingCustomDao.searchProcessStylings(searchCriteria,null);

        if(styleProcess.size() > 0){
            throw new IllegalArgumentException(ErrorMessageConstants.ALREADY_EXISTS);
        }

        //Save
        s.setStyling(styleDao.getById(processStyling.getStylingId()));
        processStylingDao.save(s);
        return processStyling;
    }

    @Override
    @Transactional(readOnly=false)
    public void deleteStyleProcess(long id) throws IllegalArgumentException {
        ProcessStylingEntity styleProcess = processStylingDao.findById(id);
        if (styleProcess == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        processStylingDao.delete(styleProcess);
    }


    private Page<StyleContainer> getStyles(Pageable pageable) {

        //Get data from StylingCustomDao
        Page<StylingEntity> result = styleCustomDao.searchStyling(null,pageable);
        return result.map(styleHelper::mappingStyleToDto);

    }
}
