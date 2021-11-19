package com.consoul.movesary.repositories;

import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.models.Move;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MoveRepositoryImpl extends MainRepository<Move, Long> implements MoveRepository{

	@Override
	public List<Move> findAllByDateCreation(LocalDate localDate) {
	  	TypedQuery<Move> query = entityManager.createQuery("select move from Move move where" +
				" move.dateCreation = '" + localDate + "'" + "order by move.dateCreation", Move.class);
	  	return query.getResultList();
	}

	@Override
	public List<Move> getMovesBasedOnUsername(String username) {
	  	TypedQuery<Move> query = entityManager.createQuery("select move from Move move" +
				" where move.user.username = '" + username + "'" + "order by move.dateCreation", Move.class);
	  	return query.getResultStream().collect(Collectors.toList());
	}

	@Override
	public Move getMoveByIdAndUsername(Long id, String username) {
		TypedQuery<Move> query = entityManager.createQuery("select move from Move " +
				" where move.user.username = '" + username + "'" +  "AND move.id =" + id, Move.class);
		try{
			return query.getSingleResult();
		}catch(NoResultException e) {
			throw new MoveNotFoundException(id);
		}
	}
}
