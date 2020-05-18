package org.georchestra.signalement.core.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"georchestra.signalement.core.aop"})
public class CoreBeanConfiguration {

}
