/**
 * 
 */
package org.georchestra.signalement.service.helper.reporting;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.georchestra.signalement.service.common.UUIDJSONWriter;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.reader.BeansWriter;

/**
 * @author FNI18300
 *
 */
@Component
public class ReportingHelper {

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Map<String,Object> hydrateData(String datas) throws ParseException {
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		return parser.parse(datas, Map.class);
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateData(Map<String,Object> datas) throws IOException {
		JSONValue.registerWriter(UUID.class, new UUIDJSONWriter());
		BeansWriter beansWriter = new BeansWriter();
		StringBuilder builder = new StringBuilder();
		beansWriter.writeJSONString(datas, builder, new JSONStyle((JSONStyle.FLAG_IGNORE_NULL)));
		return builder.toString();
	}


}
