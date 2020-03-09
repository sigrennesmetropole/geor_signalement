/**
 * 
 */
package org.georchestra.signalement.api.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.BaseEntityEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author FNI18300
 *
 */
public class HookBaseEntityEventListener extends BaseEntityEventListener {


	private static final Logger LOGGER = LoggerFactory.getLogger(HookBaseEntityEventListener.class);

	
	@Override
	protected void onCreate(ActivitiEvent event) {
		LOGGER.debug("Create:{}",event);
	}

	@Override
	protected void onInitialized(ActivitiEvent event) {
		LOGGER.debug("Initialize:{}", event );
	}

	@Override
	protected void onDelete(ActivitiEvent event) {
		LOGGER.debug("Delete:{}",event);
	}

	@Override
	protected void onUpdate(ActivitiEvent event) {
		LOGGER.debug("Update:{}",event);
	}

	@Override
	protected void onEntityEvent(ActivitiEvent event) {
		LOGGER.debug("Entity:{}",event);
	}

}
