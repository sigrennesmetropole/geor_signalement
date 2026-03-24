/**
 *
 */
package org.georchestra.signalement.api.config.scheduler;

import org.georchestra.signalement.service.sm.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler chargé de supprimer périodiquement les tâches au statut DRAFT
 * qui dépassent une durée de vie configurable (par défaut 24 heures).
 */
@Component
public class DraftTaskCleanupScheduler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DraftTaskCleanupScheduler.class);

	/**
	 * Durée d'expiration des drafts en heures (par défaut : 24h).
	 * Configurable via la propriété {@code signalement.draft.expiration.hours}.
	 */
	@Value("${signalement.draft.expiration.hours:24}")
	private int draftExpirationHours;

	private final TaskService taskService;

	public DraftTaskCleanupScheduler(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * Tâche planifiée de nettoyage des drafts expirés.
	 * <p>
	 * Par défaut, s'exécute toutes les heures. Le cron est configurable via
	 * la propriété {@code signalement.draft.cleanup.cron}.
	 * </p>
	 */
	@Scheduled(cron = "${signalement.draft.cleanup.cron:0 0 * * * *}")
	public void cleanupExpiredDrafts() {
		LOGGER.info("Starting cleanup of draft tasks older than {} hours...", draftExpirationHours);
		try {
			int deleted = taskService.deleteDraftTasksOlderThan(draftExpirationHours);
			LOGGER.info("Cleanup completed: {} draft task(s) deleted.", deleted);
		} catch (Exception e) {
			LOGGER.error("Error during draft task cleanup", e);
		}
	}

}

