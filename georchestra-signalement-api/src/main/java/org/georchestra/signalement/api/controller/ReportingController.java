/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.georchestra.signalement.api.ReportingApi;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author FNI18300
 *
 */
@Controller
public class ReportingController implements ReportingApi {

	private static final String ATTACHMENT_FILENAME = "attachment; filename=";

	@Autowired
	private TaskService taskService;

	@Override
	public ResponseEntity<Void> uploadDocument(UUID uuid, @Valid MultipartFile file) throws Exception {
		File document = java.io.File.createTempFile("upload", ".doc");
		FileUtils.copyInputStreamToFile(file.getInputStream(), document);
		DocumentContent content = new DocumentContent(file.getOriginalFilename(), file.getContentType(), document);
		taskService.addAttachment(uuid, content);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private ResponseEntity<Resource> downloadDocument(DocumentContent documentContent) throws FileNotFoundException {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME + documentContent.getFileName());
		responseHeaders.add(HttpHeaders.CONTENT_TYPE, documentContent.getContentType());
		responseHeaders.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		InputStreamResource inputStreamResource = new InputStreamResource(documentContent.getFileStream());
		return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Resource> downloadDocument(UUID uuid, String attachmentId) throws Exception {
		DocumentContent documentContent = taskService.getAttachment(uuid, attachmentId);
		return downloadDocument(documentContent);
	}

}
