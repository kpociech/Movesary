package com.consoul.movesary.exceptions;

public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(String username) {
		super(username + " not found");
	}
}
