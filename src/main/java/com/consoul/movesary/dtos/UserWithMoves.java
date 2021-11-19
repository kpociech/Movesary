package com.consoul.movesary.dtos;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserWithMoves {

	String username;
	String fullName;
	String email;
	private List<MoveWithoutUser> moves = new LinkedList<>();

}
