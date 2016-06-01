package com.ibm.internal.assignment.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

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

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UserService.class)
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
	public void testUpdateUser() {
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
									.contentType(contentType).content(json(new UserSpec(Long.valueOf(id), "Pritam", "Gade",
											null, null, null, null, null, "1", null))))
					.andExpect(status().isAccepted()).andReturn();
			users= parser.parseList(res.getResponse().getContentAsString());
			map = (LinkedHashMap) users.get(0);
			assertEquals("1", String.valueOf(map.get("status")));
			assertEquals(id, String.valueOf(map.get("id")));
			assertEquals("Pritam", String.valueOf(map.get("fname")));
			assertEquals("Gade", String.valueOf(map.get("lname")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			users= parser.parseList(res.getResponse().getContentAsString());
			map = (LinkedHashMap) users.get(0);
			assertEquals("0", String.valueOf(map.get("status")));
			assertEquals(id, String.valueOf(map.get("id")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
