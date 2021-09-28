package org.georchestra.signalement.service.sm.impl;

import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextCustomDao;
import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.core.dto.UserRoleContextSearchCriteria;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.util.UtilPageable;
import org.georchestra.signalement.service.common.ErrorMessageConstants;
import org.georchestra.signalement.service.exception.InvalidDataException;
import org.georchestra.signalement.service.mapper.acl.RoleMapper;
import org.georchestra.signalement.service.sm.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleDao roleDao;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    UtilPageable utilPageable;

    @Autowired
    UserRoleContextCustomDao userRoleContextCustomDao;

    @Override
    public Page<Role> getPageRole(Pageable pageable) {
        return roleDao.findAll(pageable).map(roleEntity -> roleMapper.entityToDto(roleEntity));
    }

    @Override
    public Role getRole(String name) {
        RoleEntity result = roleDao.findByName(name);
        if (result == null) {
            return null;
        } else {
            return roleMapper.entityToDto(result);
        }
    }

    @Override
    @Transactional(rollbackFor = InvalidDataException.class)
    public void deleteRole(String name) throws InvalidDataException {
        RoleEntity role = roleDao.findByName(name);
        if (role == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }

        Pageable pageable = utilPageable.getPageable(0, 1, null);
        UserRoleContextSearchCriteria searchCriteria = new UserRoleContextSearchCriteria();
        searchCriteria.setRole(role);
        if (userRoleContextCustomDao.searchUserRoleContext(searchCriteria, pageable).getTotalElements() != 0) {
            throw new InvalidDataException(ErrorMessageConstants.USED_OBJECT);
        }
        roleDao.delete(role);
    }

    @Override
    @Transactional(rollbackFor = InvalidDataException.class)
    public Role createRole(Role role) throws InvalidDataException {
        if (role == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_OBJECT);
        }
        String name = role.getName();
        String label = role.getLabel();
        if (name == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_ATTRIBUTE);
        }
        if (label == null) {
            throw new IllegalArgumentException(ErrorMessageConstants.NULL_ATTRIBUTE);
        }
        if (getRole(name) != null) {
            throw new InvalidDataException(ErrorMessageConstants.ALREADY_EXISTS);
        }
        roleDao.save(roleMapper.dtoToEntity(role));
        return getRole(role.getName());
    }
}
