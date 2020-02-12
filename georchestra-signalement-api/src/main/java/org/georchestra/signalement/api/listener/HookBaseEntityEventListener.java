/**
 * 
 */
package org.georchestra.signalement.api.listener;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.BaseEntityEventListener;

/**
 * @author FNI18300
 *
 */
public class HookBaseEntityEventListener extends BaseEntityEventListener {

	@Override
	protected void onCreate(ActivitiEvent event) {
		System.out.println("Create:" + event);
	}

	@Override
	protected void onInitialized(ActivitiEvent event) {
		System.out.println("Initialize:" + event );
	}

	@Override
	protected void onDelete(ActivitiEvent event) {
		System.out.println("Delete:" + event);
	}

	@Override
	protected void onUpdate(ActivitiEvent event) {
		System.out.println("Update:" + event);
	}

	@Override
	protected void onEntityEvent(ActivitiEvent event) {
		System.out.println("Entity:" + event);
	}

}
