package com.consoul.movesary.services;

import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.ForbiddenException;
import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.models.Move;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.MoveRepository;
import com.consoul.movesary.security.CurrentUserProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.consoul.movesary.utils.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class MoveServiceImplTests {

    @Mock
    MoveRepository moveRepository;

    @Mock
    UserService userService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    MoveServiceImpl moveServiceImpl;

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

    @Test
    void whenMoveNotExist_thenFailToGetMoveById() {
        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));

        MoveNotFoundException moveNotFoundException =
                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.getMoveDTOById(SAMPLE_ID));

        assertEquals("move with id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
        then(moveRepository).should().get(SAMPLE_ID);
    }

    @Test
    void whenMovesExist_thenGetMovesCreatedToday() {

        List<Move> moves = new ArrayList<>(Arrays.asList(move));
        LocalDate localDate = LocalDate.now();

        given(moveRepository.findAllByDateCreation(localDate)).willReturn(moves);
        given(modelMapper.map(moves.get(0), MoveDTO.class)).willReturn(moveDTO);

        List<MoveDTO> movesCreatedToday = moveServiceImpl.getMovesCreatedToday();

        assertEquals(movesCreatedToday.get(0), moveDTO);
        then(moveRepository).should().findAllByDateCreation(localDate);
    }

    @Test
    void whenMovesExist_thenGetALLMoves() {

        List<Move> moves = new ArrayList<>(Arrays.asList(move));

        given(moveRepository.getAll()).willReturn(moves);
        given(modelMapper.map(moves.get(0), MoveDTO.class)).willReturn(moveDTO);

        List<MoveDTO> movesCreatedToday = moveServiceImpl.getMoves();

        assertEquals(movesCreatedToday.get(0), moveDTO);
        then(moveRepository).should().getAll();
    }

    @Test
    void whenMovesExist_thenGetMoveByIdAndUsername() {
        try (MockedStatic<CurrentUserProvider> utilities = Mockito.mockStatic(CurrentUserProvider.class)) {
            utilities.when(CurrentUserProvider::isAdmin).thenReturn(false);
            given(moveRepository.getMoveByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME)).willReturn(move);
            given(modelMapper.map(move, MoveDTO.class)).willReturn(moveDTO);

            MoveDTO returnedMoveDT0 = moveServiceImpl.getMoveDTOByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME);

            assertEquals(returnedMoveDT0, moveDTO);
            then(moveRepository).should().getMoveByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME);
        }
    }

    @Test
    void whenMovesExistAndUserIsAdmin_thenGetMoveByIdAndUsername() {
        try (MockedStatic<CurrentUserProvider> utilities = Mockito.mockStatic(CurrentUserProvider.class)) {
            utilities.when(CurrentUserProvider::isAdmin).thenReturn(true);
            given(moveRepository.get(SAMPLE_ID)).willReturn(moveOpt);
            given(modelMapper.map(move, MoveDTO.class)).willReturn(moveDTO);

            MoveDTO returnedMoveDT0 = moveServiceImpl.getMoveDTOByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME);

            assertEquals(returnedMoveDT0, moveDTO);
        }
    }

    @Test
    void whenMovesExist_thenFailToGetMoveByIdAndUsername() {
        try (MockedStatic<CurrentUserProvider> utilities = Mockito.mockStatic(CurrentUserProvider.class)) {

            utilities.when(CurrentUserProvider::isAdmin).thenReturn(false);
            given(moveRepository.getMoveByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME))
                    .willThrow(new MoveNotFoundException(SAMPLE_ID));

            MoveNotFoundException moveNotFoundException =
                    assertThrows(MoveNotFoundException.class,
                            () -> moveServiceImpl.getMoveDTOByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME));

            assertEquals("move with id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
            then(moveRepository).should().getMoveByIdAndUsername(SAMPLE_ID, SAMPLE_USERNAME);
        }
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

    @Test
    void whenMoveNotExist_thenFailToUpdateMove() {
        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));

        MoveNotFoundException moveNotFoundException =
                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.update(moveDTO));

        assertEquals("move with id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
        then(moveRepository).should().get(SAMPLE_ID);
    }

    @Test
    void whenUserIsNotAuthorized_thenFailToValidateMoveUpdate() {
        doThrow(new ForbiddenException(SAMPLE_USERNAME)).when(userService).verifyUser(SAMPLE_USERNAME);

        try{
            moveServiceImpl.validateMoveUpdate(moveDTO);
            fail();
        }catch(ForbiddenException e){
            assertEquals("user: userName1Test is not authorized to perform this operation", e.getMessage());
        }
    }

    @Test
    void whenMoveHasNoId_thenFailToValidateMoveUpdate() {
        try (MockedStatic<ObjectUtils> utilities = Mockito.mockStatic(ObjectUtils.class)) {
            utilities.when(() -> ObjectUtils.isEmpty(anyLong())).thenReturn(true);

            BadRequestException badRequestException
                = assertThrows(BadRequestException.class, () -> moveServiceImpl.validateMoveUpdate(moveDTO));

        assertEquals("Invalid Move: Lack of primary id", badRequestException.getMessage());
        }
    }

    @Test
    void whenMoveExists_thenDeleteMove() {
        given(moveRepository.get(SAMPLE_ID)).willReturn(Optional.of(move));

        moveServiceImpl.delete(SAMPLE_ID);

        then(moveRepository).should().delete(move);
    }

    @Test
    void whenMoveNotExist_thenFailToDeleteMove() {
        given(moveRepository.get(SAMPLE_ID)).willThrow(new MoveNotFoundException(SAMPLE_ID));

        MoveNotFoundException moveNotFoundException =
                assertThrows(MoveNotFoundException.class, () -> moveServiceImpl.delete(SAMPLE_ID));

        assertEquals("move with id: " + SAMPLE_ID + " not found", moveNotFoundException.getMessage());
        then(moveRepository).should().get(SAMPLE_ID);
    }

}
