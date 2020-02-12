/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import java.sql.Blob;

import javax.persistence.Column;
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

	@Lob
	@Column(name="content")
	private Blob content;

}
