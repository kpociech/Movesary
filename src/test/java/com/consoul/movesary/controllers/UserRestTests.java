package com.consoul.movesary.controllers;

import com.consoul.movesary.controllers.rest.UserRest;
import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.MoveWithoutUser;
import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.dtos.UserWithMoves;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.UserNotFoundException;
import com.consoul.movesary.exceptions.UsersCustomExceptionHandler;
import com.consoul.movesary.services.MoveService;
import com.consoul.movesary.services.UserService;
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

import java.io.InputStream;
import java.util.*;

import static com.consoul.movesary.utils.Utils.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserRestTests {

    @Mock
    UserService userService;

    @Mock
    MoveService moveService;

    @InjectMocks
    UserRest userRest;

    MockMvc mockMvc;

    UserDTO userDTO;
    List<UserDTO> list;

    @BeforeEach
    void setUp() {

        userDTO = new UserDTO(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL);

        list = new ArrayList<>();
        list.add(userDTO);

        mockMvc = MockMvcBuilders.standaloneSetup(userRest).setControllerAdvice(new UsersCustomExceptionHandler()).build();
    }

    @Test
    void whenUserExists_thenReturnUserByUsername() throws Exception {
        // given
        given(userService.getUserDTObyUsername(userDTO.getUsername())).willReturn(userDTO);

        // when
        mockMvc.perform(get("/api/users/" + userDTO.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(SAMPLE_USERNAME)))
                .andExpect(jsonPath("$.fullName", is(SAMPLE_FULLNAME)));

        //then
        then(userService).should().getUserDTObyUsername(userDTO.getUsername());

    }

    @Test
    void whenUserNotExist_thenReturnStatusNotFound() throws Exception {

        String notExistingUser = "I-do-not-exist";

        given(userService.getUserDTObyUsername(notExistingUser)).willThrow(new UserNotFoundException(notExistingUser));

                mockMvc.perform(get("/api/users/" + notExistingUser)
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals(notExistingUser + " not found", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).should().getUserDTObyUsername(notExistingUser);
    }

    @Test
    void whenUsersExist_thenReturnListOfUsers() throws Exception {

        given(userService.getAllUsersDTO()).willReturn(list);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(userDTO.getUsername())));

        then(userService).should().getAllUsersDTO();
    }


    @Test
    void whenUsersNotExist_thenReturnEmptyList() throws Exception {

        given(userService.getAllUsersDTO()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        then(userService).should().getAllUsersDTO();
    }

    @Test
    void whenUserHasMoves_thenReturnUserWithMoves() throws Exception {

        MoveWithoutUser moveWithoutUser = new MoveWithoutUser(1L, "Olliesss", "basic move", SAMPLE_VIDEOO_URL);

        //MoveDTO moveDTO = new MoveDTO(1L, "Olliesss", "descriptionOfMove", userDTO);
        MoveDTO moveDTO = new MoveDTO(1L, "Olliesss", "descriptionOfMove", SAMPLE_VIDEOO_URL, userDTO);
        UserWithMoves userWithMoves =
                new UserWithMoves(SAMPLE_USERNAME, SAMPLE_FULLNAME, SAMPLE_USER_MAIL, Arrays.asList(moveWithoutUser));

        given(userService.getUserWithMoves(SAMPLE_USERNAME)).willReturn(userWithMoves);

        mockMvc.perform(get("/api/users/" + SAMPLE_USERNAME + "/moves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(SAMPLE_USERNAME)))
                .andExpect(jsonPath("$.fullName", is(SAMPLE_FULLNAME)))
                .andExpect(jsonPath("$.moves", hasSize(1)))
                .andExpect(jsonPath("$.moves[0].name", is("Olliesss")));

        then(userService).should().getUserWithMoves(SAMPLE_USERNAME);
    }


    @Test
    void whenUserNotExist_thenFailToReturnUserWithMoves() throws Exception {
        given(userService.getUserWithMoves(SAMPLE_USERNAME)).willThrow(new UserNotFoundException(SAMPLE_USERNAME));

        mockMvc.perform(get("/api/users/" + SAMPLE_USERNAME + "/moves")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals(SAMPLE_USERNAME + " not found", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).should().getUserWithMoves(SAMPLE_USERNAME);
    }

    @Test
    void whenUserExists_thenReturnUserWithMostMoves() throws Exception {
        // given
        given(userService.getUserWithMostMoves()).willReturn(userDTO);

        // when
        mockMvc.perform(get("/api/users/most-skilled"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(userDTO.getUsername())))
                .andExpect(jsonPath("$.fullName", is(SAMPLE_FULLNAME)));

        // then
        then(userService).should().getUserWithMostMoves();
    }

    @Test
    void whenUserNotExist_thenCreateUser() throws Exception {

        given(userService.create(ArgumentMatchers.any(UserDTO.class))).willReturn(userDTO);

        mockMvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(SAMPLE_USERNAME)))
                .andExpect(jsonPath("$.fullName", is(SAMPLE_FULLNAME)))
                .andExpect(jsonPath("$.email", is(SAMPLE_USER_MAIL)));

        then(userService).should().create(ArgumentMatchers.any(UserDTO.class));
    }


    @Test
    void whenUserNotExist_thenCreateUserFromJsonFile() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        InputStream is = UserDTO.class.getResourceAsStream("/jsonSample/JsonUserDTO.json");
        UserDTO readValueFromJson = mapper.readValue(is, UserDTO.class);

        given(userService.create(ArgumentMatchers.any(UserDTO.class))).willReturn(readValueFromJson);

        mockMvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(readValueFromJson))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(readValueFromJson.getUsername())))
                .andExpect(jsonPath("$.fullName", is(readValueFromJson.getFullName())))
                .andExpect(jsonPath("$.email", is(readValueFromJson.getEmail())));

        then(userService).should().create(ArgumentMatchers.any(UserDTO.class));
    }

    @Test
    void whenUserAlreadyExists_thenFailToCreateUser() throws Exception {

        given(userService.create(ArgumentMatchers.any(UserDTO.class))).willThrow(new BadRequestException("this user already exists"));

        mockMvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("this user already exists", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).should().create(ArgumentMatchers.any(UserDTO.class));
    }

    @Test
    void whenPassedBodyToCreateUserMethodHasNoUsername_thenFailToCreateUser() throws Exception {
        UserDTO userDTOInvalid = new UserDTO();

        mockMvc.perform(post("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTOInvalid))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Invalid username: null", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void whenPassedBodyToUpdateUserMethodHasNoUsername_thenFailToUpdateUser() throws Exception {
        UserDTO userDTOInvalid = new UserDTO();

        mockMvc.perform(put("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTOInvalid))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
                .andExpect(result -> assertEquals("Invalid username: null", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).shouldHaveNoInteractions();
    }

    @Test
    void whenUserExists_thenUpdateUser() throws Exception {

        given(userService.update(ArgumentMatchers.any(UserDTO.class))).willReturn(userDTO);

        mockMvc.perform(put("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(SAMPLE_USERNAME)))
                .andExpect(jsonPath("$.fullName", is(SAMPLE_FULLNAME)))
                .andExpect(jsonPath("$.email", is(SAMPLE_USER_MAIL)));

        then(userService).should().update(ArgumentMatchers.any(UserDTO.class));
    }

    @Test
    void whenUserNotExists_thenFailToUpdateUser() throws Exception {
        given(userService.update(ArgumentMatchers.any(UserDTO.class))).willThrow(new UserNotFoundException(userDTO.getUsername()));

        mockMvc.perform(put("/api/users")
                .content(new ObjectMapper().writeValueAsString(userDTO))
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UserNotFoundException))
                .andExpect(result -> assertEquals(userDTO.getUsername() + " not found", Objects.isNull(result.getResolvedException()) ? "" : result.getResolvedException().getMessage()));

        then(userService).should().update(ArgumentMatchers.any(UserDTO.class));
    }

    @Test
    void whenUserExists_thenDeleteUser() throws Exception {

        mockMvc.perform(delete("/api/users/username1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(userService).should().delete("username1");
    }

    @Test
    void whenUserNotExist_thenFailToDeleteUser() throws Exception {

        willThrow(new UserNotFoundException("username1")).given(userService).delete("username1");

        mockMvc.perform(delete("/api/users/username1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        then(userService).should().delete("username1");
    }

}
