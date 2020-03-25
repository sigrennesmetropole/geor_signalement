/**
 * 
 */
package org.georchestra.signalement.core.dto;

/**
 * @author FNI18300
 *
 */
public class EMailData {

	private String subject;
	
	private String body;
	
	private String fileBody;

	public EMailData() {
		super();
	}

	public EMailData(String subject, String body, String fileBody) {
		super();
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
