package org.georchestra.signalement.service.sm.impl;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.ColorEasement;
import org.georchestra.signalement.core.dto.FlowMapConfiguration;
import org.georchestra.signalement.core.dto.ViewMapConfiguration;
import org.georchestra.signalement.service.bean.Configuration;
import org.georchestra.signalement.core.dto.ConfigurationData;
import org.georchestra.signalement.service.mapper.ConfigurationMapper;
import org.georchestra.signalement.service.sm.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Flow;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

    @Value("${application.version}")
    private String version;

    @Value("${georchestra.role.administrator}")
    private String roleAdministrator;

    @Value("${flow.url}")
    private String flowUrl;

    @Value("${flow.layer}")
    private String flowLayer;

    @Value("${flow.matrixSet}")
    private String flowMatrixSet;

    @Value("${flow.version}")
    private String flowVersion;

    @Value("${flow.format}")
    private String flowFormat;

    @Value("${flow.projection}")
    private String flowProjection;

    @Value("${flow.style}")
    private String flowStyle;

    @Value("${flow.matrixId}")
    private String flowMatrixId;

    @Value("${view.zoom}")
    private String viewZoom;

    @Value("${view.x}")
    private String viewX;

    @Value("${view.y}")
    private String viewY;

    @Value("${color.fill.default}")
    private String defaultColor;

    @Value("${color.fill.hover}")
    private String hoverColor;

    @Value("${color.stroke.hover}")
    private String strokeHover;

    @Value("${color.stroke.default}")
    private String strokeDefault;

    @Autowired
    ConfigurationMapper configMapper;

    @Override
    public ConfigurationData getApplicationConfiguration() {

        FlowMapConfiguration resFlow = new FlowMapConfiguration();
        resFlow.setFormat(flowFormat);
        resFlow.setProjection(flowProjection);
        resFlow.setVersion(flowVersion);
        resFlow.setMatrixSet(flowMatrixSet);
        resFlow.setUrl(flowUrl);
        resFlow.setLayer(flowLayer);
        resFlow.setStyle(flowStyle);
        resFlow.setMatrixId(StringUtils.isNotEmpty(flowMatrixId)?flowMatrixId+':':flowMatrixId);

        ViewMapConfiguration resView = new ViewMapConfiguration();
        resView.setZoom(viewZoom);
        resView.setX(viewX);
        resView.setY(viewY);

        ColorEasement color = new ColorEasement();
        color.setByDefault(defaultColor);
        color.setHover(hoverColor);
        color.setStrokeByDefault(strokeDefault);
        color.setStrokeHover(strokeHover);

        return configMapper.entityToDto(new Configuration(version, roleAdministrator, resFlow, resView, color));
    }
}
