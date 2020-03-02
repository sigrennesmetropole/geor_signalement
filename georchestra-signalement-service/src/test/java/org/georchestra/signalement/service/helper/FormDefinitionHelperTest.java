/**
 * 
 */
package org.georchestra.signalement.service.helper;

import org.georchestra.signalement.core.dto.FieldDefinition;
import org.georchestra.signalement.core.dto.FieldType;
import org.georchestra.signalement.core.dto.FormDefinition;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.form.FormDefinitionHelper;
import org.junit.Test;

/**
 * @author FNI18300
 *
 */
public class FormDefinitionHelperTest {

	@Test
	public void testDehydrateHydrate() throws FormDefinitionException {
		FormDefinitionHelper formDefinitionHelper = new FormDefinitionHelper();
		FormDefinition formDefinition = new FormDefinition();
		FieldDefinition f1 = new FieldDefinition();
		f1.setType(FieldType.STRING);
		f1.setExtendedType("textarea");
		f1.setLabel("label 1");
		f1.setName("name1");
		f1.setReadOnly(Boolean.FALSE);
		f1.setMinOccur(Integer.valueOf(0));
		f1.setMaxOccur(Integer.valueOf(1));
		formDefinition.addFieldDefinitions(f1);
		String s = formDefinitionHelper.deshydrateForm(formDefinition);
		FormDefinition formDefinition2 = formDefinitionHelper.hydrateForm(s);
		
	}
}
