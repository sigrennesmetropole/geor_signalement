/**
 * 
 */
package org.georchestra.signalement.service.st.repository.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.ged.AttachmentDao;
import org.georchestra.signalement.core.entity.ged.AttachmentEntity;
import org.georchestra.signalement.service.exception.DocumentRepositoryException;
import org.georchestra.signalement.service.st.repository.DocumentRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;

/**
 * @author FNI18300
 *
 */
@Service
public class DocumentRepositoryServiceImpl implements DocumentRepositoryService {

	@Autowired
	private AttachmentDao attachmentDao;

	@Autowired
	private EntityManager entityManager;

	@Override
	public Long createDocument(List<String> attachmentIds, DocumentContent documentContent)
			throws DocumentRepositoryException {
		AttachmentEntity entity = new AttachmentEntity();
		entity.setAttachmentIds(attachmentIds);
		entity.setMimeType(documentContent.getContentType());
		entity.setName(documentContent.getFileName());

		try (InputStream is = documentContent.getFileStream()) {
			EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) entityManager.getEntityManagerFactory();
			Connection connection = info.getDataSource().getConnection();
			Blob blob = connection.createBlob();
			OutputStream os = blob.setBinaryStream(0);
			IOUtils.copy(is, os);
			entity.setContent(blob);
		} catch (Exception e) {
			throw new DocumentRepositoryException("Failed to create blob", e);
		}

		entity = attachmentDao.save(entity);
		return entity.getId();
	}

	@Override
	public DocumentContent getDocument(Long id) throws DocumentRepositoryException {
		DocumentContent result = null;
		Optional<AttachmentEntity> optionalEntity = attachmentDao.findById(id);
		if (optionalEntity.isPresent()) {
			AttachmentEntity entity = optionalEntity.get();
			result = convertEntityToDocumentContent(entity);
		}
		return result;
	}

	@Override
	public List<DocumentContent> getDocuments(String attachmentId) throws DocumentRepositoryException {
		List<DocumentContent> result = null;
		List<AttachmentEntity> entities = attachmentDao.findByAttachmentIds(attachmentId);
		if (CollectionUtils.isNotEmpty(entities)) {
			result = new ArrayList<DocumentContent>(entities.size());
			for (AttachmentEntity entity : entities) {
				result.add(convertEntityToDocumentContent(entity));
			}
		}
		return result;
	}

	@Override
	public List<Long> getDocumentIds(String attachmentId) throws DocumentRepositoryException {
		List<Long> result = null;
		List<AttachmentEntity> entities = attachmentDao.findByAttachmentIds(attachmentId);
		if (CollectionUtils.isNotEmpty(entities)) {
			result = new ArrayList<Long>(entities.size());
			for (AttachmentEntity entity : entities) {
				result.add(entity.getId());
			}
		}
		return result;
	}

	private DocumentContent convertEntityToDocumentContent(AttachmentEntity entity) throws DocumentRepositoryException {
		DocumentContent result = null;
		try (InputStream is = entity.getContent().getBinaryStream()) {
			File file = File.createTempFile("donwload", ".doc");
			FileUtils.copyInputStreamToFile(is, file);
			result = new DocumentContent(entity.getName(), entity.getMimeType(), file);
		} catch (Exception e) {
			throw new DocumentRepositoryException("Failed to read blob", e);
		}
		return result;
	}
}
