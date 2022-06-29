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

@RestController
@Api(tags = "styles")
public class StyleController  implements StylesApi {

    @Autowired
    StyleService styleService;

    @Autowired
    UtilPageable utilPageable;

    @Override
    public ResponseEntity<StylePageResult> searchStyle(Integer offset, Integer limit, String sortExpression) throws Exception {
        Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
        Page<StyleContainer> pageResult = styleService.searchStyles(pageable);
        StylePageResult resultObject = new StylePageResult();
        resultObject.setResults(pageResult.getContent());
        resultObject.setTotalItems(pageResult.getTotalElements());
        return ResponseEntity.ok(resultObject);
    }
}
