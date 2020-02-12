/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.georchestra.signalement.api.TaskApi;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author FNI18300
 *
 */
@Controller
public class TaskController implements TaskApi {

	@Override
	public ResponseEntity<Task> claimTask(UUID taskUuid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Task> createDraft(@Valid ReportingDescription reportingDescription) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Void> doIt(UUID taskUuid, String actionName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<List<Task>> searchTasks() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Task> startTask(@Valid Task task) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Void> uploadDocument(UUID taskUuid, @Valid MultipartFile file) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Resource> downloadDocument(UUID taskUuid, UUID uuid) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}


}
