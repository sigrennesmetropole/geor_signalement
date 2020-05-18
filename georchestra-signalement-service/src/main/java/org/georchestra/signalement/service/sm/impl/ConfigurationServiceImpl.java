package org.georchestra.signalement.service.sm.impl;

import org.georchestra.signalement.service.bean.Configuration;
import org.georchestra.signalement.core.dto.ConfigurationData;
import org.georchestra.signalement.service.mapper.ConfigurationMapper;
import org.georchestra.signalement.service.sm.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${application.version}")
    private String version;

    @Autowired
    ConfigurationMapper configMapper;

    @Override
    public ConfigurationData getApplicationVersion() {
        return configMapper.entityToDto(new Configuration(version));
    }
}
