package com.mycompany.api.student;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.mycompany.api.student.repository.User;
import com.mycompany.api.student.security.jwt.JwtUtility;
import com.mycompany.api.student.security.jwt.JwtValidationException;

public class JwtUtilityTests {

	private static JwtUtility jwtUtil;
	
	@BeforeAll
	static void setup() {
		jwtUtil = new JwtUtility();
		jwtUtil.setIssuerName("My Company");
		jwtUtil.setSecretKey("ABCDEFJHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxwyz!@#$%^&*()");
		jwtUtil.setAccessTokenExpiration(2);
	}
	
	@Test
	public void testGenerateFail() {
		assertThrows(IllegalArgumentException.class, new org.junit.jupiter.api.function.Executable() {
			
			@Override
			public void execute() throws Throwable {
				User user = null;
				jwtUtil.generateAccessToken(user);
			}
		});
	}
	
	@Test
	public void testGenerateSuccess() {
		User user = new User();
		user.setId(3);
		user.setUsername("jonedoe");
		user.setPassword("read");
		user.setRole("read");
		
		String token = jwtUtil.generateAccessToken(user);
		assertThat(token).isNotNull();
		System.out.println(token);
	}
	
	@Test
	public void testValidateFail() {
		assertThrows(JwtValidationException.class, () -> {
			jwtUtil.validateAccessToken("a.b.c");
		});
	}
	
	@Test
	public void testValidateSuccess() {
		User user = new User();
		user.setId(3);
		user.setUsername("jonedoe");
		user.setPassword("read");
		user.setRole("read");
		
		String token = jwtUtil.generateAccessToken(user);
		
		assertThat(token).isNotNull();
		
		assertDoesNotThrow(() -> {
			jwtUtil.validateAccessToken(token);
		});
	}
}
