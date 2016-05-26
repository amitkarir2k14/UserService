package com.ibm.internal.assignment.service;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ibm.internal.assignment.entity.User;
import com.ibm.internal.assignment.repository.UserRepository;
import com.ibm.internal.assignment.service.exceptions.InvalidUserException;
import com.ibm.internal.assignment.service.exceptions.UserNotFoundException;

@EnableJpaRepositories(basePackageClasses = { UserRepository.class })
@ComponentScan(basePackages = { "com.ibm.internal.assignment" })
@EnableEurekaClient
@RestController
@RequestMapping("/user")
public class UserService {

	@Autowired
	private UserServiceEntityManager entityManager; 	

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "userServiceClient");

		SpringApplication.run(UserService.class, args);

	}

	@RequestMapping(value = "/signup", consumes = "application/json", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public void createUser(@RequestBody User user, UriComponentsBuilder urib) {
		if (null == user)
			throw new InvalidUserException();
		User postSave = entityManager.save(user);
		HttpHeaders httpHeaders = new HttpHeaders();
		URI entityUri = urib.path(String.valueOf(postSave.getId())).build().toUri();
		httpHeaders.setLocation(entityUri);
	}

	@RequestMapping(value = "/{id}", produces = "application/json", method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public User getUser(@PathVariable Long id)  {
		User user = entityManager.findOne(id);
		if (null == user)
			throw new UserNotFoundException();
		return user;
	}

}