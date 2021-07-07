package org.georchestra.signalement.service.bean;

import lombok.Data;

/**
 * Classe contenant les élements de configuration
 */
@Data
public class Configuration {

	// Version de l'application
	private String version;

	// Nom du rôle d'administrateur de signalement
	private String roleAdministrator;
	
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
	public Configuration(String version, String role) {
		this.version = version;
		this.roleAdministrator = role;
	}

}
