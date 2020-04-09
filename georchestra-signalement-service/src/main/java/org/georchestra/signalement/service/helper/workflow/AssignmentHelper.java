package org.georchestra.signalement.service.helper.workflow;

import org.georchestra.signalement.core.dao.acl.GeographicAreaCustumDao;
import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class AssignmentHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssignmentHelper.class);

    @Autowired
    private GeographicAreaCustumDao geographicAreaCustumDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private UserDao userDao;

    /**
     *  Récuperer la liste des users
     *
     * @param reportingEntity
     * @param roleName
     * @return
     */
    public  List<String> computeAssignees(AbstractReportingEntity reportingEntity, String roleName) {

        List<String> listUserLogin = new ArrayList<>();


        // récuper l'id du role name
        Long idRole = null;

        RoleEntity roleEntity = roleDao.findByName(roleName);
        if(roleEntity != null){
            idRole = roleEntity.getId();
        }

        // récuper l'id du context description
        Long idContextDescription = reportingEntity.getContextDescription().getId();

        // Faire l'intersection entre la geometrie du signalement et la table geographicArea et recuperer l'id geographicArea
        Long idGographicArea = geographicAreaCustumDao.findGeographicAreaIntersectWithGeometry(reportingEntity.getGeometry(), reportingEntity.getGeographicType());

        // faire une requete custom pour recuperer la liste d'utilisateur de la tale user_role_contexte
        // à partir de idGographicArea, idContextDescription et idRole

        List<UserEntity> listUserEntities = userDao.findUsers(idRole, idContextDescription, idGographicArea);

        // Recuperer les logins des users
        for(UserEntity userEntity : listUserEntities){
            listUserLogin.add(userEntity.getLogin());
        }

        return listUserLogin;
    }
}
