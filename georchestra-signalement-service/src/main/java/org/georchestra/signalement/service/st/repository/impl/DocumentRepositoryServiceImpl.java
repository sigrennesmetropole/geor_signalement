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
import org.hibernate.engine.jdbc.BlobProxy;
import org.postgresql.jdbc.PgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Service
@Transactional(readOnly = true)
public class DocumentRepositoryServiceImpl implements DocumentRepositoryService {

	@Autowired
	private AttachmentDao attachmentDao;

	@Autowired
	private EntityManager entityManager;

	@Override
	@Transactional(readOnly = false)
	public Long createDocument(List<String> attachmentIds, DocumentContent documentContent)
			throws DocumentRepositoryException {
		AttachmentEntity entity = new AttachmentEntity();
		entity.setAttachmentIds(attachmentIds);
		entity.setMimeType(documentContent.getContentType());
		entity.setName(documentContent.getFileName());

		try (InputStream is = documentContent.getFileStream()) {
			EntityManagerFactoryInfo info = (EntityManagerFactoryInfo) entityManager.getEntityManagerFactory();
			Connection connection = info.getDataSource().getConnection();
			if (connection instanceof java.sql.Wrapper) {
				connection = ((java.sql.Wrapper) connection).unwrap(PgConnection.class);
			}
			if (connection instanceof PgConnection) {
				Blob blob = BlobProxy.generateProxy(is, documentContent.getFileSize());
				entity.setContent(blob);
			} else {
				Blob blob = connection.createBlob();
				OutputStream os = blob.setBinaryStream(0);
				IOUtils.copy(is, os);
				entity.setContent(blob);
			}
			entity = attachmentDao.save(entity);
		} catch (Exception e) {
			throw new DocumentRepositoryException("Failed to create blob", e);
		}

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

	@Override
	@Transactional(readOnly = false)
	public void deleteDocument(Long id) throws DocumentRepositoryException {
		Optional<AttachmentEntity> optionalEntity = attachmentDao.findById(id);
		if (optionalEntity.isPresent()) {
			attachmentDao.delete(optionalEntity.get());
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteDocuments(String attachmentId) throws DocumentRepositoryException {
		List<AttachmentEntity> entities = attachmentDao.findByAttachmentIds(attachmentId);
		if (CollectionUtils.isNotEmpty(entities)) {
			for (AttachmentEntity entity : entities) {
				attachmentDao.delete(entity);
			}
		}
	}
}
