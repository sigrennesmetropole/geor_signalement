/**
 * 
 */
package org.georchestra.signalement.service.helper.workflow;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.common.DocumentContent;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.EMailData;
import org.georchestra.signalement.core.dto.Status;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.exception.DocumentGenerationException;
import org.georchestra.signalement.service.exception.DocumentModelNotFoundException;
import org.georchestra.signalement.service.exception.EMailException;
import org.georchestra.signalement.service.helper.mail.EmailDataModel;
import org.georchestra.signalement.service.st.generator.GenerationConnector;
import org.georchestra.signalement.service.st.generator.impl.CompositeTemplateLoader;
import org.georchestra.signalement.service.st.ldap.UserService;
import org.georchestra.signalement.service.st.mail.MailDescription;
import org.georchestra.signalement.service.st.mail.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author FNI18300
 *
 */
@Component
@Transactional(readOnly = true)
public class WorkflowContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowContext.class);

	@Autowired
	private ReportingDao reportingDao;

	@Autowired
	private MailService mailService;

	@Autowired
	private UserService userService;

	@Autowired
	private GenerationConnector generationConnector;

	/**
	 * Méthode utilitaire de log
	 * 
	 * @param message
	 */
	public void info(String message) {
		LOGGER.info("WkC - {}", message);
	}

	/**
	 * Méthode de mise à jour de l'état de l'asset associé
	 * 
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param statusValue     l'état cible
	 */
	@Transactional(readOnly = false)
	public void updateStatus(ScriptContext scriptContext, ExecutionEntity executionEntity, String statusValue) {
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		LOGGER.debug("WkC - Update {} to status {}", processInstanceBusinessKey, statusValue);
		Status status = Status.valueOf(statusValue);
		if (processInstanceBusinessKey != null && status != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			AbstractReportingEntity reportingEntity = reportingDao.findByUuid(uuid);
			if (reportingEntity != null) {
				reportingEntity.setStatus(status);
				reportingEntity.setUpdatedDate(new Date());
				reportingDao.save(reportingEntity);
				LOGGER.debug("WkC - Update {} to status {} done.", processInstanceBusinessKey, statusValue);
			} else {
				LOGGER.debug("WkC - Unkown {} skipped.", processInstanceBusinessKey);
			}
		} else {
			LOGGER.debug("WkC - Update {} to status {} skipped.", processInstanceBusinessKey, statusValue);
		}
	}

	/**
	 * Méthode d'envoie de courriel
	 * 
	 * @param a
	 * @param b
	 */
	public void sendEMail(ScriptContext scriptContext, ExecutionEntity executionEntity, EMailData eMailData) {
		LOGGER.debug("Send email...");
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		try {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			AbstractReportingEntity reportingEntity = reportingDao.findByUuid(uuid);
			if (reportingEntity != null && eMailData != null) {
				sendEMailToInitiator(executionEntity, reportingEntity, eMailData);
			} else {
				LOGGER.warn("WkC - No initiator to send email {}", processInstanceBusinessKey);
			}
		} catch (Exception e) {
			LOGGER.warn("WkC - Failed to send mail for " + processInstanceBusinessKey, e);
		}
	}

	private void sendEMailToInitiator(ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity,
			EMailData eMailData)
			throws EMailException, IOException, DocumentModelNotFoundException, DocumentGenerationException {
		User user = userService.getUserByLogin(reportingEntity.getInitiator());
		if (user != null) {
			MailDescription mailDescription = new MailDescription();
			mailDescription.setSubject(generateSubject(eMailData));
			mailDescription.addTo(user.getEmail());
			mailDescription.setHtml(true);
			mailDescription.setBody(generateEMailBody(executionEntity, reportingEntity, eMailData));
			mailService.sendMail(mailDescription);
		}
	}

	private String generateSubject(EMailData eMailData) {
		String subject = "No subject";
		if( StringUtils.isNotEmpty(eMailData.getSubject())){
			subject = eMailData.getSubject();
		}
		return subject;
	}

	private DocumentContent generateEMailBody(ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity,
			EMailData eMailData) throws IOException, DocumentModelNotFoundException, DocumentGenerationException {
		EmailDataModel emailDataModel = null;
		if (StringUtils.isNotEmpty(eMailData.getBody())) {
			emailDataModel = new EmailDataModel(executionEntity, reportingEntity,
					GenerationConnector.STRING_TEMPLATE_LOADER_PREFIX + reportingEntity.getUuid().toString() + ":"
							+ eMailData.getBody());
		} else {
			emailDataModel = new EmailDataModel(executionEntity, reportingEntity, eMailData.getFileBody());
		}

		return generationConnector.generateDocument(emailDataModel);
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public List<String> computePotentialOwners(ScriptContext scriptContext, ExecutionEntity executionEntity,
			String roleName) {
		return Arrays.asList("testuser");
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public String computeHumanPerformer(ScriptContext scriptContext, ExecutionEntity executionEntity, String roleName) {
		return "testuser";
	}
}
