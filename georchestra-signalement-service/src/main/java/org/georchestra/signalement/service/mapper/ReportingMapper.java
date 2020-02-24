/**
 * 
 */
package org.georchestra.signalement.service.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dao.ged.AttachmentDao;
import org.georchestra.signalement.core.dto.Attachment;
import org.georchestra.signalement.core.dto.ReportingDescription;
import org.georchestra.signalement.core.entity.ged.AttachmentEntity;
import org.georchestra.signalement.core.entity.reporting.AbstractReportingEntity;
import org.georchestra.signalement.service.helper.reporting.ReportingHelper;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
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
	private AttachmentDao attachmentDao;

	@Autowired
	private ReportingHelper reportingHelper;

	/**
	 * @param dto dto to transform to entity
	 * @return entity
	 */
	public abstract ReportingDescription entityToDto(AbstractReportingEntity abstractReportingEntity);

	@AfterMapping
	public void afterMapping(AbstractReportingEntity abstractReportingEntity,
			@MappingTarget ReportingDescription reportingDescription) {
		if (abstractReportingEntity.getId() != null) {
			List<AttachmentEntity> attachmentEntities = attachmentDao
					.findByAttachmentIds(abstractReportingEntity.getId().toString());
			if (CollectionUtils.isNotEmpty(attachmentEntities)) {
				List<Attachment> attachments = new ArrayList<>();
				for (AttachmentEntity attachmentEntity : attachmentEntities) {
					Attachment attachment = new Attachment();
					attachment.setId(attachmentEntity.getId());
					attachment.setName(attachmentEntity.getName());
					attachment.setMimeType(attachmentEntity.getMimeType());
					attachments.add(attachment);
				}
			}
		}
		if (StringUtils.isNotEmpty(abstractReportingEntity.getDatas())) {
			try {
				Map<String, Object> datas = reportingHelper.hydrateData(abstractReportingEntity.getDatas());
				reportingDescription.setDatas(datas);
			} catch (Exception e) {
				LOGGER.warn("Failed to parse data:" + abstractReportingEntity.getDatas(), e);
			}
		}

	}
}
