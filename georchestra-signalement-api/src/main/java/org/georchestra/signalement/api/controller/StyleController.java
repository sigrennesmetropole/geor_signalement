package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.StylesApi;
import org.georchestra.signalement.core.dto.*;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.sm.StyleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "stylesContainer")
public class StyleController  implements StylesApi {

    @Autowired
    StyleService styleService;

    @Autowired
    UtilPageable utilPageable;

    @Override
    public ResponseEntity<StyleContainer> createStyle(StyleContainer style) throws Exception {
        return ResponseEntity.ok(styleService.createStyle(style));
    }

    @Override
    public ResponseEntity<ProcessStyling> createProcessStyling(ProcessStyling processStyle) throws Exception {
        return ResponseEntity.ok(styleService.createStyleProcess(processStyle));
    }

    @Override
    public ResponseEntity<Void> deleteStyle(Long id) throws Exception {
        styleService.deleteStyle(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteProcessStyling(Long id) throws IllegalArgumentException {
        styleService.deleteStyleProcess(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<ProcessStyling>> getStyleProcessById(Long id) throws Exception {
        return ResponseEntity.ok(styleService.getLinkById(id));
    }

    @Override
    public ResponseEntity<StylePageResult> searchStyles(Integer offset, Integer limit, String sortCriteria) throws Exception {
        Pageable pageable = utilPageable.getPageable(offset, limit, sortCriteria);
        Page<StyleContainer> pageResult = styleService.searchStyles(pageable);
        StylePageResult resultObject = new StylePageResult();
        resultObject.setResults(pageResult.getContent());
        resultObject.setTotalItems(pageResult.getTotalElements());
        return ResponseEntity.ok(resultObject);
    }

    @Override
    public ResponseEntity<StyleContainer> updateStyle(StyleContainer style) throws Exception {
        return ResponseEntity.ok(styleService.updateStyle(style));
    }
}
