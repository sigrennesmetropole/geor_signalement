package org.georchestra.signalement.api.config.swagger;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalementOpenApiSwaggerConfig extends OpenApiSwaggerConfig {

	@Bean
	public GroupedOpenApi publicApi(){
		return GroupedOpenApi.builder().group("signalement-back")
				.packagesToScan("org.georchestra.signalement.api.controller")
				.build();
	}
}
