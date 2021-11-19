package com.consoul.movesary.services;

import java.time.LocalDate;
import java.util.List;

import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.models.Move;
import org.springframework.validation.BindingResult;


public interface MoveService {

    List<MoveDTO> getMovesCreatedToday();

    List<MoveDTO> findAllByDateCreation(LocalDate localDate);

    List<MoveDTO> getMovesBasedOnUsername(String username);

    MoveDTO create(MoveDTO moveDTO);

    MoveDTO update(MoveDTO moveDTO);

    void delete(Long id);

    MoveDTO getMoveDTOById(Long id);

    MoveDTO getMoveDTO(Move move);

    List<MoveDTO> getMoves();

    MoveDTO getMoveDTOByIdAndUsername(Long id, String username);

    void validateMoveUpdate(MoveDTO moveDTO);

    void validateMoveCreation(MoveDTO moveDTO, BindingResult result);
}
