package org.georchestra.signalement.service.sm.impl;

import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dto.Role;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
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
            String msg = name + "not found";
            throw new InvalidDataException(msg);
        }
        //TODO : Add not used verification in a UserRoleContext
        roleDao.delete(role);
    }

    @Override
    @Transactional(rollbackFor = InvalidDataException.class)
    public Role createRole(Role role) throws InvalidDataException {
        String name = role.getName();
        String label = role.getLabel();
        if (name == null) {
            throw new InvalidDataException("No role name provided");
        }
        if (label == null) {
            throw new InvalidDataException("No role label provided");
        }
        if (getRole(name) != null) {
            String msg = name + " is already used";
            throw new InvalidDataException(msg);
        }
        roleDao.save(roleMapper.dtoToEntity(role));
        return getRole(role.getName());
    }
}
