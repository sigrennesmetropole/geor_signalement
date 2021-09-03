package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {

    Page<Role> getPageRole(Pageable pageable);

    Role getRole(String name);

    void deleteRole(String name) throws InvalidDataException;

    Role createRole(Role role) throws InvalidDataException;
}
