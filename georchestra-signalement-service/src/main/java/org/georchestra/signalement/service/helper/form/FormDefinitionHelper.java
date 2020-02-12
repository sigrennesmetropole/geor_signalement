/**
 * 
 */
package org.georchestra.signalement.service.helper.form;

import java.io.IOException;
import java.util.UUID;

import org.georchestra.signalement.core.dto.FormDefinition;
import org.georchestra.signalement.service.common.UUIDJSONWriter;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.reader.BeansWriter;

/**
 * Helper pour la sérialisation des définitions de formulaire
 * 
 * @author FNI18300
 *
 */
@Component
public class FormDefinitionHelper {

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	public FormDefinition hydrateForm(String formDefinition) throws ParseException {
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		return parser.parse(formDefinition, FormDefinition.class);
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateForm(FormDefinition form) throws IOException {
		JSONValue.registerWriter(UUID.class, new UUIDJSONWriter());
		BeansWriter beansWriter = new BeansWriter();
		StringBuilder builder = new StringBuilder();
		beansWriter.writeJSONString(form, builder, new JSONStyle((JSONStyle.FLAG_IGNORE_NULL)));
		return builder.toString();
	}

}
