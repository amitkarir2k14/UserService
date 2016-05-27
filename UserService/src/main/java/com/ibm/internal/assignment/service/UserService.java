package com.ibm.internal.assignment.service;

import java.net.URI;

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
import com.ibm.internal.assignment.repository.UserRepository;
import com.ibm.internal.assignment.service.exceptions.Error;
import com.ibm.internal.assignment.service.helper.UserServiceHelper;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@ComponentScan(basePackages = { "com.ibm.internal.assignment" })
@EnableEurekaClient
@RestController
@RequestMapping("/user")
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
		System.setProperty("spring.config.name", "userServiceClient");

		SpringApplication.run(UserService.class, args);

	}

	@RequestMapping(value = "/signup", consumes = "application/json", method = RequestMethod.POST)
	public ResponseEntity<?> createUser(@Valid @RequestBody User user, UriComponentsBuilder urib) {
		if (null == user)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null != entityManager.getByUsername(user.getUname()))
			return errorHelper.getResponseEntity(new Error(2, "Duplicate  userr"), HttpStatus.FOUND);
		User postSave = entityManager.save(user);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
		return serviceHelper.getResponseEntity(postSave, httpHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/update", consumes = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@Valid @RequestBody User user, UriComponentsBuilder urib) {
		if (null == user)
			return errorHelper.getResponseEntity(new Error(1, "invalid user"), HttpStatus.BAD_REQUEST);
		if (null == user.getId())
			return errorHelper.getResponseEntity(new Error(1, "Identity is required for update"),
					HttpStatus.BAD_REQUEST);
		User dbUser = entityManager.findOne(user.getId());
		if (null == dbUser)
			return errorHelper.getResponseEntity(new Error(3, "user to be updated not found."), HttpStatus.NOT_FOUND);
		serviceHelper.updateUserProps(dbUser, user);
		User postSave = entityManager.save(dbUser);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
		return new ResponseEntity<User>(postSave, httpHeaders, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		User user = entityManager.findOne(id);
		if (null == user)
			return new ResponseEntity<Error>(new Error(3, "user by {id}=" + id + " not found."), HttpStatus.NOT_FOUND);
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}

	@RequestMapping(value = "/login", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<?> login(@RequestParam("uname") String username, @RequestParam("pwd") String password) {
		User user = entityManager.getByUsernameAndPassword(username, password);
		if (null == user)
			return new ResponseEntity<Error>(new Error(3, "user with supplied credentials not found."),
					HttpStatus.NOT_FOUND);
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionID", String.valueOf(Math.random()));
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

}