package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.georchestra.signalement.api.ContextDescriptionsApi;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionPageResult;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "contextDescriptions")
public class ContextDescriptionsController implements ContextDescriptionsApi {

    @Autowired
    ContextDescriptionService contextDescriptionService;

    @Autowired
    ContextDescriptionMapper contextMapper;

    @Override
    public ResponseEntity<Void> deleteContextDescription(String name) throws Exception {
        contextDescriptionService.deleteContextDescription(name);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ContextDescription> getContextDescription(String name) throws Exception {
        return ResponseEntity.ok(contextDescriptionService.getContextDescription(name));
    }

    @Override
    public ResponseEntity<ContextDescriptionPageResult>
    searchContextDescriptions(String description, String workflow, Integer offset,
                              Integer limit, String sortCriteria) throws Exception {
        UtilPageable utilPageable = new UtilPageable(limit);
        Pageable pageable = utilPageable.getPageable(offset, limit, sortCriteria);

        Page<ContextDescription> page = contextDescriptionService.
                searchPageContextDescriptions(pageable, description, workflow);
        long totalItems = page.getTotalElements();

        List<ContextDescription> results = page.getContent();

        ContextDescriptionPageResult resultObject = new ContextDescriptionPageResult();

        resultObject.setResults(results);
        resultObject.setTotalItems(new BigDecimal(totalItems));

        return ResponseEntity.ok().body(resultObject);
    }

    @Override
    public ResponseEntity<ContextDescription> createContextDescription(ContextDescription contextDescription) throws Exception {
        return ResponseEntity.ok(contextDescriptionService.createContextDescription(contextDescription));
    }

    @Override
    public ResponseEntity<ContextDescription> updateContextDescription(ContextDescription contextDescription) throws Exception {
        return ResponseEntity.ok(contextDescriptionService.updateContextDescription(contextDescription));
    }
}
