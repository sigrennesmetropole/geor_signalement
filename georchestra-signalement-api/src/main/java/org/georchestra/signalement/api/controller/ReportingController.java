/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.georchestra.signalement.api.ReportingApi;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ContextDescriptionSearchCriteria;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.service.sm.ContextDescriptionService;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;

/**
 * @author FNI18300
 *
 */
@RestController
@Api(tags = "reporting")
public class ReportingController implements ReportingApi {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";

	@Autowired
	private TaskService taskService;

	@Autowired
	private ContextDescriptionService contextDescriptionService;

	@Override
	public ResponseEntity<Attachment> uploadDocument(UUID uuid, @Valid MultipartFile file) throws Exception {
		File document = java.io.File.createTempFile("upload", ".doc");
		FileUtils.copyInputStreamToFile(file.getInputStream(), document);
		DocumentContent content = new DocumentContent(file.getOriginalFilename(), file.getContentType(), document);
		return ResponseEntity.ok(taskService.addAttachment(uuid, content));
	}

	@Override
	public ResponseEntity<Void> deleteDocument(UUID uuid, Long attachmentId) throws Exception {
		taskService.removeAttachment(uuid, attachmentId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> downloadDocument(UUID uuid, Long attachmentId) throws Exception {
		DocumentContent documentContent = taskService.getAttachment(uuid, attachmentId);
		return downloadDocument(documentContent);
	}

	@Override
	public ResponseEntity<List<ContextDescription>> searchContextDescriptions(@Valid String contextType,
			@Valid String geographicType) throws Exception {
		ContextDescriptionSearchCriteria searchCriteria = new ContextDescriptionSearchCriteria();
		if (contextType != null) {
			searchCriteria.setContextType(ContextType.valueOf(contextType));
		}
		if (geographicType != null) {
			searchCriteria.setGeographicType(GeographicType.valueOf(geographicType));
		}
		SortCriteria sortCriteria = new SortCriteria();
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setAsc(true);
		sortCriterion.setProperty("name");
		sortCriteria.addElementsItem(sortCriterion);
		return ResponseEntity.ok(contextDescriptionService.searchContextDescriptions(searchCriteria, sortCriteria));
	}

	private ResponseEntity<Resource> downloadDocument(DocumentContent documentContent) throws FileNotFoundException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentContent.getFileName());
		responseHeaders.add(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());
		responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		InputStreamResource inputStreamResource = new InputStreamResource(documentContent.getFileStream());
		return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
	}
}
