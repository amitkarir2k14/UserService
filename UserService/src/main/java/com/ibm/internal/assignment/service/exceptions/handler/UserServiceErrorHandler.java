package com.ibm.internal.assignment.service.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.ibm.internal.assignment.service.exceptions.Error;
import com.ibm.internal.assignment.service.exceptions.InvalidCredentialsException;
import com.ibm.internal.assignment.service.exceptions.InvalidUserException;
import com.ibm.internal.assignment.service.exceptions.UserNotFoundException;

@ControllerAdvice
public class UserServiceErrorHandler {
	
	@ExceptionHandler(InvalidUserException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	public Error invalidUser(){
		return new Error(1, "invalid user.");
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	public Error userUnavailable(Long id){
		return new Error(2, "user with {id}=" + id+" not found.");
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	public Error invalidCredentials(){
		return new Error(2, "invalid credentials passed in.");
	}

}
