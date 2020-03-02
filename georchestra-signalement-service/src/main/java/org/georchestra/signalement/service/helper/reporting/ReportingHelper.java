/**
 * 
 */
package org.georchestra.signalement.service.helper.reporting;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dao.form.ProcessFormDefinitionCustomDao;
import org.georchestra.signalement.core.dto.Action;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.ProcessFormDefinitionSearchCriteria;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.form.ProcessFormDefinitionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PolygonReportingEntity;
import org.georchestra.signalement.service.common.UUIDJSONWriter;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
import org.georchestra.signalement.service.mapper.form.FormMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import net.minidev.json.reader.BeansWriter;

/**
 * @author FNI18300
 *
 */
@Component
public class ReportingHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingHelper.class);

	@Autowired
	private BpmnHelper bpmnHelper;

	@Autowired
	private FormMapper formMapper;

	@Autowired
	private ProcessFormDefinitionCustomDao processFormDefinitionCustomDao;

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> hydrateData(String datas) throws ParseException {
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		return parser.parse(datas, Map.class);
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateData(Map<String, Object> datas) throws IOException {
		JSONValue.registerWriter(UUID.class, new UUIDJSONWriter());
		BeansWriter beansWriter = new BeansWriter();
		StringBuilder builder = new StringBuilder();
		beansWriter.writeJSONString(datas, builder, new JSONStyle((JSONStyle.FLAG_IGNORE_NULL)));
		return builder.toString();
	}

	/**
	 * 
	 * @param geographicType
	 * @return
	 */
	public AbstractReportingEntity createReportingEntity(GeographicType geographicType) {
		AbstractReportingEntity result = null;
		switch (geographicType) {
		case POINT:
			result = new PointReportingEntity();
			break;
		case LINE:
			result = new LineReportingEntity();
			break;
		case POLYGON:
			result = new PolygonReportingEntity();
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * Construit une instance de signalement
	 * 
	 * @param contextDescription
	 * @param initiator
	 * @return
	 */
	public AbstractReportingEntity createReportingEntity(ContextDescriptionEntity contextDescription,
			String initiator) {
		AbstractReportingEntity result = createReportingEntity(contextDescription.getGeographicType());
		result.setContextDescription(contextDescription);
		result.setCreationDate(new Date());
		result.setUpdatedDate(result.getCreationDate());
		result.setUuid(UUID.randomUUID());
		result.setInitiator(initiator);
		result.setStatus(Status.DRAFT);
		return result;
	}

	/**
	 * Créé une tâche à partir d'un signalement
	 * 
	 * @param reportingDescription
	 * @return
	 */
	public Task createTaskFromReporting(ReportingDescription reportingDescription) {
		Task task = new Task();
		task.setAsset(reportingDescription);
		task.setCreationDate(reportingDescription.getCreationDate());
		task.setUpdatedDate(reportingDescription.getUpdatedDate());
		task.setStatus(reportingDescription.getStatus());
		task.setInitiator(reportingDescription.getInitiator());
		return task;
	}

	public Task createTaskFromWorkflow(org.activiti.engine.task.Task input, ReportingDescription reportingDescription) {
		Task task = new Task();
		task.setAsset(reportingDescription);
		task.setCreationDate(reportingDescription.getCreationDate());
		task.setUpdatedDate(reportingDescription.getUpdatedDate());
		task.setStatus(reportingDescription.getStatus());
		task.setInitiator(reportingDescription.getInitiator());
		List<Action> actions = bpmnHelper.computeTaskActions(input);
		task.setActions(actions);
		task.setAssignee(input.getAssignee());
		task.setId(input.getId());
		ProcessFormDefinitionSearchCriteria searchCriteria = createSearchCriteria(input);
		SortCriteria sortCriteria = createSortCriteria();
		List<ProcessFormDefinitionEntity> processFormDefinitionEntities = processFormDefinitionCustomDao
				.searchProcessFormDefintions(searchCriteria, sortCriteria);
		if (CollectionUtils.isNotEmpty(processFormDefinitionEntities)) {
			ProcessFormDefinitionEntity processFormDefinitionEntity = processFormDefinitionEntities.get(0);
			try {
				task.setForm(formMapper.entityToDto(processFormDefinitionEntity.getFormDefinition()));
			} catch (FormDefinitionException e) {
				LOGGER.warn("Failed to set form for task:" + input.getId(), e);
			}
		}
		return task;
	}

	private SortCriteria createSortCriteria() {
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addElementsItem(createAscSortCriterion("revision"));
		sortCriteria.addElementsItem(createAscSortCriterion("userTaskId"));
		return sortCriteria;
	}

	private SortCriterion createAscSortCriterion(String property) {
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setProperty(property);
		sortCriterion.asc(true);
		return sortCriterion;
	}

	private ProcessFormDefinitionSearchCriteria createSearchCriteria(org.activiti.engine.task.Task input) {
		ProcessInstance processInstance = bpmnHelper.lookupProcessInstance(input);
		UserTask userTask = bpmnHelper.lookupUserTask(input);
		ProcessFormDefinitionSearchCriteria searchCriteria = new ProcessFormDefinitionSearchCriteria(
				processInstance.getProcessDefinitionKey(), processInstance.getProcessDefinitionVersion(), true,
				userTask.getId(), true);
		return searchCriteria;
	}

}
