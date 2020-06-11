package org.georchestra.signalement.core.dao.acl;

import org.georchestra.signalement.core.dao.QueryDslDao;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao extends QueryDslDao<UserEntity, Long> {

	UserEntity findByLogin(String login);

	@Query(value = "select u from UserEntity u inner join  u.userRoles ur where ur.role.id =:idRole and "
			+ "ur.contextDescription.id =:idContextDescription and ur.geographicArea.id =:idGeographicArea")
	List<UserEntity> findUsers(@Param("idRole") Long idRole, @Param("idContextDescription") Long idContextDescription,
			@Param("idGeographicArea") Long idGeographicArea);

}
