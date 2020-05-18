package org.georchestra.signalement.api.aop;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy()
@ComponentScan(basePackages = {"org.georchestra.signalement.api.aop"})
public class ApiBeanConfiguration {


}
