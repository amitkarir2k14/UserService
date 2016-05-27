package com.ibm.internal.assignment.service.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ibm.internal.assignment.entity.User;

@Component
public class UserServiceHelper<T> {

	public ResponseEntity<T> getResponseEntity(T t, HttpHeaders headers, HttpStatus status) {
		return new ResponseEntity<T>(t, headers, status);
	}

	public ResponseEntity<T> getResponseEntity(T t, HttpStatus status) {
		return new ResponseEntity<T>(t, status);
	}
	
	public void updateUserProps(User dbUser, User reqUser) {
		dbUser.setFirstname(reqUser.getFirstname());
		dbUser.setLastname(reqUser.getLastname());
		dbUser.setEmail(reqUser.getEmail());
		dbUser.setAddress(reqUser.getAddress());
		dbUser.setPwd(reqUser.getPwd());
		dbUser.setStatus(reqUser.getStatus());
		dbUser.setType(reqUser.getType());
	}
}
