package com.imam.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

	@Bean
	UserDetailsManager userDetailsManager(DataSource dataSource) {
		System.out.println("Inside userDetailsManager");
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		jdbcUserDetailsManager
				.setUsersByUsernameQuery("SELECT email, password, true AS enabled FROM users WHERE email = ?");
		jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT name, role FROM users WHERE email = ?");
		System.out.println(jdbcUserDetailsManager);
		return jdbcUserDetailsManager;
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				.requestMatchers("/","/posts", "/posts/{id}", "/posts/search", "/posts/filter", "/posts/sort",
						"/comments/add/{postId}", "/signUp", "/register")
				.permitAll()
				.requestMatchers("/posts/create", "/posts/update/{id}", "/posts/delete/{id}").hasAnyRole("AUTHOR", "ADMIN")
				.requestMatchers("/comments/update/{id}", "/comments/delete/{id}").hasAnyRole("AUTHOR", "ADMIN")
				.anyRequest()
				.authenticated())
				.formLogin(formLogin -> formLogin.loginPage("/login").loginProcessingUrl("/authenticateTheUser")
						.defaultSuccessUrl("/posts", true).permitAll())
				.logout(logout -> logout.logoutUrl("/logout").permitAll())
				.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

		return http.build();
	}
}
