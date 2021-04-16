/**
 * 
 */
package org.georchestra.signalement.service.mapper.reporting;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.georchestra.signalement.service.mapper.acl.ContextDescriptionMapper;
import org.georchestra.signalement.service.st.repository.DocumentRepositoryService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author FNI18300
 *
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
		ContextDescriptionMapper.class })
public abstract class ReportingMapper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportingMapper.class);

	@Autowired
	private DocumentRepositoryService documentRepositoryService;

	@Autowired
	private ReportingHelper reportingHelper;

	/**
	 * @param dto dto to transform to entity
	 * @return entity
	 */
	@Mapping(source = "id", target="functionalId")
	public abstract ReportingDescription entityToDto(AbstractReportingEntity abstractReportingEntity);

	@AfterMapping
	public void afterMapping(AbstractReportingEntity abstractReportingEntity,
			@MappingTarget ReportingDescription reportingDescription) {
		if (abstractReportingEntity.getId() != null) {
			reportingDescription.setAttachments(documentRepositoryService
					.getDocuments(abstractReportingEntity.getUuid().toString()));
		}
		if (StringUtils.isNotEmpty(abstractReportingEntity.getDatas())) {
			try {
				Map<String, Object> datas = reportingHelper.hydrateData(abstractReportingEntity.getDatas());
				reportingDescription.setDatas(datas);
			} catch (Exception e) {
				LOGGER.warn("Failed to parse data:" + abstractReportingEntity.getDatas(), e);
			}
		}
		reportingDescription.setLocalisation(reportingHelper.convertGeometryToCoordinate(abstractReportingEntity.getGeometry(), abstractReportingEntity.getGeographicType()));
	}

	@Mappings({ @Mapping(source = "description", target = "description"), @Mapping(ignore = true, target = "id"),
			@Mapping(ignore = true, target = "uuid"), @Mapping(ignore = true, target = "status"),
			@Mapping(ignore = true, target = "functionalStatus"),
			@Mapping(ignore = true, target = "initiator"), @Mapping(ignore = true, target = "geographicType"),
			@Mapping(ignore = true, target = "creationDate"), @Mapping(ignore = true, target = "updatedDate"),
			@Mapping(ignore = true, target = "contextDescription"), @Mapping(ignore = true, target = "datas") })
	public abstract void updateEntityFromDto(ReportingDescription reportingDescription,
			@MappingTarget AbstractReportingEntity abstractReportingEntity);
}
