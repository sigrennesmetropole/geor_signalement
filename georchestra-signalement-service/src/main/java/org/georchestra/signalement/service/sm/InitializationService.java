/**
 * 
 */
package org.georchestra.signalement.service.sm;

import java.util.List;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.ProcessDefinition;
import org.georchestra.signalement.service.exception.InitializationException;

/**
 * @author FNI18300
 *
 */
public interface InitializationService {

	/**
	 * Initialise les éléments qui ne peuvent pas être intialisés en base
	 * directement
	 * 
	 * @throws InitializationException
	 */
	void initialize() throws InitializationException;

	/**
	 * Charge une nouvelle définition d'un processus sous forme d'un fichier bpmn2.0
	 * au format xml
	 *
	 * @param processDefinitionName
	 * @param documentContent
	 * @throws InitializationException
	 */
	void updateProcessDefinition(String processDefinitionName, DocumentContent documentContent)
			throws InitializationException;

	/**
	 * Supprime une définition
	 * 
	 * @param processDefinitionName
	 * @throws InitializationException
	 */
	boolean deleteProcessDefinition(String processDefinitionName) throws InitializationException;

	/**
	 * Retourne la liste des définitions
	 * 
	 * @return
	 */
	List<ProcessDefinition> searchProcessDefinitions();

}
