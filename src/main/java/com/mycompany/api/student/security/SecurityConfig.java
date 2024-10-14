package com.mycompany.api.student.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import com.mycompany.api.student.security.jwt.JwtTokenFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

	@Autowired JwtTokenFilter jwtFilter;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}
	
	
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setPasswordEncoder(passwordEncoder());
		authProvider.setUserDetailsService(userDetailsService());
		
		return authProvider;
	}
	
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				auth -> auth.requestMatchers("/api/oauth/**").permitAll()
					.requestMatchers(HttpMethod.GET, "/api/students").hasAnyAuthority("write", "read")
					.requestMatchers(HttpMethod.POST, "/api/students").hasAuthority("write")
					.requestMatchers(HttpMethod.PUT, "/api/students").hasAuthority("write")
					.anyRequest().authenticated()
			)
			.csrf(csrf -> csrf.disable())
			.exceptionHandling(exh -> exh.authenticationEntryPoint(
					(request, response, exception) -> {
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
					}))
			.addFilterBefore(jwtFilter, AuthorizationFilter.class)
			; 
		
		return http.build();
	}
}










