package com.ibm.internal.assignment.entity.manager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibm.internal.assignment.entity.User;
import com.ibm.internal.assignment.repository.UserRepository;

@Component
public class UserServiceEntityManager {

	@Autowired
	private UserRepository userRepository;

	public User save(User user) {
		return userRepository.save(user);
	}

	public User findOne(Long id) {
		return userRepository.findOne(id);
	}

	public User getByUsername(String username) {
		return userRepository.findByUname(username);
	}
	

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getByUsernameAndPassword(String username, String password) {
		return userRepository.findByUnameAndPwd(username, password);
	}
}
