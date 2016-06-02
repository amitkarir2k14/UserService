package com.ibm.internal.assignment.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

//import org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import com.ibm.internal.assignment.entity.User;
import com.ibm.internal.assignment.entity.spec.UserSpec;
import com.ibm.internal.assignment.repository.UserRepository;
import com.ibm.internal.assignment.service.UserService;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@Rollback(true)
@ActiveProfiles("test")
@SpringApplicationConfiguration(classes = { UserService.class })
@WebAppConfiguration
public class UserServiceTest {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private String userName = "amitkarir";

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		this.userRepo.deleteAllInBatch();
		userRepo.save(
				new User("Amit", "Karir", "amikarir@in.ibm.com", userName, "apple", null, "My Address", null, null));
	}

	@Test
	public void testCreateUser() throws IOException, Exception {
		mockMvc.perform(post("/user").content(
				json(new UserSpec("Sid", "Gelda", "sid@gmail.com", "sidgelda", "sidg", null, "my address", null, null)))
				.contentType(contentType)).andExpect(status().isCreated());
	}

	@Test
	public void testUserLoggedIn() throws IOException, Exception {
		MvcResult result = mockMvc.perform(post("/user/login")
				.content(json(new UserSpec(null, null, null, userName, "apple", null, null, null, null)))
				.contentType(contentType)).andReturn();
		JsonParser parser = new JacksonJsonParser();
		Map<String, Object> users = parser.parseMap(result.getResponse().getContentAsString());
		if (users.isEmpty())
			fail("no user found");
		assertNotNull(users.get("sessionId"));

	}

	@Test
	public void testUserLoggedInWithNullInput() throws IOException, Exception {
		mockMvc.perform(post("/user/login")).andExpect(status().isBadRequest());

	}

	@Test
	public void testUserNotLoggedIn() throws IOException, Exception {
		MvcResult result = mockMvc.perform(post("/user/logout")
				.content(json(new UserSpec(null, null, null, userName, "apple", null, null, null, null)))
				.contentType(contentType)).andReturn();
		JsonParser parser = new JacksonJsonParser();
		Map<String, Object> users = parser.parseMap(result.getResponse().getContentAsString());
		if (users.isEmpty())
			fail("no user found");
		assertNull(users.get("sessionId"));

	}

	@Test
	public void testUserNotLoggedInWithNullInput() throws IOException, Exception {
		mockMvc.perform(post("/user/logout")).andExpect(status().isBadRequest());

	}

	@Test
	public void testUserNotLoggedInWithNullUsername() throws IOException, Exception {
		mockMvc.perform(post("/user/logout").contentType(contentType)
				.content(json(new UserSpec(null, null, null, null, "apple", null, null, null, null))))
				.andExpect(status().isBadRequest());

	}
	
	@Test
	public void testUserNotLoggedInWithNullPwd() throws IOException, Exception {
		mockMvc.perform(post("/user/logout").contentType(contentType)
				.content(json(new UserSpec(null, null, null, userName, null, null, null, null, null))))
				.andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateUser() {
		MvcResult result;
		try {
			result = mockMvc.perform(get("/users").accept(contentType)).andReturn();
			JsonParser parser = new JacksonJsonParser();
			List<Object> users = parser.parseList(result.getResponse().getContentAsString());
			LinkedHashMap map = (LinkedHashMap) users.get(0);
			String id = String.valueOf(map.get("id"));
			MvcResult res = mockMvc
					.perform(put("/user").accept(contentType)
							.contentType(contentType).content(json(new UserSpec(Long.valueOf(id), "Pritam", "Gade",
									null, null, null, null, null, "1", null))))
					.andExpect(status().isAccepted()).andReturn();
			Map<String, Object> user = parser.parseMap(res.getResponse().getContentAsString());
			assertEquals("1", String.valueOf(user.get("status")));
			assertEquals(id, String.valueOf(user.get("id")));
			assertEquals("Pritam", String.valueOf(user.get("firstname")));
			assertEquals("Gade", String.valueOf(user.get("lastname")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdateUserWithNullInput() throws IOException, Exception {
		mockMvc.perform(put("/user").contentType(contentType)).andExpect(status().isBadRequest());

	}

	@Test
	public void testUCreateUserWithDuplicateUsername() throws IOException, Exception {
		mockMvc.perform(post("/user").content(
				json(new UserSpec("Sid", "Gelda", "sid@gmail.com", userName, "sidg", null, "my address", null, null)))
				.contentType(contentType)).andExpect(status().isFound());

	}

	@Test
	public void testUpdateUserWithNullIdInInput() throws IOException, Exception {
		mockMvc.perform(put("/user").content(json(
				new UserSpec(null, "Amit", "Karir", "amikarir@in.ibm.com", userName, "apple", null, null, null, null)))
				.accept(contentType).contentType(contentType)).andExpect(status().isBadRequest());

	}

	@Test
	public void testUpdateWrongUser() throws IOException, Exception {
		mockMvc.perform(put("/user").content(json(new UserSpec(432424l, "Amit", "Karir", "amikarir@in.ibm.com",
				userName, "apple", null, null, null, null))).accept(contentType).contentType(contentType))
				.andExpect(status().isNotFound());

	}

	@Test
	public void testDeactivateUser() {
		MvcResult result;
		try {
			result = mockMvc.perform(get("/users").accept(contentType)).andReturn();
			JsonParser parser = new JacksonJsonParser();
			List<Object> users = parser.parseList(result.getResponse().getContentAsString());
			LinkedHashMap map = (LinkedHashMap) users.get(0);
			String id = String.valueOf(map.get("id"));
			MvcResult res = mockMvc
					.perform(
							put("/user/status").accept(contentType)
									.contentType(contentType).content(json(new UserSpec(Long.valueOf(id), null, null,
											null, null, null, null, null, "0", null))))
					.andExpect(status().isAccepted()).andReturn();
			Map<String, Object> user = parser.parseMap(res.getResponse().getContentAsString());
			assertEquals("0", String.valueOf(user.get("status")));
			assertEquals(id, String.valueOf(user.get("id")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testStatusWithNullSpec() throws Exception {
		mockMvc.perform(put("/user/status").contentType(contentType)).andExpect(status().isBadRequest());
	}

	@Test
	public void testStatusWithNullIdInSpec() throws Exception {
		mockMvc.perform(
				put("/user/status").content(json(new UserSpec(null, null, null, null, null, null, null, null, null)))
						.contentType(contentType))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testStatusWithNullStatusInSpec() throws Exception {
		mockMvc.perform(put("/user/status")
				.content(json(new UserSpec(1l, null, null, null, null, null, null, null, null, null)))
				.contentType(contentType)).andExpect(status().isBadRequest());
	}

	@Test
	public void testGetUser() throws Exception {
		mockMvc.perform(get("/user/" + userName).accept(contentType)).andExpect(content().contentType(contentType))
				.andExpect(status().isOk());
	}

	@Test
	public void userFound() throws Exception {
		mockMvc.perform(post("/user/login")
				.content(json(new UserSpec(null, null, null, "amitkarir", "apple", null, null, null, null)))
				.contentType(contentType)).andExpect(status().isOk());
	}

	@Test
	public void testLoginWithNullUsername() throws Exception {
		mockMvc.perform(
				post("/user/login").content(json(new UserSpec(null, null, null, null, "apple", null, null, null, null)))
						.contentType(contentType))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testLoginWithNullPassword() throws Exception {
		mockMvc.perform(post("/user/login")
				.content(json(new UserSpec(null, null, null, userName, null, null, null, null, null)))
				.contentType(contentType)).andExpect(status().isBadRequest());
	}

	@Test
	public void userNotFound() throws Exception {
		mockMvc.perform(post("/user/login")
				.content(json(new UserSpec(null, null, null, "amitkarir", "password", null, null, null, null)))
				.contentType(contentType)).andExpect(header().doesNotExist("sessionID"));
		;
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
