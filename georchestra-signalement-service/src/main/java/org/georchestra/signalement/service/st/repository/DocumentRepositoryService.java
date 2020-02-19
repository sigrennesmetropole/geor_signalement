package org.georchestra.signalement.service.st.repository;

import java.util.List;

import org.georchestra.signalement.core.common.DocumentContent;
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
	 * Retourne un document par son id
	 * 
	 * @param id
	 * @return
	 */
	DocumentContent getDocument(Long id) throws DocumentRepositoryException;

	/**
	 * Retourne les documents associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	List<DocumentContent> getDocuments(String attachmentId) throws DocumentRepositoryException;

	/**
	 * Retourne les ids des documents associé à un Id
	 * 
	 * @param attachmentId
	 * @return
	 */
	List<Long> getDocumentIds(String attachmentId) throws DocumentRepositoryException;
}
