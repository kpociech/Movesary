package com.consoul.movesary.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Slf4j
@Table(name = "Users")
public class User {

	LocalDate dateCreation;
	LocalDate updatedAt;

	@Column(unique = true)
    @Id
	String username;

	String fullName;
	String email;
	
	@PrePersist
	protected void onPersist() {
		if(dateCreation == null) {
			log.info("Pesisting new User");
			dateCreation = LocalDate.now();
		}
	}
	
    @PreUpdate
    protected void onUpdate() {
    	log.info("updating User");
    	updatedAt = LocalDate.now();
    }

	public User(String username, String fullName, String email) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.dateCreation = LocalDate.now();
	}

	public User(String username, String fullName, String email, LocalDate dateCreation) {
		super();
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.dateCreation = dateCreation;
	}

}
