package org.georchestra.signalement.service.sm;
import org.georchestra.signalement.core.dto.StyleContainer;
import org.georchestra.signalement.core.dto.ProcessStyling;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StyleService {

    Page<StyleContainer> searchStyles(Pageable pageable);

    void deleteStyle(long id) throws InvalidDataException;

    StyleContainer createStyle(StyleContainer role) throws InvalidDataException;

    StyleContainer updateStyle(StyleContainer style) throws Exception;

    List<ProcessStyling> getLinkById(Long id) throws Exception;

    ProcessStyling createStyleProcess(ProcessStyling processStyling);

    void deleteStyleProcess(long id);
}
