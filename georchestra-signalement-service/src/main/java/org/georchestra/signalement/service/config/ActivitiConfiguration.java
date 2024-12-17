/**
 * 
 */
package org.georchestra.signalement.service.config;

import java.util.ArrayList;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.spring.SpringExpressionManager;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.service.listener.HookEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author FNI18300
 *
 */
@Configuration
public class ActivitiConfiguration extends AbstractProcessEngineConfigurator {

	@Value("${spring.datasource.url}")
	private String dataSourceURL;

	@Value("${spring.datasource.username}")
	private String dataSourceUsername;

	@Value("${spring.datasource.password}")
	private String dataSourcePassword;

	@Value("${spring.datasource.driver-class-name}")
	private String dataSourceDriver;

	@Value("${spring.datasource.schema-name:}")
	private String dataSourceSchemaName;

	@Value("${spring.datasource.schema-update:true}")
	private String dataSourceSchemaUpdate;

	@Bean
	public SpringProcessEngineConfiguration springProcessEngineConfiguration(PlatformTransactionManager transactionManager, ApplicationContext applicationContext) {
		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
		configuration.setTransactionManager(transactionManager);
		configuration.setEventListeners(new ArrayList<>());
		configuration.getEventListeners().add(new HookEventListener());
		configuration.addConfigurator(this);
		configuration.setApplicationContext(applicationContext);
		configuration.setExpressionManager(new SpringExpressionManager(applicationContext, configuration.getBeans()));
		return configuration;
	}
	@Bean
	public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration) {
		return processEngineConfiguration.buildProcessEngine();
	}

	@Override
	public void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
		processEngineConfiguration.setJdbcUrl(dataSourceURL);
		processEngineConfiguration.setJdbcUsername(dataSourceUsername);
		processEngineConfiguration.setJdbcPassword(dataSourcePassword);
		processEngineConfiguration.setJdbcDriver(dataSourceDriver);
		processEngineConfiguration.setJdbcPingEnabled(false);
		processEngineConfiguration.setJdbcPingQuery("select 1;");
		if (StringUtils.isNotEmpty(dataSourceSchemaName)) {
			processEngineConfiguration.setDatabaseSchema(dataSourceSchemaName);
		}
		processEngineConfiguration.setDatabaseSchemaUpdate(dataSourceSchemaUpdate);
	}

}
