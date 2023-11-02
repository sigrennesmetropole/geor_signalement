/**
 * 
 */
package org.georchestra.signalement.service.st.mail;

import org.georchestra.signalement.service.exception.EMailException;

/**
 * @author FNI18300
 *
 */
public interface MailService {

	void sendMail(MailDescription mailDescription) throws EMailException;
}
