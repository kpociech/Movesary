package com.consoul.movesary.exceptions;

public class ForbiddenException extends RuntimeException{

	public ForbiddenException(String username) {
		super("user: " + username + " is not authorized to perform this operation");
	}

}
