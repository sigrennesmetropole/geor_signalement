package org.georchestra.signalement.service.sm;

import org.georchestra.signalement.core.dto.ConfigurationData;

/**
 * Interface du service de configuration.
 */
public interface ConfigurationService {

    /**
     * Lecture de la version de l'application.
     *
     * @return version
     */
	ConfigurationData getApplicationVersion();
}
