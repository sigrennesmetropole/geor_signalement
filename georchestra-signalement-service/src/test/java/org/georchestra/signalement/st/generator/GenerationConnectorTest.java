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
import org.georchestra.signalement.service.st.generator.datamodel.GenerationFormat;
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
			EmailDataModel emailDataModel = new EmailDataModel(null, entity,
					GenerationConnector.STRING_TEMPLATE_LOADER_PREFIX + "test:" + baos.toString());
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}

	@Test
	public void generateFileBody() {
		try {
			PointReportingEntity entity = new PointReportingEntity();
			entity.setCreationDate(new Date());
			entity.setUpdatedDate(new Date());
			entity.setContextDescription(new ContextDescriptionEntity());
			entity.getContextDescription().setLabel("Label 1");
			entity.getContextDescription().setContextType(ContextType.LAYER);
			EmailDataModel emailDataModel = new EmailDataModel(null, entity, "initiator-mail.html");
			DocumentContent document = generationConnector.generateDocument(emailDataModel);
			Assert.assertNotNull(document);
			Assert.assertNotNull(document.getFile());
			Assert.assertEquals(document.getContentType(), GenerationFormat.HTML.getTypeMime());
		} catch (Exception e) {
			Assert.fail("failed to generate:" + e.getMessage());
		}
	}
}
