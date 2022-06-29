package org.georchestra.signalement.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class StylingSearchCriteria {


    private List<String> definitions;

    private List<String> names;
}
