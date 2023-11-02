package org.georchestra.signalement.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaCustomDao;
import org.georchestra.signalement.core.dao.acl.GeographicAreaDao;
import org.georchestra.signalement.core.dao.acl.RoleCustomDao;
import org.georchestra.signalement.core.dao.acl.RoleDao;
import org.georchestra.signalement.core.dao.acl.UserCustomDao;
import org.georchestra.signalement.core.dao.acl.UserDao;
import org.georchestra.signalement.core.dao.acl.UserRoleContextDao;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicAreaSearchCriteria;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.RoleSearchCriteria;
import org.georchestra.signalement.core.dto.UserSearchCriteria;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.acl.GeographicAreaEntity;
import org.georchestra.signalement.core.entity.acl.RoleEntity;
import org.georchestra.signalement.core.entity.acl.UserEntity;
import org.georchestra.signalement.core.entity.acl.UserRoleContextEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PolygonReportingEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.helper.workflow.AssignmentHelper;
import org.georchestra.signalement.service.mapper.acl.GeographicAreaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles("test")
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.service", "org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
class AssignementHelperTest {

	@Autowired
	private AssignmentHelper assignmentHelper;

	@Autowired
	private ContextDescriptionCustomDao contextDescriptionCustomDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	@Autowired
	private UserRoleContextDao userRoleContextDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserCustomDao userCustomDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private RoleCustomDao roleCustomDao;

	@Autowired
	private GeographicAreaDao geographicAreaDao;

	@Autowired
	private GeographicAreaCustomDao geographicAreaCustomDao;

	@Autowired
	private GeographicAreaMapper geographicAreaMapper;

	@MockBean
	private GeographicAreaService geographicAreaService;

	private GeographicAreaEntity geographicArea;

	@BeforeEach
	void init() {
		geographicArea = createGeographicArea();
		UserEntity user = createUser();
		RoleEntity role = createRole();
		ContextDescriptionEntity context1 = getOrCreateContextDescription("point", GeographicType.POINT);
		createUserRoleContext(context1, user, role, geographicArea);
		ContextDescriptionEntity context2 = getOrCreateContextDescription("polygon", GeographicType.POLYGON);
		createUserRoleContext(context2, user, role, geographicArea);
		ContextDescriptionEntity context3 = getOrCreateContextDescription("line", GeographicType.LINE);
		createUserRoleContext(context3, user, role, geographicArea);

	}

	private ContextDescriptionEntity getOrCreateContextDescription(String name, GeographicType type) {
		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		searchCriteria.setGeographicType(type);
		List<ContextDescriptionEntity> contextDescriptionEntities = contextDescriptionCustomDao
				.searchContextDescriptions(searchCriteria, null);
		if (CollectionUtils.isNotEmpty(contextDescriptionEntities)) {
			return contextDescriptionEntities.get(0);
		}
		ContextDescriptionEntity item = new ContextDescriptionEntity();
		item.setGeographicType(type);
		item.setContextType(ContextType.THEMA);
		item.setLabel(name);
		item.setName(name);
		return contextDescriptionDao.save(item);
	}

	private GeographicAreaEntity createGeographicArea() {
		GeographicAreaSearchCriteria searchCriteria = new GeographicAreaSearchCriteria();
		searchCriteria.setNom("Cesson-Sévigné");
		Page<GeographicAreaEntity> gs = geographicAreaCustomDao.searchGeographicAreas(searchCriteria,
				Pageable.ofSize(5), null);
		if (gs.isEmpty()) {
			GeographicAreaEntity geographicArea = new GeographicAreaEntity();
			geographicArea.setCodeInsee("35510");
			geographicArea.setNom("Cesson-Sévigné");
			return geographicAreaDao.save(geographicArea);
		} else {
			return gs.getContent().get(0);
		}
	}

	private UserEntity createUser() {
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria("testadmin", null);
		Page<UserEntity> users = userCustomDao.searchUsers(userSearchCriteria, Pageable.ofSize(5));
		if (users.isEmpty()) {
			UserEntity user = new UserEntity();
			user.setLogin("testadmin");
			user.setRoles(List.of("Validator"));
			user.setEmail("testadmin@test.fr");
			user.setUserRoles(null);
			return userDao.save(user);
		} else {
			return users.getContent().get(0);
		}
	}

	private UserRoleContextEntity createUserRoleContext(ContextDescriptionEntity context, UserEntity user,
			RoleEntity role, GeographicAreaEntity geographicArea) {
		UserRoleContextEntity item = new UserRoleContextEntity();
		item.setContextDescription(context);
		item.setGeographicArea(geographicArea);
		item.setRole(role);
		item.setUser(user);
		return userRoleContextDao.save(item);
	}

	private RoleEntity createRole() {
		RoleSearchCriteria roleSearchCriteria = new RoleSearchCriteria();
		List<RoleEntity> roles = roleCustomDao.searchRoles(roleSearchCriteria, null);
		RoleEntity result = null;
		if (!roles.isEmpty()) {
			result = roles.stream().filter(i -> i.getName().equals("Validator")).findFirst().orElse(null);
		}
		if (result != null) {
			return result;
		}
		RoleEntity role = new RoleEntity();
		role.setLabel("Validator");
		role.setName("Validator");
		return roleDao.save(role);
	}

	@Test
	void testComputeAssigneesPolygon() {

		Coordinate[] coordinates = new Coordinate[4];
		coordinates[0] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		coordinates[1] = new Coordinate(-1.656548240950296, 48.11802860045592);
		coordinates[2] = new Coordinate(-1.6977469714190407, 48.10198197668687);
		coordinates[3] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		Polygon polygon = new Polygon(new GeometryFactory().createLinearRing(coordinates), null, new GeometryFactory());

		ContextDescriptionEntity contextDescription = getOrCreateContextDescription("polygon", GeographicType.POLYGON);

		PolygonReportingEntity polygonReportingEntity = new PolygonReportingEntity();
		polygonReportingEntity.setGeometry(polygon);
		polygonReportingEntity.setContextDescription(contextDescription);
		polygonReportingEntity.setGeographicType(GeographicType.POLYGON);

		AbstractReportingEntity reportingEntity = polygonReportingEntity;

		when(geographicAreaService.searchGeographicAreaIntersections(any(), any(), any(), any()))
				.thenReturn(List.of(geographicAreaMapper.entityToDto(geographicArea)));

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		assertNotNull(recipients);
		assertEquals(recipients, users);

	}

	@Test
	void testComputeAssigneesLine() {

		Coordinate[] coordinates = new Coordinate[2];
		coordinates[0] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		coordinates[1] = new Coordinate(-1.656548240950296, 48.11802860045592);
		LineString line = new GeometryFactory().createLineString(coordinates);

		ContextDescriptionEntity contextDescription = getOrCreateContextDescription("line", GeographicType.LINE);

		LineReportingEntity lineReportingEntity = new LineReportingEntity();
		lineReportingEntity.setGeometry(line);
		lineReportingEntity.setContextDescription(contextDescription);
		lineReportingEntity.setGeographicType(GeographicType.LINE);

		AbstractReportingEntity reportingEntity = lineReportingEntity;

		when(geographicAreaService.searchGeographicAreaIntersections(any(), any(), any(), any()))
				.thenReturn(List.of(geographicAreaMapper.entityToDto(geographicArea)));

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		assertNotNull(recipients);
		assertEquals(recipients, users);

	}

	@Test
	void testComputeAssigneesPoint() {

		Coordinate coordinate = new Coordinate(-1.6792075427081012, 48.11367358371706);
		Point point = new GeometryFactory().createPoint(coordinate);

		ContextDescriptionEntity contextDescription = getOrCreateContextDescription("point", GeographicType.POINT);

		PointReportingEntity pointReportingEntity = new PointReportingEntity();
		pointReportingEntity.setGeometry(point);
		pointReportingEntity.setContextDescription(contextDescription);
		pointReportingEntity.setGeographicType(GeographicType.POINT);

		AbstractReportingEntity reportingEntity = pointReportingEntity;

		when(geographicAreaService.searchGeographicAreaIntersections(any(), any(), any(), any()))
				.thenReturn(List.of(geographicAreaMapper.entityToDto(geographicArea)));

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		assertNotNull(recipients);
		assertEquals(recipients, users);

	}

}
