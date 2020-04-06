/**
 * 
 */
package org.georchestra.signalement.service.helper.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.st.generator.datamodel.DataModel;
import org.georchestra.signalement.service.st.generator.datamodel.GenerationFormat;

/**
 * @author FNI18300
 *
 */
public class EmailDataModel extends DataModel {

	private ExecutionEntity executionEntity;

	private AbstractReportingEntity reportingEntity;

	/**
	 * @param executionEntity
	 * @param reportingEntity
	 * @param template
	 */
	public EmailDataModel(ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity, String template) {
		super(GenerationFormat.HTML);
		this.executionEntity = executionEntity;
		this.reportingEntity = reportingEntity;
		setModelFileName(template);
	}

	@Override
	public Map<Object, Object> getDataModel() throws IOException {
		Map<Object, Object> datas = new HashMap<>();
		datas.put("execution", executionEntity);
		datas.put("reporting", reportingEntity);
		return datas;
	}

	@Override
	protected String generateFileName() {
		return "emailBody.html";
	}

}
