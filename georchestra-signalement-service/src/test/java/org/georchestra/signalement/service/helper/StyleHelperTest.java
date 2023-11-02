/**
 * 
 */
package org.georchestra.signalement.service.helper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.georchestra.signalement.StarterSpringBootTestApplication;
import org.georchestra.signalement.service.helper.geojson.StyleHelper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;

/**
 * @author FNI18300
 *
 */
@SpringBootTest(classes = StarterSpringBootTestApplication.class)
@ComponentScan({ "org.georchestra.signalement.service",
		"org.georchestra.signalement.core" })
@TestPropertySource(value = { "classpath:signalement.properties", "classpath:signalement-common.properties" })
class StyleHelperTest {
	
	@Test
	void testParseJSON() {
		JSONObject object = StyleHelper.getDefinitionStyleObj("{\"p\": [ 12, 5 ]}");
		assertNotNull(object);
		
		object = StyleHelper.getDefinitionStyleObj("{\"POLYGON\":{\"fillColor\":\"test\",\"fillOpacity\":0.5,\"color\":\"test2\",\"weight\":1,\"dashArray\":[0,2],\"opacity\":0.5}}");
		assertNotNull(object);
	}
	
	
}
