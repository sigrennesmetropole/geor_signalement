package org.georchestra.signalement.api.controller;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.api.ContextDescriptionsApi;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionPageResult;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("@authentificationHelper.isAdmin()")
@RestController
@Api(tags = "contextDescriptions")
public class ContextDescriptionsController implements ContextDescriptionsApi {

    @Autowired
    ContextDescriptionService contextDescriptionService;

    @Autowired
    ContextDescriptionMapper contextMapper;

    @Autowired
    UtilPageable utilPageable;

    @Override
    public ResponseEntity<Void> deleteContextDescription(String name) throws Exception {
        contextDescriptionService.deleteContextDescription(name);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ContextDescription> getContextDescription(String name) {
        return ResponseEntity.ok(contextDescriptionService.getContextDescription(name));
    }

    @Override
    public ResponseEntity<ContextDescriptionPageResult>
    searchContextDescriptions(String description, String workflow, Integer offset,
                              Integer limit, String sortCriteria) throws Exception {
        Pageable pageable = utilPageable.getPageable(offset, limit, sortCriteria);

        Page<ContextDescription> page = contextDescriptionService.
                searchPageContextDescriptions(pageable, sortBuilder(sortCriteria), description, workflow);

        ContextDescriptionPageResult resultObject = new ContextDescriptionPageResult();
        resultObject.setResults(page.getContent());
        resultObject.setTotalItems(page.getTotalElements());

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

    private SortCriteria sortBuilder(String sortString) {
        SortCriteria sortCriteria = null;
        if (StringUtils.isNotEmpty(sortString)) {
            sortCriteria = new SortCriteria();
            String[] splitted = sortString.split(" ");
            for (String criteria : splitted) {
                SortCriterion criterion = new SortCriterion();
                if (criteria.indexOf('-') == 0) {
                    criterion.setAsc(false);
                    criterion.setProperty(criteria.substring(1));
                    sortCriteria.addElementsItem(criterion);
                } else {
                    criterion.setAsc(true);
                    criterion.setProperty(criteria);
                    sortCriteria.addElementsItem(criterion);
                }
            }

        }
        return sortCriteria;
    }
}
