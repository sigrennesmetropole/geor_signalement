/**
 * 
 */
package org.georchestra.signalement.service.sm;

import java.util.List;
import java.util.UUID;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.AttachmentConfiguration;
import org.georchestra.signalement.core.dto.FeatureCollection;
import org.georchestra.signalement.core.dto.FeatureTypeDescription;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.service.dto.TaskSearchCriteria;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.exception.FormConvertException;
import org.georchestra.signalement.service.exception.FormDefinitionException;

/**
 * @author FNI18300
 *
 */
public interface TaskService {

	/**
	 * Demande d'affectation à la personne connectée
	 * 
	 * @param taskId
	 * @return
	 */
	Task claimTask(String taskId);

	/**
	 * Demande de création d'un draft signalement
	 * 
	 * @param reportingDescription
	 * @return
	 */
	Task createDraft(ReportingDescription reportingDescription);

	/**
	 * Retourne le formulaire draft associé à un contexte
	 * 
	 * @param contextDescriptionName
	 * @return
	 * @throws FormDefinitionException
	 */
	Form lookupDrafForm(String contextDescriptionName) throws FormDefinitionException;

	/**
	 * Abandon d'un signalement à l'état draft
	 * 
	 * @param reportingUuid
	 */
	void cancelDraft(UUID reportingUuid);

	/**
	 * Déclenchement d'une action sur une tâche
	 * 
	 * @param taskId
	 * @param actionName
	 * @throws DataException
	 */
	void doIt(String taskId, String actionName) throws DataException;

	/**
	 * Recherche des tâches affectées à l'utilisateur courant
	 * 
	 * @param taskSearchCriteria
	 * @return
	 */
	List<Task> searchTasks(TaskSearchCriteria taskSearchCriteria);

	/**
	 * Recherche des tâches affectées à l'utilisateur courant
	 * 
	 * @param taskSearchCriteria
	 * @return un flux GeoJSon
	 */
	FeatureCollection searchGeoJSonTasks(TaskSearchCriteria taskSearchCriteria);

	/**
	 * 
	 * @param contextName le nom d'un contexte connu du système. Si ce paramètre est
	 *                    null, le flux sortant ne contient pas de propriété
	 *                    geometry
	 * @return un flux GeoJSon avec la description des propriétés
	 */
	FeatureTypeDescription getGeoJSonTaskFeatureTypeDescription(String contextName);

	/**
	 * Retourne une tâche par son id
	 * 
	 * @param taskId
	 * @return
	 */
	Task getTask(String taskId);

	/**
	 * Mets à jour le signalement associé à la tâche
	 * 
	 * @param task
	 * @return
	 * @throws DataException
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	Task updateTask(Task task) throws DataException, FormDefinitionException, FormConvertException;

	/**
	 * Créé une nouvelle tâche à partir du signalement draft
	 * 
	 * @param task
	 * @return
	 * @throws DocumentRepositoryException
	 * @throws DataException
	 * @throws FormDefinitionException
	 * @throws FormConvertException
	 */
	Task startTask(Task task)
			throws DocumentRepositoryException, DataException, FormDefinitionException, FormConvertException;

	/**
	 * Ajoute un document sur un signalement
	 * 
	 * @param reportingUuid
	 * @param content
	 * @return
	 * @throws DocumentRepositoryException
	 */
	Attachment addAttachment(UUID reportingUuid, DocumentContent content) throws DocumentRepositoryException;

	/**
	 * Retourne la description d'un attachment pour un signalement et un id
	 * 
	 * @param reportingUuid
	 * @param attachmentId
	 * @return
	 * @throws DocumentRepositoryException
	 */
	Attachment getAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException;

	/**
	 * Retourne le document pour un signalement et un id
	 * 
	 * @param reportingUuid
	 * @param attachmentId
	 * @return
	 * @throws DocumentRepositoryException
	 */
	DocumentContent getAttachmentContent(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException;

	/**
	 * Supprime un document attaché à un signalement
	 * 
	 * @param reportingUuid
	 * @param attachmentId
	 * @throws DocumentRepositoryException
	 */
	void removeAttachment(UUID reportingUuid, Long attachmentId) throws DocumentRepositoryException;

	/**
	 * Retourne la liste des attachements d'un signalement
	 * 
	 * @param reportingUuid
	 * @return
	 */
	List<Attachment> getAttachments(UUID reportingUuid);

	/**
	 * Retourne la configuration associé à la gestion des documents
	 * 
	 * @return
	 */
	AttachmentConfiguration getAttachmentConfiguration();

}
