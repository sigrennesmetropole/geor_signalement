package org.georchestra.signalement.api.controller;

import org.georchestra.signalement.api.AdministrationApi;
import org.georchestra.signalement.core.dto.ConfigurationData;
import org.georchestra.signalement.service.sm.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-12T10:28:09.523+02:00")

/**
 * Controlleur pour la configuration.
 */
@Controller
public class AdministrationController implements AdministrationApi {

    @Autowired
    private ConfigurationService configurationService;


    @Override
    public ResponseEntity<ConfigurationData> getConfiguration() throws Exception{
        return new ResponseEntity<ConfigurationData>(configurationService.getApplicationVersion(), HttpStatus.OK);
    }
}
