/**
 * 
 */
package org.georchestra.signalement.api.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FNI18300
 *
 */
public class HookEventListener implements ActivitiEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(HookEventListener.class);

	@Override
	public void onEvent(ActivitiEvent event) {
		switch (event.getType()) {

		case JOB_EXECUTION_SUCCESS:
			LOGGER.info("A job well done! " + event);
			break;

		case JOB_EXECUTION_FAILURE:
			LOGGER.info("A job has failed... " + event);
			break;

		default:
			LOGGER.info("Event received: " + event.getType() + "=> " + event);
		}
	}

	@Override
	public boolean isFailOnException() {
		return false;
	}

}
