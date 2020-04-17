/**
 * 
 */
package org.georchestra.signalement.st.generator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.service.helper.mail.EmailDataModel;
import org.georchestra.signalement.service.st.generator.GenerationConnector;
import org.georchestra.signalement.service.st.generator.GenerationConnectorConstants;
import org.georchestra.signalement.service.st.generator.datamodel.GenerationFormat;
import org.georchestra.signalement.service.st.ldap.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author FNI18300
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.api", "org.georchestra.signalement.service",
		"org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
public class GenerationConnectorTest {

	@Autowired
	private GenerationConnector generationConnector;

	@Autowired
	private UserService userService;

	@Test
	public void generateStringBody() {
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
			EmailDataModel emailDataModel = new EmailDataModel(userService, null, entity,
					GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX + "test:" + baos.toString());
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiator() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateAssigneMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("assignee-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiatorCancelled() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-cancelled-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBodyTemplateInitiatorCompletedMail() {
		try {
			EmailDataModel emailDataModel = getEmailDataModel("initiator-completed-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
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
		return new EmailDataModel(userService, null, entity, template);
	}

}
