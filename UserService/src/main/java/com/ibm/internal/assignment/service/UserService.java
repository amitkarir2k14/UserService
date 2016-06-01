package com.ibm.internal.assignment.service;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.internal.assignment.entity.User;
import com.ibm.internal.assignment.entity.manager.UserServiceEntityManager;
import com.ibm.internal.assignment.entity.spec.UserSpec;
import com.ibm.internal.assignment.entity.spec.UserSpecEntityTransformer;
import com.ibm.internal.assignment.repository.UserRepository;
import com.ibm.internal.assignment.service.exceptions.Error;
import com.ibm.internal.assignment.service.helper.UserServiceHelper;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@ComponentScan(basePackages = { "com.ibm.internal.assignment" })
//@EnableEurekaClient
@RestController
public class UserService {

	@Autowired
	private UserServiceEntityManager entityManager;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	UserServiceHelper<User> serviceHelper;
	@Autowired
	UserServiceHelper<Error> errorHelper;

	public static void main(String[] args) {
//		System.setProperty("spring.config.name", "userServiceClient");

		SpringApplication.run(UserService.class, args);

	}

	@RequestMapping(value = "/user", consumes = "application/json", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody UserSpec userSpec, UriComponentsBuilder urib) {
		if (null == userSpec)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null != entityManager.getByUsername(userSpec.getUsername()))
			return errorHelper.getResponseEntity(new Error(2, "Duplicate  user"), HttpStatus.FOUND);
		User user = new UserSpecEntityTransformer(userSpec).transformSpecToEntity();
		User postSave = entityManager.save(user);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
		return serviceHelper.getResponseEntity(postSave, httpHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/user/update", consumes = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@Valid @RequestBody UserSpec userSpec, UriComponentsBuilder urib) {
		if (null == userSpec)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null == userSpec.getId())
			return errorHelper.getResponseEntity(new Error(1, "Identity is required for update"),
					HttpStatus.BAD_REQUEST);
		User dbUser = entityManager.findOne(userSpec.getId());
		if (null == dbUser)
			return errorHelper.getResponseEntity(new Error(3, "user to be updated not found."), HttpStatus.NOT_FOUND);
		serviceHelper.updateUserProps(dbUser, userSpec);
		User postSave = entityManager.save(dbUser);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
		return new ResponseEntity<User>(postSave, httpHeaders, HttpStatus.ACCEPTED);

	}

	@RequestMapping(value = "/user/status", consumes = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<?> deactivateUser(@RequestBody UserSpec userSpec, UriComponentsBuilder urib) {
		if (null == userSpec)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null == userSpec.getId())
			return errorHelper.getResponseEntity(new Error(1, "Identity is required for update"),
					HttpStatus.BAD_REQUEST);
		if (userSpec.getStatus() == null)
			return errorHelper.getResponseEntity(new Error(1, "invalid status to set"), HttpStatus.BAD_REQUEST);
		User dbUser = entityManager.findOne(userSpec.getId());
		if (null == dbUser)
			return errorHelper.getResponseEntity(new Error(3, "user to be updated not found."), HttpStatus.NOT_FOUND);
		dbUser.setStatus(userSpec.getStatus());
		User postSave = entityManager.save(dbUser);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
		return new ResponseEntity<User>(postSave, httpHeaders, HttpStatus.ACCEPTED);

	}

	@RequestMapping(value = "/user/{username}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable String  username) {
		User user = entityManager.getByUsername(username);
		if (null == user)
			return new ResponseEntity<Error>(new Error(3, "user not found."), HttpStatus.NOT_FOUND);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/users", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUsers() {
		List<User> users = entityManager.getAllUsers();
		if (null == users || users.isEmpty())
			return new ResponseEntity<Error>(new Error(4, "users not found."), HttpStatus.NOT_FOUND);
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}

	@RequestMapping(value = "/user/login", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody UserSpec userSpec) {
		if (null == userSpec)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null == userSpec.getUsername())
			return errorHelper.getResponseEntity(new Error(1, "username is null"),
					HttpStatus.BAD_REQUEST);
		if (userSpec.getPwd() == null)
			return errorHelper.getResponseEntity(new Error(1, "pwd is null"), HttpStatus.BAD_REQUEST);			
		User user = entityManager.getByUsernameAndPassword(userSpec.getUsername(), userSpec.getPwd());
		if (null == user)
			return new ResponseEntity<Error>(new Error(3, "user with supplied credentials not found."),
					HttpStatus.NOT_FOUND);
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionID", String.valueOf(Math.random()));
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

}