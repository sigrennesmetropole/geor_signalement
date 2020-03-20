package org.georchestra.signalement.service.st.repository;

import java.util.List;

import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;

public interface DocumentRepositoryService {

	/**
	 * Créer un document
	 * 
	 * @param attachmentIds
	 * @param documentContent
	 * @return
	 */
	Long createDocument(List<String> attachmentIds, DocumentContent documentContent) throws DocumentRepositoryException;

	/**
	 * Retourne l'enveloppe d'un document associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	Attachment getDocument(Long id);

	/**
	 * Retourne un document par son id
	 * 
	 * @param id
	 * @return
	 */
	DocumentContent getDocumentContent(Long id) throws DocumentRepositoryException;

	/**
	 * Retourne l'enveloppe des documents associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	List<Attachment> getDocuments(String attachmentId);

	/**
	 * Retourne les documents associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	List<DocumentContent> getDocumentContents(String attachmentId) throws DocumentRepositoryException;

	/**
	 * Retourne les ids des documents associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	List<Long> getDocumentIds(String attachmentId) throws DocumentRepositoryException;

	/**
	 * Supprime un document par son id
	 * 
	 * @param id
	 * @throws DocumentRepositoryException
	 */
	void deleteDocument(Long id) throws DocumentRepositoryException;

	/**
	 * Supprime tous les documents associés à un identfiiant de rattachemetn
	 * 
	 * @param attachmentId
	 * @throws DocumentRepositoryException
	 */
	void deleteDocuments(String attachmentId) throws DocumentRepositoryException;
}
