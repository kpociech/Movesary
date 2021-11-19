package com.consoul.movesary.controllers.rest;


import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.MoveWithoutUser;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.ForbiddenException;
import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.security.CurrentUserProvider;
import com.consoul.movesary.services.MoveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/moves")
public class MoveRest {

    final private MoveService moveService;
    final private UserRest userRest;
    private String keycloakUsername;

    public MoveRest(MoveService moveService, UserRest userRest) {
        this.moveService = moveService;
        this.userRest = userRest;
    }

    @GetMapping("/today")
    public List<MoveDTO> getMovesCreatedToday() {
        return moveService.getMovesCreatedToday();
    }

    @GetMapping
    public List<MoveDTO> getMoves(){
        return moveService.getMoves();
    }

    @RolesAllowed("user")
    @GetMapping("{id}")
    public MoveDTO getMoveById(@PathVariable Long id) {
        keycloakUsername = CurrentUserProvider.getCurrentUser().getUsername();
        return moveService.getMoveDTOByIdAndUsername(id, keycloakUsername);
    }

    @GetMapping("/by-date/{date}")
    public List<MoveDTO> getMoves(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return moveService.findAllByDateCreation(parsedDate);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("Invalid Date: " + date);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RolesAllowed("user")
    @PostMapping
    public MoveDTO create(@RequestBody @Valid MoveDTO moveDTO, BindingResult result) {
        moveService.validateMoveCreation(moveDTO, result);

        return moveService.create(moveDTO);
    }

    @RolesAllowed("user")
    @PutMapping
    public MoveDTO update(@RequestBody MoveDTO moveDTO) {
        keycloakUsername = CurrentUserProvider.getCurrentUser().getUsername();
        moveService.validateMoveUpdate(moveDTO);

        MoveDTO updatedMove = moveService.update(moveDTO);
        log.info("move with id=" + moveDTO.getId() + " was updated by user=" + keycloakUsername);
        return updatedMove;
    }

    @RolesAllowed("user")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        keycloakUsername = CurrentUserProvider.getCurrentUser().getUsername();
        MoveDTO foundMove = getMoveById(id);

        moveService.delete(foundMove.getId());
        log.info("move with id=" + id + " was removed by user=" + keycloakUsername);
    }

}
