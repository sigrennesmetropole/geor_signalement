/**
 * 
 */
package org.georchestra.signalement.core.entity.ged;

import java.sql.Blob;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
