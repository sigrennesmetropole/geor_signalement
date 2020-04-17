/**
 * 
 */
package org.georchestra.signalement.service.helper.workflow;

import java.io.IOException;
import java.util.*;

import javax.script.ScriptContext;

import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.apache.commons.collections4.CollectionUtils;
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
import org.georchestra.signalement.service.st.generator.GenerationConnectorConstants;
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

	@Autowired
	private AssignmentHelper assignmentHelper;

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
	 * Envoi de courriel
	 * @param scriptContext   le context du script
	 * @param executionEntity le context d'execution
	 * @param eMailData
	 */
	public void sendEMail(ScriptContext scriptContext, ExecutionEntity executionEntity, EMailData eMailData) {
		LOGGER.debug("Send email...");
		try {
			AbstractReportingEntity reportingEntity = lookupReportingEntity(executionEntity);
			if (reportingEntity != null && eMailData != null) {
				sendEMail(executionEntity, reportingEntity, eMailData, Arrays.asList(reportingEntity.getInitiator()));
			}
		} catch (Exception e) {
			LOGGER.warn("WkC - Failed to send mail for " + executionEntity.getProcessDefinitionKey(), e);
		}
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
			String roleName, String subject, String body) {
		LOGGER.debug("computePotentialOwners...");
		EMailData eMailData = new EMailData(subject, body);
		return computePotentialOwners(scriptContext, executionEntity, roleName, eMailData);
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
			String roleName, EMailData eMailData) {
		LOGGER.debug("computePotentialOwners...");
		List<String> assignees = null;
		AbstractReportingEntity reportingEntity = lookupReportingEntity(executionEntity);
		if (reportingEntity != null) {
			assignees = assignmentHelper.computeAssignees(reportingEntity, roleName);
			try {
				// Ici il faut calcule le contenue de recipients
				sendEMail(executionEntity, reportingEntity, eMailData, assignees);
			} catch (Exception e) {
				LOGGER.warn("Failed to send email to " + assignees + " from " + reportingEntity, e);
			}
		}
		return assignees;
	}

	/**
	 * Retourne la liste des users candidats pour la tâche
	 * 
	 * @param scriptContext   le context
	 * @param executionEntity l'entité d'execution
	 * @param roleName        le rôle rechercher
	 * @return la liste des users par leur identifiant sec-username
	 */
	public String computeHumanPerformer(ScriptContext scriptContext, ExecutionEntity executionEntity, String roleName,
			EMailData eMailData) {
		LOGGER.debug("computeHumanPerformer...");
		String assignee = null;
		AbstractReportingEntity reportingEntity = lookupReportingEntity(executionEntity);
		if (reportingEntity != null) {
			assignee = assignmentHelper.computeAssignee(reportingEntity, roleName);
			try {
				// Ici il faut calcule le contenue de result
				sendEMail(executionEntity, reportingEntity, eMailData, Arrays.asList(assignee));
			} catch (Exception e) {
				LOGGER.warn("Failed to send email to " + assignee + " from " + reportingEntity, e);
			}
		}
		return assignee;
	}

	public String computeHumanPerformer(ScriptContext scriptContext, ExecutionEntity executionEntity, String roleName,
			String subject, String body) {
		LOGGER.debug("computeHumanPerformer...");
		EMailData eMailData = new EMailData(subject, body);
		return computeHumanPerformer(scriptContext, executionEntity, roleName, eMailData);
	}

	private AbstractReportingEntity lookupReportingEntity(ExecutionEntity executionEntity) {
		AbstractReportingEntity result = null;
		String processInstanceBusinessKey = executionEntity.getProcessInstanceBusinessKey();
		if (processInstanceBusinessKey != null) {
			UUID uuid = UUID.fromString(processInstanceBusinessKey);
			result = reportingDao.findByUuid(uuid);
		}
		if (result == null) {
			LOGGER.warn("WkC - No target for {}", processInstanceBusinessKey);
		}
		return result;
	}

	private void sendEMail(ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity,
			EMailData eMailData, List<String> recipients)
			throws EMailException, IOException, DocumentModelNotFoundException, DocumentGenerationException {
		if (eMailData != null && CollectionUtils.isNotEmpty(recipients)) {
			for (String recipient : recipients) {
				User user = userService.getUserByLogin(recipient);
				if (user != null) {
					MailDescription mailDescription = new MailDescription();
					mailDescription.setSubject(generateSubject(eMailData));
					mailDescription.addTo(user.getEmail());
					mailDescription.setHtml(true);
					mailDescription.setBody(generateEMailBody(executionEntity, reportingEntity, eMailData));
					mailService.sendMail(mailDescription);
				}
			}
		}
	}

	private String generateSubject(EMailData eMailData) {
		String subject = "No subject";
		if (StringUtils.isNotEmpty(eMailData.getSubject())) {
			subject = eMailData.getSubject();
		}
		return subject;
	}

	private DocumentContent generateEMailBody(ExecutionEntity executionEntity, AbstractReportingEntity reportingEntity,
			EMailData eMailData) throws IOException, DocumentModelNotFoundException, DocumentGenerationException {
		EmailDataModel emailDataModel = null;
		if (StringUtils.isNotEmpty(eMailData.getBody())) {
			emailDataModel = new EmailDataModel(userService, executionEntity, reportingEntity,
					GenerationConnectorConstants.STRING_TEMPLATE_LOADER_PREFIX + reportingEntity.getUuid().toString() + ":"
							+ eMailData.getBody());
		} else {
			emailDataModel = new EmailDataModel(userService, executionEntity, reportingEntity, eMailData.getFileBody());
		}

		return generationConnector.generateDocument(emailDataModel);
	}
}
