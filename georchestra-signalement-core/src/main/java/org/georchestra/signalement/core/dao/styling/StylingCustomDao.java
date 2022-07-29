package org.georchestra.signalement.core.dao.styling;

import org.georchestra.signalement.core.dto.StylingSearchCriteria;
import org.georchestra.signalement.core.entity.styling.StylingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public interface StylingCustomDao{

    /**
     *
     * @param searchCriteria
     * @param pageable
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    Page<StylingEntity> searchStyling(StylingSearchCriteria searchCriteria, Pageable pageable
                                      );

}
