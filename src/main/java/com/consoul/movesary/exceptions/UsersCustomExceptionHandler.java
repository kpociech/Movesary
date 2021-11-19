package com.consoul.movesary.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class UsersCustomExceptionHandler {
	
	@ResponseBody
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	public String userBadRequestHanlder(BadRequestException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	@ExceptionHandler(UserNotFoundException.class)
	public String userNotFoundHanlder(UserNotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ResponseStatus(value=HttpStatus.NOT_FOUND)
	@ExceptionHandler(MoveNotFoundException.class)
	public String moveNotFoundHanlder(MoveNotFoundException ex) {
		return ex.getMessage();
	}

	@ResponseBody
	@ResponseStatus(value=HttpStatus.FORBIDDEN)
	@ExceptionHandler(ForbiddenException.class)
	public String unauthorizedAccessHandler(ForbiddenException ex) {
		return ex.getMessage();
	}

}
