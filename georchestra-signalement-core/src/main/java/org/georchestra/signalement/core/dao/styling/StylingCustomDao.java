package org.georchestra.signalement.core.dao.styling;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.dto.ProcessStylingSearchCriteria;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.StylingSearchCriteria;
import org.georchestra.signalement.core.entity.styling.ProcessStylingEntity;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface StylingCustomDao{

    /**
     *
     * @param searchCriteria
     * @param sortCriteria
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    Page<StylingEntity> searchStyling(StylingSearchCriteria searchCriteria, Pageable pageable,
                                      SortCriteria sortCriteria);
}
