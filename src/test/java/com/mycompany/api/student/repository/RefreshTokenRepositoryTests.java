package com.mycompany.api.student.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import jakarta.persistence.Query;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RefreshTokenRepositoryTests {
	
	@Autowired RefreshTokenRepository repo;
	@Autowired TestEntityManager testEntityManager;
	
	@Test
	public void testFindByUsernameNotFound() {
		String usernameNotExist = "dafsdf";
		List<RefreshToken> findResult = repo.findByUsername(usernameNotExist);
		
		assertThat(findResult).isEmpty();
	}
	
	@Test
	public void testFindByUsernameFound() {
		String usernameNotExist = "admin";
		List<RefreshToken> findResult = repo.findByUsername(usernameNotExist);
		
		assertThat(findResult).isNotEmpty();
	}
	
	
	@Test
	public void testDeleteByExpireTime() {
		String 	jpql = "SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.expiryTime <= CURRENT_TIME";
		
		Query query = testEntityManager.getEntityManager().createQuery(jpql);
		
		Long numberOfExpiredRefreshTokens = (Long) query.getSingleResult();
		System.out.println(numberOfExpiredRefreshTokens);
		int rowDeleted = repo.deleteByExpiryTime();
		System.out.println("row deleted " + rowDeleted);
		assertEquals(numberOfExpiredRefreshTokens, rowDeleted);
	}
	
	
	

}









