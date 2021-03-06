/**
 * 
 */
package org.georchestra.signalement.api.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.georchestra.signalement.core.dto.User;
import org.georchestra.signalement.service.sm.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This filter is inspired from georchestra geowebcache project
 * 
 * @author FNI18300
 *
 */
public class PreAuthenticationFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(PreAuthenticationFilter.class);

	public static final String SEC_USERNAME = "sec-username";
	public static final String SEC_ROLES = "sec-roles";
	public static final String SEC_ORG = "sec-org";
	public static final String SEC_EMAIL = "sec-email";
	public static final String SEC_ORGNAME = "sec-orgname";
	public static final String SEC_TEL = "sec-tel";
	public static final String SEC_PROXY = "sec-proxy";
	public static final String SEC_LASTNAME = "sec-lastname";
	public static final String SEC_FIRSTNAME = "sec-firstname";

	private UserService userService;

	public PreAuthenticationFilter(UserService userService) {
		super();
		this.userService = userService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			if (LOGGER.isInfoEnabled()) {
				Enumeration<String> names = httpServletRequest.getHeaderNames();
				while (names.hasMoreElements()) {
					LOGGER.info("header:{}", names.nextElement());
				}
			}
			final String username = httpServletRequest.getHeader(SEC_USERNAME);
			if (username != null) {
				SecurityContextHolder.getContext().setAuthentication(createAuthentication(httpServletRequest));

				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Populated SecurityContextHolder with pre-auth token: '{}'",
							SecurityContextHolder.getContext().getAuthentication());
				}
			} else {
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("SecurityContextHolder not populated with pre-auth token");
				}
			}
		}

		chain.doFilter(request, response);
	}

	/**
	 * Construction du token pre-authentification
	 * 
	 * @param httpServletRequest
	 * @return
	 */
	private Authentication createAuthentication(HttpServletRequest httpServletRequest) {
		final String username = httpServletRequest.getHeader(SEC_USERNAME);
		final String rolesString = httpServletRequest.getHeader(SEC_ROLES);
		Set<String> rolesSet = new LinkedHashSet<>();
		List<String> roles = null;
		if (rolesString != null) {
			roles = Arrays.asList(rolesString.split(";"));
			rolesSet.addAll(roles);
		}
		try {
			User user = userService.getUserByLogin(username);
			if (user == null) {
				user = new User();
				user.setLogin(username);
				assignUserData(user, httpServletRequest, roles);
				user = userService.createUser(user);
			} else {
				boolean updated = assignUserData(user, httpServletRequest, roles);
				if (updated) {
					user = userService.updateUser(user);
				}
			}
			return new PreAuthenticationToken(username, user, rolesSet);
		} catch (Exception e) {
			LOGGER.debug("Mise à jour ou création d'utilisateur en erreur :{}", e.getMessage());
			User user = new User();
			user.setLogin(username);
			assignUserData(user, httpServletRequest, roles);
			return new PreAuthenticationToken(username, user, rolesSet);
		}
	}

	private boolean assignUserData(User user, HttpServletRequest httpServletRequest, List<String> roles) {
		boolean update = false;
		String email = httpServletRequest.getHeader(SEC_EMAIL);
		if (StringUtils.isNotEmpty(email) && !email.equals(user.getEmail())) {
			user.setEmail(email);
			update = true;
		}
		String firstName = httpServletRequest.getHeader(SEC_FIRSTNAME);
		if (StringUtils.isNotEmpty(firstName) && !firstName.equals(user.getFirstName())) {
			user.setFirstName(firstName);
			update = true;
		}
		String lastName = httpServletRequest.getHeader(SEC_LASTNAME);
		if (StringUtils.isNotEmpty(lastName) && !lastName.equals(user.getLastName())) {
			user.setLastName(lastName);
			update = true;
		}
		String organization = httpServletRequest.getHeader(SEC_ORGNAME);
		if (StringUtils.isNotEmpty(organization) && !organization.equals(user.getOrganization())) {
			user.setOrganization(organization);
			update = true;
		}
		if ((roles != null && !roles.equals(user.getRoles())) || (roles == null && user.getRoles() != null)) {
			user.setRoles(roles);
			update = true;
		}
		return update;
	}

}
