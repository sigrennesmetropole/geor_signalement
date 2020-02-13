/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.georchestra.signalement.api.TaskApi;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * @author FNI18300
 *
 */
@Controller
public class TaskController implements TaskApi {

	@Autowired
	private TaskService taskService;

	@Override
	public ResponseEntity<Task> claimTask(String taskId) throws Exception {
		return ResponseEntity.ok(taskService.claimTask(taskId));
	}

	@Override
	public ResponseEntity<Task> createDraft(@Valid ReportingDescription reportingDescription) throws Exception {
		return ResponseEntity.ok(taskService.createDraft(reportingDescription));
	}

	@Override
	public ResponseEntity<Void> doIt(String taskId, String actionName) throws Exception {
		taskService.doIt(taskId, actionName);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<List<Task>> searchTasks() throws Exception {
		return ResponseEntity.ok(taskService.searchTasks());
	}

	@Override
	public ResponseEntity<Task> startTask(@Valid Task task) throws Exception {
		return ResponseEntity.ok(taskService.startTask(task));
	}

}
