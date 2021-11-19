package com.consoul.movesary.services;

import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.models.Move;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.MoveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.consoul.movesary.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class MoveServiceImplTests {

    @Mock
    MoveRepository moveRepository;

    @InjectMocks
    MoveServiceImpl moveServiceImpl;

//    @Mock
//    MoveService moveService;

    @Mock
    ModelMapper modelMapper;

    MoveDTO moveDTO;
    Move move;
    UserDTO userDTO;
    User user;
    Optional<Move> moveOpt;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL);
        moveDTO = new MoveDTO(SAMPLE_ID, SAMPLE_MOVE_NAME, SAMPLE_DESC, SAMPLE_VIDEOO_URL, userDTO);
        user = new User(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL, SAMPLE_DATE);
        move = new Move(SAMPLE_MOVE_NAME, SAMPLE_DESC, SAMPLE_VIDEOO_URL, user);
        moveOpt = Optional.of(move);
    }

    @Test
    void whenMoveExists_thenGetMoveById() {

        given(moveRepository.get(SAMPLE_ID)).willReturn(moveOpt);
        given(modelMapper.map(move, MoveDTO.class)).willReturn(moveDTO);

        MoveDTO moveById = moveServiceImpl.getMoveDTOById(SAMPLE_ID);

        assertEquals(moveDTO, moveById);
        then(moveRepository).should().get(SAMPLE_ID);
    }

//    @Test
//    void whenMoveNotExist_thenFailToGetMoveById() {
//        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));
//
//        MoveNotFoundException moveNotFoundException =
//                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.getMoveDTOById(SAMPLE_ID));
//
//        assertEquals("id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
//        then(moveRepository).should().get(SAMPLE_ID);
//    }

    @Test
    void whenMovesExist_thenReturnMovesCreatedToday() {

        List<Move> moves = new ArrayList<>(Arrays.asList(move));
        LocalDate localDate = LocalDate.now();

        given(moveRepository.findAllByDateCreation(localDate)).willReturn(moves);
        given(modelMapper.map(moves.get(0), MoveDTO.class)).willReturn(moveDTO);

        List<MoveDTO> movesCreatedToday = moveServiceImpl.getMovesCreatedToday();

        assertEquals(movesCreatedToday.get(0), moveDTO);
        then(moveRepository).should().findAllByDateCreation(localDate);
    }

    @Test
    void whenDateIsValid_thenGetMovesByDate() {

        List<Move> moves = new ArrayList<>(Arrays.asList(move));
        String date = "2020-12-15";
        LocalDate parsedDate = LocalDate.parse(date);

        given(moveRepository.findAllByDateCreation(parsedDate)).willReturn(moves);
        given(modelMapper.map(moves.get(0), MoveDTO.class)).willReturn(moveDTO);

        List<MoveDTO> movesCreatedToday = moveServiceImpl.findAllByDateCreation(parsedDate);

        assertEquals(movesCreatedToday.get(0), moveDTO);
        then(moveRepository).should().findAllByDateCreation(parsedDate);
    }

    @Test
    void whenMovesExist_ThenGetMovesBasedOnUsername() {

        List<Move> moves = new ArrayList<>(Arrays.asList(move));
        List<MoveDTO> movesDTO = new ArrayList<>(Arrays.asList(moveDTO));

        given(moveRepository.getMovesBasedOnUsername(SAMPLE_USERNAME)).willReturn(moves);
        given(modelMapper.map(moves.get(0), MoveDTO.class)).willReturn(moveDTO);

        List<MoveDTO> movesDTOReceived = moveServiceImpl.getMovesBasedOnUsername(SAMPLE_USERNAME);

        assertEquals(movesDTOReceived.get(0), moveDTO);
        then(moveRepository).should().getMovesBasedOnUsername(SAMPLE_USERNAME);
    }

    @Test
    void whenMoveIsValid_thenCreateMove() {

        given(modelMapper.map(moveDTO, Move.class)).willReturn(move);
        given(moveRepository.create(move)).willReturn(move);
        given(modelMapper.map(move, MoveDTO.class)).willReturn(moveDTO);

        MoveDTO moveDTOReceived = moveServiceImpl.create(moveDTO);

        assertEquals(moveDTO, moveDTOReceived);
        then(moveRepository).should().create(move);
    }


    @Test
    void whenMoveExists_thenUpdateMove() {

        given(moveRepository.get(SAMPLE_ID)).willReturn(moveOpt);

        doNothing().when(modelMapper).map(moveDTO, move);
        given(moveRepository.update(move)).willReturn(move);
        given(modelMapper.map(move, MoveDTO.class)).willReturn(moveDTO);

        MoveDTO moveDTOReceived = moveServiceImpl.update(moveDTO);

        assertEquals(moveDTO, moveDTOReceived);
        then(moveRepository).should().update(move);
    }

//    @Test
//    void whenMoveNotExist_thenFailToUpdateMove() {
//        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));
//
//        MoveNotFoundException moveNotFoundException =
//                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.update(moveDTO));
//
//        assertEquals("id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
//        then(moveRepository).should().get(SAMPLE_ID);
//    }

    @Test
    void whenMoveExists_thenDeleteMove() {
        moveRepository.delete(move);

        then(moveRepository).should().delete(move);
        then(moveRepository).should().delete(move);
        then(moveRepository).should().delete(move);
    }

//    @Test
//    void whenMoveNotExist_thenFailToDeleteMove() {
//        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));
//
//        MoveNotFoundException moveNotFoundException =
//                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.delete(SAMPLE_ID));
//
//        assertEquals("id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
//        then(moveRepository).should().get(SAMPLE_ID);
//    }

}
