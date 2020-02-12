package org.georchestra.signalement.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().and().authorizeRequests().antMatchers("/", "/*.js", "/*.css", "/*.ico").permitAll()
				// On autorise l'accès aux json de conf pour l'authent
				.antMatchers("/assets/{\\p}/*.json").permitAll()
				// On autorise l'accès aux images pour l'authent
				.antMatchers("/assets/{\\p}/*.jpeg").permitAll().antMatchers("/assets/{\\p}/{\\p}/*.svg").permitAll()
				// On autorise certains WS utilisés pour l'authent
				.antMatchers("/signalement/**").permitAll()
				// -- swagger ui
				.antMatchers("/csrf", "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/v2/api-docs/**",
						"/configuration/ui", "/configuration/security")
				.permitAll().antMatchers("/admin/**").fullyAuthenticated().and().httpBasic().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "PUT", "DELETE"));
		configuration.addAllowedHeader("*");
		configuration.setAllowCredentials(true);

		// Url autorisées
		// 4200 pour les développement | 8080 pour le déploiement
		configuration.setAllowedOrigins(Arrays.asList("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
