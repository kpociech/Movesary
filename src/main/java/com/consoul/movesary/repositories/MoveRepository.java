package com.consoul.movesary.repositories;

import com.consoul.movesary.models.Move;

import java.time.LocalDate;
import java.util.List;

public interface MoveRepository extends BaseRepository<Move, Long>{
//TODO test these 2 method in MoveRepositoryTest class and 1 method from UserRepository
    List<Move> findAllByDateCreation(LocalDate localDate);

    List<Move> getMovesBasedOnUsername(String username);

    Move getMoveByIdAndUsername(Long id, String username);
}
