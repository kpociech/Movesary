package com.consoul.movesary.services;

import com.consoul.movesary.dtos.MoveWithoutUser;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.dtos.UserWithMoves;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.ForbiddenException;
import com.consoul.movesary.exceptions.UserNotFoundException;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.UserRepository;
import com.consoul.movesary.security.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MoveService moveService;
    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, MoveService moveService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.moveService = moveService;
    }

    @Override
    public List<UserDTO> getAllUsersDTO() {
        return mapAllToDTO(userRepository.getAll());
    }

    @Override
    public UserDTO getUserWithMostMoves() {
        User user = userRepository.getUserWithMostMoves();

        return getUserDTO(user);
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);

        try {
            user = userRepository.create(user);
        } catch (DataIntegrityViolationException ex) {
            throw new BadRequestException("Username=" + user.getUsername() + " already exists");
        }

        return getUserDTO(user);
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        User user = getUserByUsername(userDTO.getUsername());

        modelMapper.map(userDTO, user);
        user = userRepository.update(user);

        return getUserDTO(user);
    }

    @Override
    public void delete(String username) {
        userRepository.delete(getUserByUsername(username));
    }

    @Override
    public UserDTO getUserDTObyUsername(String username) {
        return getUserDTO(getUserByUsername(username));
    }

    @Override
    public UserWithMoves getUserWithMoves(String username) {
        List<MoveWithoutUser> moveWithoutUser = moveService.getMovesBasedOnUsername(username).stream()
                                                           .map(n -> modelMapper.map(n, MoveWithoutUser.class))
                                                           .collect(Collectors.toList());

        UserWithMoves userWithMoves = modelMapper.map(getUserByUsername(username), UserWithMoves.class);
        userWithMoves.setMoves(moveWithoutUser);

        return userWithMoves;
    }

    @Override
    public UserDTO checkUserExistenceInDBAfterLoginngIn() {
        String keycloakUsername = CurrentUserProvider.getCurrentUser().getUsername();
        UserDTO userDTO;

        try {
            userDTO = getUserDTObyUsername(keycloakUsername);
        } catch (UserNotFoundException e) {
            log.info("user: " + keycloakUsername + " is being added to DB");
            userDTO = create(CurrentUserProvider.getCurrentUser());
        }

        return userDTO;
    }

    public void verifyUser(String username) {
        String keycloakUsername = CurrentUserProvider.getCurrentUser().getUsername();
        if(!username.equals(keycloakUsername) && !CurrentUserProvider.isAdmin()) {
            throw new ForbiddenException(keycloakUsername);
        }
    }

    private List<UserDTO> mapAllToDTO(List<User> users) {
        return users.stream()
                    .map(this::getUserDTO)
                    .collect(Collectors.toList());
    }

    private UserDTO getUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User getUserByUsername(String username) {
        return userRepository.get(username)
                             .orElseThrow(() -> new UserNotFoundException(username));
    }

}
