package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.GeographicareasApi;
import org.georchestra.signalement.core.dto.GeographicArea;
import org.georchestra.signalement.core.dto.GeographicAreaPageResult;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * GeographicAreas Controller.
 */
@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "geographicAreas")
public class GeographicAreasController implements GeographicareasApi {

    @Autowired
    GeographicAreaService geographicAreaService;

    @Override
    public ResponseEntity<GeographicArea> getGeographicArea(String name) throws Exception {
        return ResponseEntity.ok(geographicAreaService.getGeographicArea(name));
    }

    @Override
    public ResponseEntity<GeographicAreaPageResult> searchGeographicAreas(String name, Integer offset, Integer limit, String sortExpression) throws Exception {

        UtilPageable utilPageable = new UtilPageable(limit);
        Pageable pageable = utilPageable.getPageable(offset, limit, sortExpression);
        Page<GeographicArea> pageResult = geographicAreaService.searchGeographicAreas(pageable, name);
        GeographicAreaPageResult resultObject = new GeographicAreaPageResult();
        resultObject.setResults(pageResult.getContent());
        resultObject.setTotalItems(new BigDecimal(pageResult.getTotalElements()));

        return ResponseEntity.ok(resultObject);
    }
}