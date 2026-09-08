package org.georchestra.signalement.service.sm.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.reporting.ReportingDao;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.dto.Task;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.helper.authentification.AuthentificationHelper;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.georchestra.signalement.service.mapper.reporting.ReportingMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tests unitaires des contrôles d'entrée (gardes nul) et du chemin nominal de
 * createDraft, sans contexte Spring.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

	@Mock
	private ReportingDao reportingDao;

	@Mock
	private ContextDescriptionDao contextDescriptionDao;

	@Mock
	private ReportingHelper reportingHelper;

	@Mock
	private AuthentificationHelper authentificationHelper;

	@Mock
	private ReportingMapper reportingMapper;

	@InjectMocks
	private TaskServiceImpl taskService;

	private static ReportingDescription reportingWithContext(String contextName) {
		return new ReportingDescription().contextDescription(new ContextDescription().name(contextName));
	}

	@Test
	void createDraftNullReporting() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.createDraft(null));
		assertEquals("Reporting with a context is mandatory", ex.getMessage());
	}

	@Test
	void createDraftNullContext() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> taskService.createDraft(new ReportingDescription()));
		assertEquals("Reporting with a context is mandatory", ex.getMessage());
	}

	@Test
	void createDraftUnknownContext() {
		when(contextDescriptionDao.findByName("ctx")).thenReturn(null);
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> taskService.createDraft(reportingWithContext("ctx")));
		assertEquals("Invalid context name:ctx", ex.getMessage());
	}

	@Test
	void createDraftNominal() {
		ReportingDescription input = reportingWithContext("ctx");
		ContextDescriptionEntity context = new ContextDescriptionEntity();
		AbstractReportingEntity entity = org.mockito.Mockito.mock(AbstractReportingEntity.class);
		Task expected = new Task();
		when(contextDescriptionDao.findByName("ctx")).thenReturn(context);
		when(reportingHelper.createReportingEntity(eq(context), any())).thenReturn(entity);
		when(reportingMapper.entityToDto(entity)).thenReturn(input);
		when(reportingHelper.createTaskFromReporting(input)).thenReturn(expected);

		Task result = taskService.createDraft(input);

		assertSame(expected, result);
		verify(reportingDao).save(entity);
	}

	@Test
	void startTaskNullTask() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.startTask(null));
		assertEquals("Task with asset is mandatory", ex.getMessage());
	}

	@Test
	void startTaskNullAsset() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> taskService.startTask(new Task()));
		assertEquals("Task with asset is mandatory", ex.getMessage());
	}

	@Test
	void updateTaskNullTask() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(null));
		assertEquals("Task with asset is mandatory", ex.getMessage());
	}

	@Test
	void updateTaskNullAsset() {
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> taskService.updateTask(new Task()));
		assertEquals("Task with asset is mandatory", ex.getMessage());
	}

	@Test
	void updateTaskDraftNotFound() {
		Task task = new Task().asset(reportingWithContext("ctx"));
		assertNotNull(task.getAsset());
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(task));
		assertEquals("Task does not exist or has a bad status", ex.getMessage());
	}
}
