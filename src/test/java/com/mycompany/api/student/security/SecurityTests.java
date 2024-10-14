package com.mycompany.api.student.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.api.student.auth.AuthRequest;
import com.mycompany.api.student.auth.AuthResponse;
import com.mycompany.api.student.auth.RefreshTokenRequest;
import com.mycompany.api.student.repository.Student;

@SpringBootTest	
@AutoConfigureMockMvc
public class SecurityTests {

	private static final String GET_ACCESS_TOKEN_ENDPOINT = "/api/oauth/token";
	private static final String LIST_STUDENT_ENDPOINT = "/api/students?pageSize=10&pageNum=1";
	private static final String REFRESH_TOKEN_ENDPOINT = "/api/oauth/token/refresh";
	
	@Autowired MockMvc mockMvc;
	@Autowired ObjectMapper objectMapper;
	
	@Test
	public void getBaseUriShouldReturn401() throws Exception {
		mockMvc.perform(get("/"))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testGetAccessTokenBadRequest() throws Exception {
		AuthRequest request = new AuthRequest();
		request.setUsername("namddfh");
		request.setPassword("sdfsdfs");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.contentType("application/json").content(requestBody))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testGetAccessTokenFail() throws Exception {
		AuthRequest request = new AuthRequest();
		request.setUsername("namddfh");
		request.setPassword("sdfsdfs");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.contentType("application/json").content(requestBody))
				.andDo(print())
				.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testGetAccessTokenSuccess() throws Exception {
		AuthRequest request = new AuthRequest();
		request.setUsername("tuandat");
		request.setPassword("dattuan");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.contentType("application/json").content(requestBody))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty());
	}
	
	
	@Test
	public void testGetListStudentFail() throws Exception {
		mockMvc.perform(get(LIST_STUDENT_ENDPOINT).
				header("Authorization", "Bearer somethinginvalid"))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.errors[0]").isNotEmpty());
	}
	
	@Test
	public void testGetListStudentSuccess() throws Exception {
		AuthRequest request = new AuthRequest();
		request.setUsername("tuandat");
		request.setPassword("dattuan");
		
		String requestBody = objectMapper.writeValueAsString(request);
		MvcResult mvcResult = mockMvc.perform(post(GET_ACCESS_TOKEN_ENDPOINT)
				.contentType("application/json").content(requestBody))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").isNotEmpty())
				.andExpect(jsonPath("$.refreshToken").isNotEmpty())
				.andReturn();
		
		String responseBody = mvcResult.getResponse().getContentAsString();
		
		AuthResponse response = objectMapper.readValue(responseBody, AuthResponse.class);
		
		String bearerToken  = "Bearer " + response.getAccessToken();
		System.out.println("bearToken: " + bearerToken);
		mockMvc.perform(get(LIST_STUDENT_ENDPOINT)
				.header("Authorization", bearerToken))
				.andDo(print())
				.andExpect(status().isOk())
//				.andExpect(jsonPath("$.errors[0]").isEmpty())
				;
		
	}
	
	@Test
	public void testAddStudent1() throws Exception {
		String apiEndPoint = "/api/students";
		
		Student student = new Student();
		student.setName("Ravi Kuma");
		
		String requestBody = objectMapper.writeValueAsString(student);
		mockMvc.perform(post(apiEndPoint).contentType("application/json").content(requestBody)
				.with(jwt().authorities(new SimpleGrantedAuthority("write"))))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.name").isString());
	}
	
	@Test
	public void testDeleteStudent() throws Exception {
		Integer studentId = 2;
		String apiEndPoint = "/api/students/" +studentId ;
		
		mockMvc.perform(delete(apiEndPoint).with(jwt().authorities(new SimpleGrantedAuthority("write"))))
			.andDo(print())
			.andExpect(status().isNoContent());
	}
	
	@Test
	public void testRefreshTokenBadRequest() throws Exception {
		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setUsername("adf");
		request.setRefreshToken("dfsadfsf");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestBody))
		.andDo(print())
		.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testRefreshTokenFail() throws Exception {
		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setUsername("admin");
		request.setRefreshToken("abcdefghijkmnlopqrstuvwxyxabcdefghijkmnlopqrst");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestBody))
		.andDo(print())
		.andExpect(status().isUnauthorized());
	}
	
	@Test
	public void testRefreshTokenSuccess() throws Exception {
		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setUsername("tuandat");
		request.setRefreshToken("cdaeda04-adb9-4c9f-ade4-0d9b2695f8b3");
		
		String requestBody = objectMapper.writeValueAsString(request);
		
		mockMvc.perform(post(REFRESH_TOKEN_ENDPOINT).contentType("application/json").content(requestBody))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.accessToken").isNotEmpty())
		.andExpect(jsonPath("$.refreshToken").isNotEmpty());
	}
}











