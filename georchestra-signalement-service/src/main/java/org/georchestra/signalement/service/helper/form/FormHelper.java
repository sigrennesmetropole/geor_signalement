/**
 * 
 */
package org.georchestra.signalement.service.helper.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.runtime.ProcessInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.acl.ContextDescriptionDao;
import org.georchestra.signalement.core.dao.form.ProcessFormDefinitionCustomDao;
import org.georchestra.signalement.core.dto.ContextDescription;
import org.georchestra.signalement.core.dto.Field;
import org.georchestra.signalement.core.dto.FieldDefinition;
import org.georchestra.signalement.core.dto.FieldType;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.ProcessFormDefinitionSearchCriteria;
import org.georchestra.signalement.core.dto.Section;
import org.georchestra.signalement.core.dto.SortCriteria;
import org.georchestra.signalement.core.dto.SortCriterion;
import org.georchestra.signalement.core.entity.acl.ContextDescriptionEntity;
import org.georchestra.signalement.core.entity.form.ProcessFormDefinitionEntity;
import org.georchestra.signalement.service.exception.FormConvertException;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.georchestra.signalement.service.helper.workflow.BpmnHelper;
import org.georchestra.signalement.service.mapper.form.FormMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author FNI18300
 *
 */
@Component
public class FormHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(FormHelper.class);

	private static final String DEFAULT_DATA_FORMAT = "dd/MM/yyyy";

	private static final String DRAFT_USER_TASK_ID = "draft";

	@Autowired
	private FormMapper formMapper;

	@Autowired
	private BpmnHelper bpmnHelper;

	@Autowired
	private ProcessFormDefinitionCustomDao processFormDefinitionCustomDao;

	@Autowired
	private ContextDescriptionDao contextDescriptionDao;

	/**
	 * Retourne le formulaire le plus adapté à la tâche
	 * 
	 * Lors de la recherche on ramène tous les formulaires pour le workflow, la
	 * révision courante ou null, la tâche utilisateur ou null ce qui permet d'avoir
	 * des formulaires génériques pour toutes les révisions ou pour toutes les
	 * tâches etc.
	 * 
	 * @param input
	 * @return
	 * @throws FormDefinitionException
	 */
	public Form lookupForm(org.activiti.engine.task.Task input) throws FormDefinitionException {
		Form result = null;
		ProcessFormDefinitionSearchCriteria searchCriteria = createSearchCriteria(input);
		SortCriteria sortCriteria = createSortCriteria();
		List<ProcessFormDefinitionEntity> processFormDefinitionEntities = processFormDefinitionCustomDao
				.searchProcessFormDefintions(searchCriteria, sortCriteria);
		if (CollectionUtils.isNotEmpty(processFormDefinitionEntities)) {
			ProcessFormDefinitionEntity processFormDefinitionEntity = processFormDefinitionEntities.get(0);
			try {
				result = formMapper.entityToDto(processFormDefinitionEntity.getFormDefinition());
			} catch (FormDefinitionException e) {
				LOGGER.warn("Failed to set form for task:" + input.getId(), e);
			}
		}
		return result;
	}
	
	public Form lookupDraftForm(ContextDescription contextDescription) throws FormDefinitionException {
		return lookupDraftForm(contextDescription.getName());
	}

	public Form lookupDraftForm(String contextDescriptionName) throws FormDefinitionException {
		Form result = null;
		ProcessFormDefinitionSearchCriteria searchCriteria = createSearchCriteria(contextDescriptionName);
		SortCriteria sortCriteria = createSortCriteria();
		List<ProcessFormDefinitionEntity> processFormDefinitionEntities = processFormDefinitionCustomDao
				.searchProcessFormDefintions(searchCriteria, sortCriteria);
		if (CollectionUtils.isNotEmpty(processFormDefinitionEntities)) {
			ProcessFormDefinitionEntity processFormDefinitionEntity = processFormDefinitionEntities.get(0);
			try {
				result = formMapper.entityToDto(processFormDefinitionEntity.getFormDefinition());
			} catch (FormDefinitionException e) {
				LOGGER.warn("Failed to set form for draft task", e);
			}
		}
		return result;
	}

	/**
	 * Copie les données d'un formulaire source dans une cible. Le formulaire cible
	 * est considéré comme la référence. Ce qui signifie que seul les champs
	 * présents dans la cible sont copiés et les autres sont ingorés
	 * 
	 * @param source
	 * @param target
	 */
	public void copyFormData(Form source, Form target) {
		if (source != null && target != null && CollectionUtils.isNotEmpty(target.getSections())) {
			for (Section section : target.getSections()) {
				copySectionData(source, section);
			}
		}
	}

	private void copySectionData(Form source, Section section) {
		if (CollectionUtils.isNotEmpty(section.getFields())) {
			for (Field targetField : section.getFields()) {
				Field sourceField = lookupField(source, targetField.getDefinition().getName());
				if (sourceField != null) {
					targetField.setValues(sourceField.getValues());
				}
			}
		}
	}

	/**
	 * Retourne un champ depuis un form par son nom
	 * 
	 * @param form
	 * @param name
	 * @return
	 */
	public Field lookupField(Form form, String name) {
		Field result = null;
		if (form != null && CollectionUtils.isNotEmpty(form.getSections())) {
			for (Section section : form.getSections()) {
				result = lookupField(section, name);
			}
		}
		return result;
	}

	private Field lookupField(Section section, String name) {
		Field result = null;
		if (CollectionUtils.isNotEmpty(section.getFields())) {
			for (Field field : section.getFields()) {
				if (field.getDefinition().getName().equals(name)) {
					result = field;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param form
	 * @return la map des champs existants
	 */
	public Map<String, FieldDefinition> buildFieldDefinition(Form form) {
		Map<String, FieldDefinition> result = new HashMap<>();
		if (form != null && CollectionUtils.isNotEmpty(form.getSections())) {
			for (Section section : form.getSections()) {
				if (CollectionUtils.isNotEmpty(section.getFields())) {
					for (Field field : section.getFields()) {
						result.put(field.getDefinition().getName(), field.getDefinition());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Rempli le formulaire à partir de la map des données
	 * 
	 * @param form
	 * @param datas
	 */
	public void fillForm(Form form, Map<String, Object> datas) {
		for (Section section : form.getSections()) {
			if (CollectionUtils.isNotEmpty(section.getFields())) {
				for (Field field : section.getFields()) {
					Object value = datas.get(field.getDefinition().getName());
					fillField(field, value);
				}
			}
		}
	}

	private void fillField(Field field, Object value) {
		if (value != null) {
			if (FieldType.LIST == field.getDefinition().getType() && field.getDefinition().isMultiple()) {
				fillFieldList(field, value);
			} else {
				field.addValuesItem(value.toString());
			}
		}
	}

	private void fillFieldList(Field field, Object value) {
		if (value instanceof Collection) {
			for (Object itemValue : ((Collection<?>) value)) {
				field.addValuesItem(itemValue.toString());
			}
		} else {
			field.addValuesItem(value.toString());
		}
	}

	/**
	 * Rempli la map à partir du formulaire
	 * 
	 * @param form
	 * @param datas
	 * @throws FormConvertException
	 */
	public void fillMap(Form form, Map<String, Object> datas) throws FormConvertException {
		for (Section section : form.getSections()) {
			if (CollectionUtils.isNotEmpty(section.getFields())) {
				for (Field field : section.getFields()) {
					fillMap(field.getDefinition(), datas, field.getValues());
				}
			}
		}
	}

	private void fillMap(FieldDefinition fieldDefinition, Map<String, Object> datas, List<String> values)
			throws FormConvertException {
		if (CollectionUtils.isNotEmpty(values)) {
			if (FieldType.LIST == fieldDefinition.getType() && fieldDefinition.isMultiple()) {
				datas.put(fieldDefinition.getName(), values);
			} else {
				// il n'y a qu'une valeur car on gère le multiple que pour les listes à choix
				String value = values.get(0);
				Object convertedValue = convertValue(fieldDefinition, value);
				datas.put(fieldDefinition.getName(), convertedValue);
			}
		}
	}

	private Object convertValue(FieldDefinition fieldDefinition, String value) throws FormConvertException {
		Object result = null;
		try {
			switch (fieldDefinition.getType()) {
			case BOOLEAN:
				result = Boolean.valueOf(value);
				break;
			case DATE:
				String pattern = StringUtils.isNotEmpty(fieldDefinition.getExtendedType())
						? fieldDefinition.getExtendedType()
						: DEFAULT_DATA_FORMAT;
				SimpleDateFormat dataFormat = new SimpleDateFormat(pattern);
				result = dataFormat.parse(value);

				break;
			case DOUBLE:
				result = Double.valueOf(value);
				break;
			case LONG:
				result = Long.valueOf(value);
				break;
			default:
				result = value;
				break;
			}
		} catch (ParseException e) {
			throw new FormConvertException("Failed to convert value:" + value + " for field:" + fieldDefinition, e);
		}
		return result;
	}

	private ProcessFormDefinitionSearchCriteria createSearchCriteria(org.activiti.engine.task.Task input) {
		ProcessInstance processInstance = bpmnHelper.lookupProcessInstance(input);
		UserTask userTask = bpmnHelper.lookupUserTask(input);
		ProcessFormDefinitionSearchCriteria searchCriteria = new ProcessFormDefinitionSearchCriteria(
				processInstance.getProcessDefinitionKey(), processInstance.getProcessDefinitionVersion(), true,
				userTask.getId(), true);
		return searchCriteria;
	}

	private ProcessFormDefinitionSearchCriteria createSearchCriteria(String contextDescriptionName) {
		ContextDescriptionEntity contextDescriptionEntity = contextDescriptionDao
				.findByName(contextDescriptionName);
		if (contextDescriptionEntity == null) {
			throw new IllegalArgumentException("Invalid context name:" + contextDescriptionName);
		}
		String processInstanceId = bpmnHelper.lookupProcessInstanceBusinessKey(contextDescriptionEntity);
		ProcessFormDefinitionSearchCriteria searchCriteria = new ProcessFormDefinitionSearchCriteria(processInstanceId,
				null, true, DRAFT_USER_TASK_ID, true);
		return searchCriteria;
	}

	private SortCriteria createSortCriteria() {
		SortCriteria sortCriteria = new SortCriteria();
		sortCriteria.addElementsItem(createAscSortCriterion("revision"));
		sortCriteria.addElementsItem(createAscSortCriterion("userTaskId"));
		return sortCriteria;
	}

	private SortCriterion createAscSortCriterion(String property) {
		SortCriterion sortCriterion = new SortCriterion();
		sortCriterion.setProperty(property);
		sortCriterion.asc(true);
		return sortCriterion;
	}

}
