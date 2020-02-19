/**
 * 
 */
package org.georchestra.signalement.core.entity.ged;

import java.sql.Blob;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * @author FNI18300
 *
 */
@Data
@Entity
@Table(name = "attachment")
public class AttachmentEntity implements LongId {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "mime_type", length = 100)
	private String mimeType;
	
	@Column(name = "name", length = 150)
	private String name;
	
	@Column(name="attachment_ids")
	@ElementCollection(targetClass=String.class)
	private List<String> attachmentIds;

	@Lob
	@Column(name="content")
	private Blob content;

}
