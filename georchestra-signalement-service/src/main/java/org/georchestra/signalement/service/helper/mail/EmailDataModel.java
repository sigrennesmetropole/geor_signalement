/**
 * 
 */
package org.georchestra.signalement.service.helper.mail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.helper.workflow.AssignmentHelper;
import org.georchestra.signalement.service.sm.UserService;
import org.georchestra.signalement.service.st.generator.datamodel.DataModel;
import org.georchestra.signalement.service.st.generator.datamodel.GenerationFormat;

/**
 * @author FNI18300
 *
 */
public class EmailDataModel extends DataModel {

	private UserService userService;

	private ExecutionEntity executionEntity;

	private AbstractReportingEntity reportingEntity;

	private String roleName;

	private AssignmentHelper assignmentHelper;

	/**
	 * @param executionEntity
	 * @param reportingEntity
	 * @param template
	 */
	public EmailDataModel(UserService userService, AssignmentHelper assignmentHelper, ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity, String template, String roleName) {
		super(GenerationFormat.HTML);
		this.executionEntity = executionEntity;
		this.reportingEntity = reportingEntity;
		this.userService = userService;
		this.roleName = roleName;
		this.assignmentHelper = assignmentHelper;
		setModelFileName(template);
	}

	@Override
	public Map<Object, Object> getDataModel() throws IOException {
		Map<Object, Object> datas = new HashMap<>();
		datas.put("execution", executionEntity);
		datas.put("reporting", reportingEntity);
		datas.put("roleName", roleName);
		datas.put("assignmentHelper", assignmentHelper);
		datas.put("dataModelUtils", this);
		return datas;
	}

	@Override
	protected String generateFileName() {
		return "emailBody.html";
	}

	public User getUser(String login) {
		User u = userService.getUserByLogin(login);
		if( u != null) {
			if( u.getFirstName() == null) {
				u.setFirstName(StringUtils.EMPTY);
			}
			if( u.getLastName() == null){
				u.setLastName(StringUtils.EMPTY);
			}
			if( u.getOrganization() == null) {
				u.setOrganization(StringUtils.EMPTY);
			}
		}
		return u;
	}



}
