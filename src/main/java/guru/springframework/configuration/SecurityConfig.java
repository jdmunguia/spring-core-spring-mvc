package guru.springframework.configuration;

import guru.springframework.controllers.MySimpleUrlAuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;

/**
 * <p>
 * This component and its source code representation are copyright protected and
 * proprietary to Trivera Technologies, Inc., Worldwide
 *
 * This component and source code may be used for instructional and evaluation
 * purposes only. No part of this component or its source code may be sold,
 * transferred, or publicly posted, nor may it be used in a commercial or
 * production environment, without the express written consent of the Trivera
 * Technologies, Inc.
 *
 * Copyright (c) 2017 Trivera Technologies, Inc. http://www.triveratech.com
 * </p>
 * 
 * @author The Trivera Tech Team.
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	@Autowired
	MySimpleUrlAuthenticationSuccessHandler mySimpleUrlAuthenticationSuccessHandler;
	/**
	 * Configure AuthenticationManager with in-memory based credentials. Uses
	 * SHA-256 for HASH password encoder.
	 *
	 * @param auth
	 *            AuthenticationManagerBuilder
	 * @throws Exception
	 *             Authentication exception
	 */
	@Autowired
	public void configureGlobalSecurity(final AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
		auth.inMemoryAuthentication().withUser("user2").password("password").roles("USER");
		auth.inMemoryAuthentication().withUser("michael").password("gold").roles("USER", "MANAGER");
		auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("tomcat").password("tomcat").roles("ADMIN", "SUPER");// dba
																									// have
																									// two
																									// roles.

	}

	/**
	 * HTTP Security configuration
	 *
	 * @param http
	 *            HttpSecurity configuration.
	 * @throws Exception
	 *             Authentication configuration exception
	 *             http://docs.spring.io/spring-security/site/migrate/current/3-
	 *             to-4/html5/migrate-3-to-4-jc.html
	 */
	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
					.antMatchers("/login.html").permitAll()
					.antMatchers("/**").access("hasRole('USER')")
					.antMatchers("/unauthorize.html").permitAll().and()
				.formLogin()
					.loginPage("/login.html")
					.failureUrl("/login-error.html")
					//.successHandler(mySimpleUrlAuthenticationSuccessHandler)
				.and()
				.logout()//.logoutUrl("/logout")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .deleteCookies("JSESSIONID")
				.logoutSuccessUrl("/login.html");

	}

	@Override
	public void configure(final WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Bean
	public RoleVoter roleVoter() {
		return new RoleVoter();
	}

	@Bean
	public AccessDecisionManager accessDecisionManager() {
		UnanimousBased decisionManager = new UnanimousBased(new ArrayList<AccessDecisionVoter<? extends Object>>() {
			{
				add(roleVoter());
				add(new AuthenticatedVoter());
			}
		});
		return decisionManager;
	}

	// ConcurrentSessionFilter support
	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

} // The End...
