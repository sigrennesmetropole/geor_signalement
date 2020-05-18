/**
 * 
 */
package org.georchestra.signalement.service.mapper.form;

import org.apache.commons.collections4.CollectionUtils;
import org.georchestra.signalement.core.dto.Form;
import org.georchestra.signalement.core.dto.Section;
import org.georchestra.signalement.core.entity.form.FormDefinitionEntity;
import org.georchestra.signalement.core.entity.form.FormSectionDefinitionEntity;
import org.georchestra.signalement.service.exception.FormDefinitionException;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class FormMapper {

	@Autowired
	private SectionMapper sectionMapper;

	/**
	 * 
	 * @param formDefinitionEntity
	 * @return
	 * @throws FormDefinitionException
	 */
	public abstract Form entityToDto(FormDefinitionEntity formDefinitionEntity) throws FormDefinitionException;

	/**
	 * 
	 * @param formDefinitionEntity
	 * @param form
	 * @throws FormDefinitionException
	 */
	@AfterMapping
	public void afterMapping(FormDefinitionEntity formDefinitionEntity, @MappingTarget Form form)
			throws FormDefinitionException {
		if (formDefinitionEntity != null
				&& CollectionUtils.isNotEmpty(formDefinitionEntity.getFormSectionDefinitions())) {
			for (FormSectionDefinitionEntity formSectionDefinitionEntity : formDefinitionEntity
					.getFormSectionDefinitions()) {
				Section section = sectionMapper.entityToDto(formSectionDefinitionEntity.getSectionDefinition());
				section.setReadOnly(formSectionDefinitionEntity.isReadOnly());
				form.addSectionsItem(section);
			}
		}
	}
}
