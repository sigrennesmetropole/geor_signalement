/**
 * 
 */
package org.georchestra.signalement.service.helper.reporting;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Action;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.GeographicType;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.core.entity.reporting.LineReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PointReportingEntity;
import org.georchestra.signalement.core.entity.reporting.PolygonReportingEntity;
import org.georchestra.signalement.service.common.UUIDJSONWriter;
import org.georchestra.signalement.service.exception.DataException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.form.FormHelper;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
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
	private FormHelper formHelper;

	/**
	 * Parse une définition de formulaire
	 * 
	 * @param formDefinition
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> hydrateData(String datas) throws DataException {
		JSONParser parser = new JSONParser(JSONParser.MODE_PERMISSIVE);
		try {
			return parser.parse(datas, Map.class);
		} catch (ParseException e) {
			throw new DataException("Failed to hydrate data:" + datas, e);
		}
	}

	/**
	 * Serialize une définition de formulaire
	 * 
	 * @param form
	 * @return
	 * @throws IOException
	 */
	public String deshydrateData(Map<String, Object> datas) throws DataException {
		JSONValue.registerWriter(UUID.class, new UUIDJSONWriter());
		BeansWriter beansWriter = new BeansWriter();
		StringBuilder builder = new StringBuilder();
		try {
			beansWriter.writeJSONString(datas, builder, new JSONStyle((JSONStyle.FLAG_IGNORE_NULL)));
		} catch (IOException e) {
			throw new DataException("Failed to deshydrate data", e);
		}
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

		try {
			task.setForm(formHelper.lookupForm(input));
			fillFormWithData(task.getForm(), reportingDescription);
		} catch (FormDefinitionException e) {
			LOGGER.warn("Failed to set form for task:" + input.getId(), e);
		}

		return task;
	}

	private void fillFormWithData(Form form, ReportingDescription reportingDescription) {
		if (form != null && CollectionUtils.isNotEmpty(form.getSections()) && reportingDescription.getDatas() != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> datas = (Map<String, Object>) reportingDescription.getDatas();
			formHelper.fillForm(form, datas);
		}
	}
}
