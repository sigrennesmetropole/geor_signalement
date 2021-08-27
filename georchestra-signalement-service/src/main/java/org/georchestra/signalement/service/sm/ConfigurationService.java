package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.ConfigurationData;

/**
 * Interface du service de configuration.
 */
public interface ConfigurationService {

    /**
     * Lecture de la configuration de l'application
     *
     * @return ConfigurationData contenant les informations de l'application
     */
	ConfigurationData getApplicationConfiguration();
}
