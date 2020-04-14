package org.georchestra.signalement.service.helper;

import java.util.ArrayList;
import java.util.List;

import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionCustomDao;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PolygonReportingEntity;
import org.georchestra.signalement.service.helper.workflow.AssignmentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.api", "org.georchestra.signalement.service",
		"org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
public class AssignementHelperTest {

	@Autowired
	private AssignmentHelper assignmentHelper;

	@Autowired
	private ContextDescriptionCustomDao contextDescriptionCustomDao;

	@Test
	public void testComputeAssigneesPolygon() {

		Coordinate[] coordinates = new Coordinate[4];
		coordinates[0] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		coordinates[1] = new Coordinate(-1.656548240950296, 48.11802860045592);
		coordinates[2] = new Coordinate(-1.6977469714190407, 48.10198197668687);
		coordinates[3] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		Polygon polygon = new Polygon(new GeometryFactory().createLinearRing(coordinates), null, new GeometryFactory());

		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		searchCriteria.setGeographicType(GeographicType.POLYGON);
		List<ContextDescriptionEntity> contextDescriptionEntities = contextDescriptionCustomDao
				.searchContextDescriptions(searchCriteria, null);

		PolygonReportingEntity polygonReportingEntity = new PolygonReportingEntity();
		polygonReportingEntity.setGeometry(polygon);
		polygonReportingEntity.setContextDescription(contextDescriptionEntities.get(0));
		polygonReportingEntity.setGeographicType(GeographicType.POLYGON);

		AbstractReportingEntity reportingEntity = polygonReportingEntity;

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		Assert.assertNotNull(recipients);
		Assert.assertEquals(recipients, users);

	}

	@Test
	public void testComputeAssigneesLine() {

		Coordinate[] coordinates = new Coordinate[2];
		coordinates[0] = new Coordinate(-1.7152564318682417, 48.136132343495014);
		coordinates[1] = new Coordinate(-1.656548240950296, 48.11802860045592);
		LineString line = new GeometryFactory().createLineString(coordinates);

		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		searchCriteria.setGeographicType(GeographicType.LINE);
		List<ContextDescriptionEntity> contextDescriptionEntities = contextDescriptionCustomDao
				.searchContextDescriptions(searchCriteria, null);

		LineReportingEntity lineReportingEntity = new LineReportingEntity();
		lineReportingEntity.setGeometry(line);
		lineReportingEntity.setContextDescription(contextDescriptionEntities.get(0));
		lineReportingEntity.setGeographicType(GeographicType.LINE);

		AbstractReportingEntity reportingEntity = lineReportingEntity;

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		Assert.assertNotNull(recipients);
		Assert.assertEquals(recipients, users);

	}

	@Test
	public void testComputeAssigneesPoint() {

		Coordinate coordinate = new Coordinate(-1.6792075427081012, 48.11367358371706);
		Point point = new GeometryFactory().createPoint(coordinate);

		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		searchCriteria.setGeographicType(GeographicType.POINT);
		List<ContextDescriptionEntity> contextDescriptionEntities = contextDescriptionCustomDao
				.searchContextDescriptions(searchCriteria, null);

		PointReportingEntity pointReportingEntity = new PointReportingEntity();
		pointReportingEntity.setGeometry(point);
		pointReportingEntity.setContextDescription(contextDescriptionEntities.get(0));
		pointReportingEntity.setGeographicType(GeographicType.POINT);

		AbstractReportingEntity reportingEntity = pointReportingEntity;

		List<String> recipients = assignmentHelper.computeAssignees(reportingEntity, "Validator");

		List<String> users = new ArrayList<>();
		users.add("testadmin");

		Assert.assertNotNull(recipients);
		Assert.assertEquals(recipients, users);

	}
}
