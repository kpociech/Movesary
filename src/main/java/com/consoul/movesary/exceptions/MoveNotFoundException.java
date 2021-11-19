package com.consoul.movesary.exceptions;

public class MoveNotFoundException extends RuntimeException {

	public MoveNotFoundException(Long id) {
		super("move with id: " + id + " not found");
	}
}
