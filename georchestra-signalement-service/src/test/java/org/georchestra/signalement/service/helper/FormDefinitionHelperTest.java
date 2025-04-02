/**
 * 
 */
package org.georchestra.signalement.service.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.core.dto.FieldDefinition;
import org.georchestra.signalement.core.dto.FieldType;
import org.georchestra.signalement.core.dto.FormDefinition;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.form.FormDefinitionHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

/**
 * @author FNI18300
 *
 */
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.service", "org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
class FormDefinitionHelperTest {

	@Autowired
	private FormDefinitionHelper formDefinitionHelper;

	@Test
	void testDehydrateHydrate() throws FormDefinitionException {
		FormDefinition formDefinition = new FormDefinition();
		FieldDefinition f1 = new FieldDefinition();
		f1.setType(FieldType.STRING);
		f1.setExtendedType("textarea");
		f1.setLabel("label 1");
		f1.setName("name1");
		f1.setReadOnly(Boolean.FALSE);
		f1.setRequired(Boolean.TRUE);
		f1.setMultiple(Boolean.FALSE);
		formDefinition.addFieldDefinitions(f1);
		String s = formDefinitionHelper.deshydrateForm(formDefinition);
		FormDefinition formDefinition2 = formDefinitionHelper.hydrateForm(s);
		assertNotNull(formDefinition2);
		assertNotNull(formDefinition2.getFieldDefinitions());
		assertEquals(formDefinition2.getFieldDefinitions().size(), 1);
		assertEquals(formDefinition2.getFieldDefinitions().get(0).getName(), "name1");
	}

	@Test
	void testDehydrateHydrate_extendedType() throws FormDefinitionException {
		FormDefinition formDefinition = new FormDefinition();
		FieldDefinition f1 = new FieldDefinition();
		f1.setType(FieldType.STRING);
		f1.setExtendedType("[{\"code\":\"a\", \"label\": \"A\"},{\"code\":\"b\", \"label\": \"B\"}]");
		f1.setLabel("label 1");
		f1.setName("name1");
		f1.setReadOnly(Boolean.FALSE);
		f1.setRequired(Boolean.TRUE);
		f1.setMultiple(Boolean.FALSE);
		formDefinition.addFieldDefinitions(f1);
		String s = formDefinitionHelper.deshydrateForm(formDefinition);
		FormDefinition formDefinition2 = formDefinitionHelper.hydrateForm(s);
		assertNotNull(formDefinition2);
		assertNotNull(formDefinition2.getFieldDefinitions());
		assertEquals(formDefinition2.getFieldDefinitions().size(), 1);
		assertEquals(formDefinition2.getFieldDefinitions().get(0).getName(), "name1");
	}

	@Test
	void testDehydrateHydrate_fieldDefinitions() throws FormDefinitionException {
		try (InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("formDefinition.json")) {
			String s = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
			FormDefinition formDefinition = formDefinitionHelper.hydrateForm(s);
			assertNotNull(formDefinition);
		} catch (IOException e) {
			fail(e);
		}
	}
}
