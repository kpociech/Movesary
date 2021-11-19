package com.consoul.movesary.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveWithoutUser {

	Long id;
	String name;
	String description;
	String videoURL;

}
