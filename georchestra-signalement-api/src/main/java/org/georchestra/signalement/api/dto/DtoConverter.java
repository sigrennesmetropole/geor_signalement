package org.georchestra.signalement.api.dto;


import org.georchestra.signalement.service.bean.Configuration;
import org.georchestra.signalement.service.exception.ApiServiceException;

/**
 * Classe de conversion DTO / MODEL.
 */
public final class DtoConverter {


    private DtoConverter() throws ApiServiceException {
        throw new ApiServiceException("Utility class");
    }

    /**
     * Construit l'objet de config à renvoyer au Front.
     *
     * @param version la version de l'appli
     * @return
     */
    public static Configuration buildConfigurationDto(final String version) {
        Configuration dto = new Configuration();
        dto.setVersion(version);
        return dto;
    }

}
