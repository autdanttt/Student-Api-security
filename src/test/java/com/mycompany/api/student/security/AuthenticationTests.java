package com.mycompany.api.student.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest
public class AuthenticationTests {

	@Autowired AuthenticationManager authManager;
	
	@Test
	public void testAuthenticationFail() {
		assertThrows(BadCredentialsException.class, () -> {
			authManager.authenticate(new UsernamePasswordAuthenticationToken("namhm", "xxxx"));
		});
	}
	
	
	@Test
	public void testAuthenticationSuccess() {
		String username = "namhm";
		String password = "mhman";
		
		Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		
		assertThat(authentication.isAuthenticated()).isTrue();
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getAuthorities();
		
		assertThat(userDetails.getUsername()).isEqualTo(username);
	}
}
