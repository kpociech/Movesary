package com.consoul.movesary.services;

import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.dtos.UserWithMoves;
import java.util.List;

public interface UserService {

    List<UserDTO> getAllUsersDTO();

    UserDTO getUserWithMostMoves();

    void delete(String username);

    UserDTO getUserDTObyUsername(String username);

    UserDTO create(UserDTO userDTO);

    UserDTO update(UserDTO userDTO);

    UserWithMoves getUserWithMoves(String username);

    void verifyUser(String username);
}
