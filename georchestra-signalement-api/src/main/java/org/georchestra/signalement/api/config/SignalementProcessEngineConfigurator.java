/**
 * 
 */
package org.georchestra.signalement.api.config;

import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.activiti.engine.cfg.ProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author FNI18300
 *
 */
public class SignalementProcessEngineConfigurator extends AbstractProcessEngineConfigurator{

	@Value("${spring.datasource.url}")
	private String dataSourceURL;

	@Value("${spring.datasource.username}")
	private String dataSourceUsername;
	
	@Value("${spring.datasource.password}")
	private String dataSourcePassword;
	
	@Value("${spring.datasource.driver}")
	private String dataSourceDriver;
		
	@Override
	public void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
		processEngineConfiguration.setJdbcUrl(dataSourceURL);
		processEngineConfiguration.setJdbcUsername(dataSourceUsername);
		processEngineConfiguration.setJdbcPassword(dataSourcePassword);
	}

	@Override
	public void configure(ProcessEngineConfigurationImpl processEngineConfiguration) {
	}

}
