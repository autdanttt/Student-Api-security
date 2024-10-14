package com.mycompany.api.student.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTests {

	
	@Autowired UserRepository repo;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Test
	public void testAddFirstUser() {
		User user1 = new User();
		user1.setUsername("admin");
		user1.setRole("read");
		
		String rowPassword = "nimda";
		String encodedPassword = passwordEncoder.encode(rowPassword);
		
		user1.setPassword(encodedPassword);
		
		User savedUser = repo.save(user1);
		assertThat(savedUser).isNotNull();
	}
	
	@Test
	public void testAddSecondUser() {
		User user2 = new User();
		user2.setUsername("tuandat");
		user2.setRole("write");
		
		String rowPassword = "dattuan";
		String encodedPassword = passwordEncoder.encode(rowPassword);
		
		user2.setPassword(encodedPassword);
		
		User savedUser = repo.save(user2);
		assertThat(savedUser).isNotNull();
	}
	
	
	@Test
	public void testFindUserNotFound() {
		Optional<User> findByUsername = repo.findByUsername("SDdfsd");
		assertThat(findByUsername).isNotPresent();
	}
	
	@Test
	public void testFindUserFound() {
		String username = "admin";
		Optional<User> findByUsername = repo.findByUsername(username);
		
		User user = findByUsername.get();
		assertThat(user.getUsername()).isEqualTo(username);
	}
}














