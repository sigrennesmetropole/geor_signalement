package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.dto.StyleTMP;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StyleService {

    //Page<StyleTMP> searchStyles(Pageable pageable);

    //StyleTMP getStyle(String name);

    //List<StyleContainer> getStyles();

    Page<StyleContainer> searchStyles(Pageable pageable);
}
