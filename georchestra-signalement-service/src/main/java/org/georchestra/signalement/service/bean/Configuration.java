package org.georchestra.signalement.service.bean;

import lombok.Data;
import org.georchestra.signalement.core.dto.ColorEasement;
import org.georchestra.signalement.core.dto.FlowMapConfiguration;
import org.georchestra.signalement.core.dto.ViewMapConfiguration;

/**
 * Classe contenant les élements de configuration
 */
@Data
public class Configuration {

	// Version de l'application
	private String version;

	// Nom du rôle d'administrateur de signalement
	private String roleAdministrator;

	// Type du flux du fond de carte du contexte
	private FlowMapConfiguration flowMapConfiguration;

	// Url du flux du fond de carte du contexte
	private ViewMapConfiguration viewMapConfiguration;

	// couleur des emprises de carte du contexte
	private ColorEasement colorEasementMapConfiguration;
	
	/**
	 * Constructeur par defaut A conserver pour l'utilisation des mapper mapStruct
	 */
	public Configuration() {
	}

	/**
	 * Constructeur avec la version et le role
	 * 
	 * @param version
	 * @param role
	 */
	public Configuration(String version, String role, FlowMapConfiguration flowMapConfiguration, ViewMapConfiguration viewMapConfiguration, ColorEasement color) {
		this.version = version;
		this.roleAdministrator = role;
		this.flowMapConfiguration = flowMapConfiguration;
		this.viewMapConfiguration = viewMapConfiguration;
		this.colorEasementMapConfiguration = color;
	}

}
