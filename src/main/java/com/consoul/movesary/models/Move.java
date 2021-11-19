package com.consoul.movesary.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "Moves")
public class Move {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	boolean visibility = true;

	LocalDate dateCreation;
	LocalDate updatedAt;

	String name;
	String description;
	String videoURL;

	static Long longuu = 10L;

	@PrePersist
	protected void onPersist() {
		if(dateCreation == null) {
			log.info("persisting Move");
			dateCreation = LocalDate.now();
		}
	}
	
    @PreUpdate
    protected void onUpdate() {
    	log.info("update Move");
        updatedAt = LocalDate.now();
    }
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference
	@EqualsAndHashCode.Exclude
	User user;

	public Move(String name, String description, String videoURL, User user) {
		this.name = name;
		this.description = description;
		this.dateCreation = LocalDate.now();
		this.user = user;
		this.videoURL = videoURL;
	}

	public Move(String name, String description, String videoURL, LocalDate dateCreation, User user) {
		this.name = name;
		this.description = description;
		this.dateCreation = dateCreation;
		this.user = user;
		this.videoURL = videoURL;
	}

}
