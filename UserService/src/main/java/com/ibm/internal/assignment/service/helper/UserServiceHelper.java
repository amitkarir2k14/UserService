package com.ibm.internal.assignment.service.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.ibm.internal.assignment.entity.User;
import com.ibm.internal.assignment.entity.spec.UserSpec;

@Component
public class UserServiceHelper<T> {

	public ResponseEntity<T> getResponseEntity(T t, HttpHeaders headers, HttpStatus status) {
		return new ResponseEntity<T>(t, headers, status);
	}

	public ResponseEntity<T> getResponseEntity(T t, HttpStatus status) {
		return new ResponseEntity<T>(t, status);
	}
	
	public void updateUserProps(User user, UserSpec userSpec) {
		if (userSpec.getId() != null)
			user.setId(userSpec.getId());
		if (userSpec.getAddress() != null)
			user.setAddress(userSpec.getAddress());
		if (userSpec.getEmailAddress() != null)
			user.setEmail(userSpec.getAddress());
		if (userSpec.getFinPortfolioId() != null)
			user.setPortfolioId((userSpec.getFinPortfolioId()));
		if (userSpec.getFname() != null)
			user.setFirstname(userSpec.getFname());
		if (userSpec.getLname() != null)
			user.setLastname(userSpec.getLname());
		if (userSpec.getPwd() != null)
			user.setPwd(userSpec.getPwd());
		if (userSpec.getStatus() != null)
			user.setStatus(userSpec.getStatus());
		if (userSpec.getType() != null)
			user.setType(userSpec.getType());
		if (userSpec.getUsername() != null)
			user.setUname(userSpec.getUsername());
	}
}
