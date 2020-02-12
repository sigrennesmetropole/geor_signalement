package org.georchestra.signalement.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource("classpath:signalement-common.properties")
@Data
public class SignalementPropertiesManager {

   
}
