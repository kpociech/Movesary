package com.consoul.movesary.controllers.rest;

import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.dtos.UserWithMoves;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserRest {

    private final UserService userService;

    public UserRest(UserService userService) {
        this.userService = userService;
    }

    @RolesAllowed("admin")
    @GetMapping
    public List<UserDTO> getUsers() {
        return userService.getAllUsersDTO();
    }

    @RolesAllowed("user")
    @GetMapping("/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        userService.verifyUser(username);

        return userService.getUserDTObyUsername(username);
    }

    @RolesAllowed("user")
    @GetMapping("/{username}/moves")
    public UserWithMoves getUserWithMovesByUsername(@PathVariable String username) {
        userService.verifyUser(username);

        return userService.getUserWithMoves(username);
    }

    @RolesAllowed("user")
    @GetMapping("/most-skilled")
    public UserDTO getMostSkilledUser() {
        return userService.getUserWithMostMoves();
    }

    @RolesAllowed("admin")
    @PutMapping
    public UserDTO update(@RequestBody UserDTO userDTO) {
        if (ObjectUtils.isEmpty(userDTO.getUsername())) {
            throw new BadRequestException("Invalid username: " + userDTO.getUsername());
        }

        return userService.update(userDTO);
    }

    @RolesAllowed("admin")
    @DeleteMapping("/{username}")
    public void deleteUser(@PathVariable String username) {
        userService.delete(username);
    }

    @RolesAllowed("admin")
    @PostMapping
    public UserDTO addUser(@RequestBody UserDTO userDTO) {
        if (ObjectUtils.isEmpty(userDTO.getUsername())) {
            throw new BadRequestException("Invalid username: " + userDTO.getUsername());
        }

        return userService.create(userDTO);
    }
}
