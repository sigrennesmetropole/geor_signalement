package org.georchestra.signalement.core.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StylingSearchCriteria {

    private Long styleId;

    private List<String> definitions;

    private List<String> names;
}
