package com.consoul.movesary.controllers;

import com.consoul.movesary.controllers.rest.MoveRest;
import com.consoul.movesary.controllers.rest.UserRest;
import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.ForbiddenException;
import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.exceptions.UsersCustomExceptionHandler;
import com.consoul.movesary.services.MoveService;
import com.consoul.movesary.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.consoul.movesary.utils.Utils.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MoveRestTests {

    @Mock
    MoveService moveService;

    @Mock
    UserRest userRest;

    @InjectMocks
    MoveRest moveRest;

    MockMvc mockMvc;

    MoveDTO moveDTO;
    UserDTO userDTO;
    List<UserDTO> list;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL);
        moveDTO = new MoveDTO(SAMPLE_ID, SAMPLE_MOVE_NAME, SAMPLE_DESC, SAMPLE_VIDEOO_URL, userDTO);

        list = new ArrayList<>();
        list.add(userDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(moveRest).setControllerAdvice(new UsersCustomExceptionHandler()).build();
    }

    @Test
    void whenMovesExist_thenReturnMovesCreatedToday() throws Exception {

        List<MoveDTO> movesDTO = new ArrayList<>(Arrays.asList(moveDTO));

        given(moveService.getMovesCreatedToday()).willReturn(movesDTO);

        mockMvc.perform(get("/api/moves/today"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is(moveDTO.getName())));

        then(moveService).should().getMovesCreatedToday();
    }

    @Test
    void whenMovesExist_thenReturnAllMoves() throws Exception {

        List<MoveDTO> movesDTO = new ArrayList<>(Arrays.asList(moveDTO));

        given(moveService.getMoves()).willReturn(movesDTO);

        mockMvc.perform(get("/api/moves"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is(moveDTO.getName())));

        then(moveService).should().getMoves();
    }

    @Test
    void whenMoveExists_thenReturnMoveById() throws Exception {
        // given
        given(moveService.getMoveDTOByIdAndUsername(moveDTO.getId(), null)).willReturn(moveDTO);

        // when
        mockMvc.perform(get("/api/moves/" + moveDTO.getId()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(5)))
               .andExpect(jsonPath("$.name", is(SAMPLE_MOVE_NAME)))
               .andExpect(jsonPath("$.description", is(SAMPLE_DESC)))
               .andExpect(jsonPath("$.userDTO.username", is(SAMPLE_USERNAME)));

        //then
        then(moveService).should().getMoveDTOByIdAndUsername(SAMPLE_ID, null);
    }

    @Test
    void whenMoveNotExist_thenReturnStatusNotFound() throws Exception {

        given(moveService.getMoveDTOByIdAndUsername(moveDTO.getId(), null)).willThrow(new MoveNotFoundException(SAMPLE_ID));

        mockMvc.perform(get("/api/moves/" + moveDTO.getId())
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof MoveNotFoundException))
               .andExpect(result -> assertEquals("move with id: " + SAMPLE_ID + " not found", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(moveService).should().getMoveDTOByIdAndUsername(SAMPLE_ID, null);

    }

    @Test
    void whenDateIsValid_thenGetMovesByDate() throws Exception {

        List<MoveDTO> movesDTO = new ArrayList<>(Arrays.asList(moveDTO));
        String date = "2020-12-15";
        LocalDate parsedDate = LocalDate.parse(date);

        given(moveService.findAllByDateCreation(parsedDate)).willReturn(movesDTO);

        mockMvc.perform(get("/api/moves/by-date/" + date))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].name", is(moveDTO.getName())));

        then(moveService).should().findAllByDateCreation(parsedDate);
    }

    @Test
    void whenDateIsInvalid_thenFailToGetMoveByDate() throws Exception {

        String invalidDate = "2020-12-15555";

        mockMvc.perform(get("/api/moves/by-date/" + invalidDate)
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
               .andExpect(result -> assertEquals("Invalid Date: " + invalidDate, Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(moveService).shouldHaveNoInteractions();
    }


    @Test
    void whenMoveIsValid_thenCreateMove() throws Exception {

        MoveDTO moveDTOWithoutId = new MoveDTO();
        moveDTOWithoutId.setName(SAMPLE_MOVE_NAME);
        moveDTOWithoutId.setDescription(SAMPLE_DESC);
        moveDTOWithoutId.setUserDTO(userDTO);

        given(moveService.create(ArgumentMatchers.any(MoveDTO.class))).willReturn(moveDTO);

        mockMvc.perform(post("/api/moves")
                .content(new ObjectMapper().writeValueAsString(moveDTOWithoutId))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id", is(5)))
               .andExpect(jsonPath("$.name", is(SAMPLE_MOVE_NAME)))
               .andExpect(jsonPath("$.description", is(SAMPLE_DESC)))
               .andExpect(jsonPath("$.userDTO.username", is(SAMPLE_USERNAME)));

        then(moveService).should().create(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenMoveHasIdPassed_thenFailToCreateMove() throws Exception {

        doThrow(new BadRequestException("Invalid Move: There should not be primary id passed")).when(moveService).validateMoveCreation(ArgumentMatchers.any(MoveDTO.class), any());

        mockMvc.perform(post("/api/moves")
                .content(new ObjectMapper().writeValueAsString(moveDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
               .andExpect(result -> assertEquals("Invalid Move: There should not be primary id passed", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        verify(moveService, times(1)).validateMoveCreation(ArgumentMatchers.any(MoveDTO.class), any());
        verify(moveService, never()).create(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenMoveExists_thenUpdateMove() throws Exception {
        given(moveService.update(ArgumentMatchers.any(MoveDTO.class))).willReturn(moveDTO);

        mockMvc.perform(put("/api/moves")
                .content(new ObjectMapper().writeValueAsString(moveDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id", is(5)))
               .andExpect(jsonPath("$.name", is(SAMPLE_MOVE_NAME)))
               .andExpect(jsonPath("$.description", is(SAMPLE_DESC)))
               .andExpect(jsonPath("$.userDTO.username", is(SAMPLE_USERNAME)));

        then(moveService).should().update(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenMoveNotExsist_thenFailToUpdateMove() throws Exception {

        given(moveService.update(ArgumentMatchers.any(MoveDTO.class))).willThrow(new MoveNotFoundException(SAMPLE_ID));

        mockMvc.perform(put("/api/moves/")
                .content(new ObjectMapper().writeValueAsString(moveDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isNotFound())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof MoveNotFoundException))
               .andExpect(result -> assertEquals("move with id: " + SAMPLE_ID + " not found", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(moveService).should().update(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenMoveIdIsEmpty_thenFailToUpdateMove() throws Exception {

        MoveDTO moveDTOWithoutId = new MoveDTO(null, SAMPLE_MOVE_NAME, SAMPLE_DESC, SAMPLE_VIDEOO_URL, userDTO);

        doThrow(new BadRequestException("Invalid Move: Lack of primary id")).when(moveService).validateMoveUpdate(ArgumentMatchers.any(MoveDTO.class));

        mockMvc.perform(put("/api/moves/")
                .content(new ObjectMapper().writeValueAsString(moveDTOWithoutId))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isBadRequest())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
               .andExpect(result -> assertEquals("Invalid Move: Lack of primary id", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        verify(moveService, times(1)).validateMoveUpdate(ArgumentMatchers.any(MoveDTO.class));
        verify(moveService, never()).update(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenUserIsInvalid_thenFailToUpdateMove() throws Exception {

        doThrow(new ForbiddenException(SAMPLE_USERNAME))
                .when(moveService).validateMoveUpdate(ArgumentMatchers.any(MoveDTO.class));

        mockMvc.perform(put("/api/moves/")
                .content(new ObjectMapper().writeValueAsString(moveDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isForbidden())
               .andExpect(result -> assertTrue(result.getResolvedException() instanceof ForbiddenException))
               .andExpect(result -> assertEquals("user: " + SAMPLE_USERNAME +
                       " is not authorized to perform this operation", Objects.isNull(result.getResolvedException()) ?
                       "" : result.getResolvedException().getMessage()));

        verify(moveService, times(1)).validateMoveUpdate(ArgumentMatchers.any(MoveDTO.class));
        verify(moveService, never()).update(ArgumentMatchers.any(MoveDTO.class));
    }

    @Test
    void whenMoveExists_thenDeleteMove() throws Exception {

        given(moveRest.getMoveById(moveDTO.getId())).willReturn(moveDTO);

        mockMvc.perform(delete("/api/moves/5")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(moveService).should().delete(SAMPLE_ID);
    }

}

