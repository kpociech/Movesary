package com.consoul.movesary.services;

import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.MoveWithoutUser;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.dtos.UserWithMoves;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.UserNotFoundException;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.consoul.movesary.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTests {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userServiceImpl;

    @Mock
    MoveService moveService;

    @Mock
    ModelMapper modelMapper;

    UserDTO userDTO;
    User user;
    Optional<User> userOpt;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL);
        user = new User(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL, SAMPLE_DATE);
        userOpt = Optional.of(user);
    }

    @Test
    void whenUserExists_thenReturnUserByUsername() {

        //SAMPLE_USERNAME can have any content - it doesnt matter here.
        given(userRepository.get(SAMPLE_USERNAME)).willReturn(userOpt);
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);

        UserDTO userByUsername = userServiceImpl.getUserDTObyUsername(SAMPLE_USERNAME);

        assertEquals(userDTO, userByUsername);
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

    @Test
    void whenUserNotExist_thenReturnStatusNotFound() {
        given(userRepository.get(SAMPLE_USERNAME)).willThrow(new UserNotFoundException(SAMPLE_USERNAME));

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userServiceImpl.getUserDTObyUsername(SAMPLE_USERNAME));

        assertEquals(SAMPLE_USERNAME + " not found", userNotFoundException.getMessage());
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

    @Test
    void whenUsersExist_thenReturnListOfUsers() {

        ArrayList<User> users = new ArrayList<>();
        users.add(user);

        given(userRepository.getAll()).willReturn(users);
        given(modelMapper.map(users.get(0), UserDTO.class)).willReturn(userDTO);

        List<UserDTO> allUsersDTO = userServiceImpl.getAllUsersDTO();

        assertEquals(allUsersDTO.get(0), userDTO);
        then(userRepository).should().getAll();
    }


    @Test
    void whenUsersNotExist_thenReturnEmptyList() {

        given(userRepository.getAll()).willReturn(Collections.EMPTY_LIST);

        List<UserDTO> allUsersDTO = userServiceImpl.getAllUsersDTO();

        assertTrue(allUsersDTO.isEmpty());
        then(userRepository).should().getAll();
    }

    @Test
    void whenUserExists_thenReturnUserWithMostMoves() {

        given(userRepository.getUserWithMostMoves()).willReturn(user);
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);

        UserDTO userWithMostMoves = userServiceImpl.getUserWithMostMoves();

        assertEquals(userDTO, userWithMostMoves);
        then(userRepository).should().getUserWithMostMoves();

    }

    @Test
    void whenUserHasMoves_thenReturnUserWithMoves() {

        UserWithMoves userWithMoves = new UserWithMoves();
        userWithMoves.setUsername(userDTO.getUsername());
        userWithMoves.setFullName(userDTO.getFullName());
        userWithMoves.setEmail(userDTO.getEmail());

        //MoveDTO moveDTO = new MoveDTO(1L, "justName", "justDesc", userDTO);
        MoveDTO moveDTO = new MoveDTO(1L, "justName", "justDesc", SAMPLE_VIDEOO_URL, userDTO);
        MoveWithoutUser moveWithoutUser = new MoveWithoutUser(1L, "justName", "justDesc", SAMPLE_VIDEOO_URL);

        List<MoveDTO> listOfMovesDTO = new ArrayList<>();
        listOfMovesDTO.add(moveDTO);

        List<MoveWithoutUser> listOfMoves = new ArrayList<>();
        listOfMoves.add(moveWithoutUser);

        given(moveService.getMovesBasedOnUsername(userWithMoves.getUsername())).willReturn(listOfMovesDTO);
        given(modelMapper.map(moveDTO, MoveWithoutUser.class)).willReturn(moveWithoutUser);
        given(userRepository.get(SAMPLE_USERNAME)).willReturn(userOpt);
        given(modelMapper.map(user, UserWithMoves.class)).willReturn(userWithMoves);

        //userWithMovesDTO is getting his moves list set in this method and is returned as userWithMovesDTOReceived
        UserWithMoves userWithMovesReceived = userServiceImpl.getUserWithMoves(userWithMoves.getUsername());

        assertEquals(userWithMoves, userWithMovesReceived);
        then(moveService).should().getMovesBasedOnUsername(userWithMoves.getUsername());
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

    @Test
    void whenUserNotExist_thenFailToReturnUserWithMoves() {
        given(userRepository.get(SAMPLE_USERNAME)).willThrow(new UserNotFoundException(SAMPLE_USERNAME));

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userServiceImpl.getUserWithMoves(SAMPLE_USERNAME));

        assertEquals(SAMPLE_USERNAME + " not found", userNotFoundException.getMessage());
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

    @Test
    void whenUserNotExist_thenCreateUser() {

        given(modelMapper.map(userDTO, User.class)).willReturn(user);
        given(userRepository.create(user)).willReturn(user);
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);

        UserDTO userDTOReceived = userServiceImpl.create(userDTO);

        assertEquals(userDTO, userDTOReceived);
        then(userRepository).should().create(user);

    }

    @Test
    void whenUserAlreadyExists_thenFailToCreateUser() {

        given(modelMapper.map(userDTO, User.class)).willReturn(user);
        given(userRepository.create(user)).willThrow(new BadRequestException("Username " + user.getUsername() + " already exists"));

        assertThrows(BadRequestException.class, () -> userServiceImpl.create(userDTO));
        then(userRepository).should().create(user);

    }

    @Test
    void whenUserExists_thenUpdateUser() {

        given(userRepository.get(SAMPLE_USERNAME)).willReturn(userOpt);

        //given(modelMapper.map(userDTO, user)).;
        doNothing().when(modelMapper).map(userDTO, user);
        given(userRepository.update(user)).willReturn(user);
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);

        UserDTO userDTOReceived = userServiceImpl.update(userDTO);

        assertEquals(userDTO, userDTOReceived);
        then(userRepository).should().update(user);
    }

    @Test
    void whenUserNotExist_thenFailToUpdateUser() {
        given(userRepository.get(SAMPLE_USERNAME)).willThrow(new UserNotFoundException(SAMPLE_USERNAME));

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userServiceImpl.update(userDTO));

        assertEquals(SAMPLE_USERNAME + " not found", userNotFoundException.getMessage());
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

    @Test
    void whenUserExists_thenDeleteUser() {
        userRepository.delete(user);

        then(userRepository).should().delete(user);
    }

    @Test
    void whenUserNotExist_thenFailToDeleteUser() {
        given(userRepository.get(SAMPLE_USERNAME)).willThrow(new UserNotFoundException(SAMPLE_USERNAME));

        UserNotFoundException userNotFoundException =
                assertThrows(UserNotFoundException.class, () -> userServiceImpl.delete(SAMPLE_USERNAME));

        assertEquals(SAMPLE_USERNAME + " not found", userNotFoundException.getMessage());
        then(userRepository).should().get(SAMPLE_USERNAME);
    }

}
