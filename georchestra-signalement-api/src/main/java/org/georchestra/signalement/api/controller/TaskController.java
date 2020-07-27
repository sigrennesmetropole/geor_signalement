/**
 * 
 */
package org.georchestra.signalement.api.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.api.TaskApi;
import org.georchestra.signalement.core.dto.ContextType;
import org.georchestra.signalement.core.dto.FeatureCollection;
import org.georchestra.signalement.core.dto.FeatureTypeDescription;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.service.dto.TaskSearchCriteria;
import org.georchestra.signalement.service.sm.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

/**
 * @author FNI18300
 *
 */
@RestController
@Api(tags = "tasks")
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
	public ResponseEntity<List<Task>> searchTasks(@Valid String contextName, @Valid String contextType,
			@Valid String geographicType, @Valid Boolean asAdmin) throws Exception {
		return ResponseEntity
				.ok(taskService.searchTasks(computeSearchCriteria(contextName, contextType, geographicType, asAdmin)));
	}

	@Override
	public ResponseEntity<FeatureCollection> searchGeoJSonTasks(@Valid String contextName, @Valid String contextType,
			@Valid String geographicType, @Valid Boolean asAdmin) throws Exception {
		return ResponseEntity.ok(taskService
				.searchGeoJSonTasks(computeSearchCriteria(contextName, contextType, geographicType, asAdmin)));
	}
	
	@Override
	public ResponseEntity<FeatureTypeDescription> getGeoJSonTaskProperties() throws Exception {
		return ResponseEntity.ok(taskService
				.getGeoJSonTaskFeatureTypeDescription());
	}

	@Override
	public ResponseEntity<Task> startTask(@Valid Task task) throws Exception {
		return ResponseEntity.ok(taskService.startTask(task));
	}

	@Override
	public ResponseEntity<Void> cancelDraft(UUID uuid) throws Exception {
		taskService.cancelDraft(uuid);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<Task> getTask(String taskId) throws Exception {
		return ResponseEntity.ok(taskService.getTask(taskId));
	}

	@Override
	public ResponseEntity<Task> updateTask(@Valid Task task) throws Exception {
		return ResponseEntity.ok(taskService.updateTask(task));
	}

	private TaskSearchCriteria computeSearchCriteria(String contextName, String contextType, String geographicType,
			Boolean asAdmin) {
		TaskSearchCriteria taskSearchCriteria = new TaskSearchCriteria();
		if (contextName != null) {
			taskSearchCriteria.setContextName(contextName);
		}
		if (StringUtils.isNotEmpty(contextType)) {
			taskSearchCriteria.setContextType(ContextType.valueOf(contextType));
		}
		if (StringUtils.isNotEmpty(geographicType)) {
			taskSearchCriteria.setGeographicType(GeographicType.valueOf(geographicType));
		}
		if (asAdmin != null) {
			taskSearchCriteria.setAsAdmin(asAdmin);
		}
		return taskSearchCriteria;
	}

}
