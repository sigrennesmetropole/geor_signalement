/**
 * 
 */
package org.georchestra.signalement.service.helper.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class WorkflowContext {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowContext.class);

	public void updateStatus(Object a, Object b) {
		LOGGER.debug("Update status...");

	}

	public void sendEMail(Object a, Object b) {
		LOGGER.debug("Send email...");
	}
}
