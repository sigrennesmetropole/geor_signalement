/**
 * 
 */
package org.georchestra.signalement.st.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.service.acl.GeographicAreaService;
import org.georchestra.signalement.service.helper.mail.EmailDataModel;
import org.georchestra.signalement.service.helper.workflow.AssignmentHelper;
import org.georchestra.signalement.service.sm.UserService;
import org.georchestra.signalement.service.st.generator.GenerationConnector;
import org.georchestra.signalement.service.st.generator.GenerationConnectorConstants;
import org.georchestra.signalement.service.st.generator.datamodel.GenerationFormat;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
/**
 * @author FNI18300
 *
 */
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.api", "org.georchestra.signalement.service",
		"org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
class GenerationConnectorTest {

	@Autowired
	private GenerationConnector generationConnector;

	@Autowired
	private UserService userService;

	@Autowired
	GeographicAreaService geographicAreaService;

	@Autowired
	AssignmentHelper assignmentHelper;

	private String roleName = "Validator";

	@Test
	void generateStringBody() {
		try (InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("models/initiator-mail.html")) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			IOUtils.copy(is, baos);
			PointReportingEntity entity = new PointReportingEntity();
			entity.setCreationDate(new Date());
			entity.setUpdatedDate(new Date());
			entity.setContextDescription(new ContextDescriptionEntity());
			entity.getContextDescription().setLabel("Label 1");
			entity.getContextDescription().setContextType(ContextType.LAYER);
			entity.setAssignee("testuser");
			EmailDataModel emailDataModel = new EmailDataModel(userService, assignmentHelper, null, entity, roleName,
					GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX + "test:" + baos.toString());
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiator() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateAssigneMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("assignee-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiatorCancelled() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-cancelled-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiatorCompletedMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-completed-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiatorHandledMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-handled-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}


	//////////////////////////

	@Test
	public void generateFileBodyTemplateRVA_Initiator() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("RVA_initiator-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateRVA_AssigneMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("RVA_assignee-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateRVA_InitiatorCancelled() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("RVA_initiator-cancelled-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateRVA_InitiatorCompletedMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("RVA_initiator-completed-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateRVA_InitiatorHandledMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("RVA_initiator-handled-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			assertNotNull(document);
			assertNotNull(document.getFile());
			assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			fail("failed to generate:" + e.getMessage());
		}
	}


	private EmailDataModel getEmailDataModel(String template) {
		PointReportingEntity entity = new PointReportingEntity();
		entity.setCreationDate(new Date());
		entity.setUpdatedDate(new Date());
		entity.setContextDescription(new ContextDescriptionEntity());
		entity.getContextDescription().setLabel("Label 1");
		entity.getContextDescription().setContextType(ContextType.LAYER);
		entity.setAssignee("testuser");
		entity.setInitiator("testuser");
		entity.setDescription("bla bla bla bla");
		entity.setGeographicType(GeographicType.POINT);
		GeometryFactory gf = new GeometryFactory();
		entity.setGeometry(gf.createPoint(new Coordinate(-1.606084, 48.126307)));
		return new EmailDataModel(userService, assignmentHelper, null, entity, roleName, template);
	}

}
