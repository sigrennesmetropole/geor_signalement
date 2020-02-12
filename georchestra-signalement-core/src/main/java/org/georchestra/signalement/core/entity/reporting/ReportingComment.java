/**
 * 
 */
package org.georchestra.signalement.core.entity.reporting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.georchestra.signalement.core.common.LongId;

import lombok.Data;

/**
 * @author FNI18300
 *
 */

@Data
@Entity
@Table(name = "reporting_comment")
public class ReportingComment implements LongId {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "author", length = 100, nullable = false)
	private String author;

	@Column(name = "creation_date", nullable = false)
	private Date creationDate;

	@Column(name = "comment", length = 1024)
	private String comment;

}
