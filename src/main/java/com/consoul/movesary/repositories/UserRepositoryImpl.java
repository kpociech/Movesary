package com.consoul.movesary.repositories;

import com.consoul.movesary.models.User;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class UserRepositoryImpl extends MainRepository<User, String> implements UserRepository{

    @Override
    public User getUserWithMostMoves() {
        Query query = entityManager.createNativeQuery(
                "SELECT username, full_name, email, users.date_Creation, users.updated_At" +
                        " FROM users" +
                        " INNER JOIN moves ON username = user_id" +
                        " GROUP BY username" +
                        " ORDER BY count(moves.user_id)" +
                        " DESC" +
                        " LIMIT 1;", User.class);
        return (User)query.getSingleResult();
    }
}
