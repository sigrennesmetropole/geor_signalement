package org.georchestra.signalement.api.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Classe de configuration globale de l'application.
 */
@SpringBootApplication
@ComponentScan({ "org.georchestra.signalement.api", "org.georchestra.signalement.service",
		"org.georchestra.signalement.core" })
@EntityScan(basePackages = "org.georchestra.signalement.core.entity")
@EnableJpaRepositories(basePackages = "org.georchestra.signalement.core.dao")
@PropertySource(value = { "classpath:signalement.properties" }, ignoreResourceNotFound = false)
@PropertySource(value = { "classpath:signalement-common.properties" }, ignoreResourceNotFound = false)
public class SignalementSpringBootApplication extends SpringBootServletInitializer {

	public static void main(final String[] args) {
		// Renomage du fichier de properties pour éviter les conflits avec d'autres
		// applications sur le tomcat
		System.setProperty("spring.config.name", "signalement");
		SpringApplication.run(SignalementSpringBootApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(SignalementSpringBootApplication.class);
	}
}
