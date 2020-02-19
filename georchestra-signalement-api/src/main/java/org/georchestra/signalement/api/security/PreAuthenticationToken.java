/**
 * 
 */
package org.georchestra.signalement.api.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * An authentication that is obtained by reading the credentials from the
 * headers.
 *
 * @see PreAuthenticationFilter
 *
 * @author from Jesse on 4/24/2014.
 */
public class PreAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -3957062197124193685L;
	private final String principal;

	/**
	 * Constructeur
	 * @param username
	 * @param roles
	 */
	public PreAuthenticationToken(String username, Set<String> roles) {
		super(createGrantedAuthorities(roles));
		this.principal = username;

		setAuthenticated(true);
		UserDetails details = new User(username, "", true, true, true, true, super.getAuthorities());
		setDetails(details);

	}

	/**
	 * Construction de la listes de roles
	 * @param roles
	 * @return
	 */
	private static Collection<? extends GrantedAuthority> createGrantedAuthorities(Set<String> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>(roles.size());
		for (String role : roles) {
			authorities.add(new SimpleGrantedAuthority(role));
		}
		return authorities;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}
}
