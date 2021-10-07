package org.georchestra.signalement.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleContextSearchCriteria {

    private String userLogin;
    
    private Long userId;
    
    private String  roleName;
    
    private Long roleId;
    
    private String contextDescriptionName;
    
    private Long contextDescriptionId;
    
    private Long geographicAreaId;
}
