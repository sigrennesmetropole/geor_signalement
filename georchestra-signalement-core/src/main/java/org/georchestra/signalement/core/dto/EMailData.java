/**
 * 
 */
package org.georchestra.signalement.core.dto;

/**
 * @author FNI18300
 *
 */
public class EMailData {

	public static final String FILE_PREFIX = "file:";

	private String subject;

	private String body;

	private String fileBody;

	public EMailData() {
	}

	public EMailData(String subject, String body) {
		this.subject = subject;
		if (body != null && body.startsWith(FILE_PREFIX)) {
			this.fileBody = body.substring(FILE_PREFIX.length());
		} else {
			this.body = body;
		}
	}

	public EMailData(String subject, String body, String fileBody) {
		this.subject = subject;
		this.body = body;
		this.fileBody = fileBody;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFileBody() {
		return fileBody;
	}

	public void setFileBody(String fileBody) {
		this.fileBody = fileBody;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EMailData [subject=");
		builder.append(subject);
		builder.append(", body=");
		builder.append(body);
		builder.append(", fileBody=");
		builder.append(fileBody);
		builder.append("]");
		return builder.toString();
	}

}
