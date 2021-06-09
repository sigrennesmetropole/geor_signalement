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
import org.georchestra.signalement.service.acl.GeographicAreaService;
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

	private GeographicAreaService geographicAreaService;

	/**
	 * @param executionEntity
	 * @param reportingEntity
	 * @param template
	 */
	public EmailDataModel(UserService userService, GeographicAreaService geographicAreaService, ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity, String template) {
		super(GenerationFormat.HTML);
		this.executionEntity = executionEntity;
		this.reportingEntity = reportingEntity;
		this.userService = userService;
		this.geographicAreaService = geographicAreaService;
		setModelFileName(template);
	}

	@Override
	public Map<Object, Object> getDataModel() throws IOException {
		Map<Object, Object> datas = new HashMap<>();
		datas.put("execution", executionEntity);
		datas.put("reporting", reportingEntity);
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

	/**
	 * Permet d'obtenir la commune dans laquelle à été posé le signalement
	 * @param reportingEntity
	 * @return
	 */
	public String getCommuneDuSignalement(AbstractReportingEntity reportingEntity) {
		return this.geographicAreaService.getNomGeographicAreaIntersectWithGeometry(reportingEntity.getGeometry(), reportingEntity.getGeographicType());
	}

}
