package org.georchestra.signalement.api.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasicSecurityConstants {
    public static final String SWAGGER_RESSOURCE_URL =  "/signalement/swagger-resources/**";
    public static final String SWAGGER_RESSOURCE_UI =  "/signalement/swagger-ui.html";
    public static final String SWAGGER_RESSOURCE_INDEX =  "/signalement/swagger-ui/**";

    public static final String WEBJARS_URL = "/webjars/**";

    public static final String SIGNALEMENT_URL = "/signalement/**";

    public static final String API_DOCS_URL = "/signalement/v3/api-docs/**";

    public static final String CONFIGURATION_SECURITY_URL = "/configuration/security";
    public static final String CONFIGURATION_UI_URL = "/configuration/ui";

    public static final String ADMINISTRATION_URL ="/signalement/administration/**";
    public static final String ADMINISTRATION_INITIALIZE_URL ="/signalement/administration/initialize";
    public static final String ADMINISTRATION_CONFIGURATION_URL ="/signalement/administration/configuration";
    public static final String ADMINISTRATION_PROCESSDEFINITION_SEARCH_URL ="/signalement/administration/processDefinition/search";
    
    public static final String ADMINISTRATION_EXTENSION_URL="/extension/index.js";
    public static final String ADMINISTRATION_PROXY_URL = "/proxy";
    

    public static final String ASSETS_URL = "/assets/**";
    public static final String ASSETS_JSON_URL = "/assets/**/*.json";
    public static final String ASSETS_JPEG_URL = "/assets/**/*.jpeg";
    public static final String ASSETS_SVG_URL = "/assets/**/*.svg";

    public static final String ICONES_URL = "/*.ico";
    public static final String CSS_URL = "/*.css";
    public static final String SLASH_URL = "/";
    public static final String JS_URL =  "/*.js";
    public static final String CRSF_URL =  "/csrf";


}
