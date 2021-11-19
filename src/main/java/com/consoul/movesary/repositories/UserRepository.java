package com.consoul.movesary.repositories;

import com.consoul.movesary.models.User;


public interface UserRepository extends BaseRepository<User, String> {
    User getUserWithMostMoves();

}
