package org.georchestra.signalement.service.helper.workflow;

import org.apache.commons.collections4.CollectionUtils;
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
	 * Récuperer le login des users pour leur affecter la tache
	 *
	 * @param reportingEntity
	 * @param roleName
	 * @return
	 */
	public List<String> computeAssignees(AbstractReportingEntity reportingEntity, String roleName) {
		List<String> assignees = new ArrayList<>();

		// récuper les user par intersction des geomtries et en fonction du role et du
		// context description
		List<UserEntity> userEntities = getUserEntities(reportingEntity, roleName);

		// Recuperer les logins des users
		if (CollectionUtils.isNotEmpty(userEntities)) {
			for (UserEntity userEntity : userEntities) {
				assignees.add(userEntity.getLogin());
			}
		}
		return assignees;
	}

	/**
	 * Récuperer le login d'un user pour lui affecter la tache
	 *
	 * @param reportingEntity
	 * @param roleName
	 * @return
	 */
	public String computeAssignee(AbstractReportingEntity reportingEntity, String roleName) {
		String assignee = null;

		// récuper les user par intersction des geomtries et en fonction du role et du
		// context description
		List<UserEntity> userEntities = getUserEntities(reportingEntity, roleName);

		// Recuperer le logins de l'utilisaeur
		if (CollectionUtils.isNotEmpty(userEntities)) {

			if (userEntities.size() > 1) {
				LOGGER.warn("Find more than user to compute human perfomer from " + reportingEntity);
			}

			assignee = userEntities.get(0).getLogin();
		}

		return assignee;

	}

	/**
	 * Récuperer la liste des utilisateur
	 * 
	 * @param reportingEntity
	 * @param roleName
	 * @return
	 */
	private List<UserEntity> getUserEntities(AbstractReportingEntity reportingEntity, String roleName) {

		// récuper l'id du role name
		Long idRole = null;
		RoleEntity roleEntity = roleDao.findByName(roleName);
		if (roleEntity != null) {
			idRole = roleEntity.getId();
		}

		// récuper l'id du context description
		Long idContextDescription = reportingEntity.getContextDescription().getId();

		// Faire l'intersection entre la geometrie du signalement et la table
		// geographicArea et recuperer l'id geographicArea
		Long idGographicArea = geographicAreaCustumDao.findGeographicAreaIntersectWithGeometry(
				reportingEntity.getGeometry(), reportingEntity.getGeographicType());

		// faire une requete custom pour recuperer la liste d'utilisateur de la tale
		// user_role_contexte
		// à partir de idGographicArea, idContextDescription et idRole

		List<UserEntity> userEntities = userDao.findUsers(idRole, idContextDescription, idGographicArea);

		return userEntities;
	}
}
