package com.consoul.movesary.services;

import com.consoul.movesary.dtos.MoveDTO;
import com.consoul.movesary.dtos.MoveWithoutUser;
import com.consoul.movesary.exceptions.BadRequestException;
import com.consoul.movesary.exceptions.ForbiddenException;
import com.consoul.movesary.exceptions.MoveNotFoundException;
import com.consoul.movesary.models.Move;
import com.consoul.movesary.repositories.MoveRepository;
import com.consoul.movesary.security.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MoveServiceImpl implements MoveService {

	private final MoveRepository moveRepository;
    private final ModelMapper modelMapper;
    private final UserService userService;

	public MoveServiceImpl(MoveRepository moveRepository, ModelMapper modelMapper, @Lazy UserService userService) {
		this.moveRepository = moveRepository;
		this.modelMapper = modelMapper;
		this.userService = userService;
	}

	@Override
	public List<MoveDTO> getMovesCreatedToday() {
        LocalDate localDate = LocalDate.now();

        return mapAllToDTO(moveRepository.findAllByDateCreation(localDate));
	}

	@Override
	public List<MoveDTO> getMoves() {
		return mapAllToDTO(moveRepository.getAll());
	}

	@Override
	public MoveDTO getMoveDTOByIdAndUsername(Long id, String username) {
		if(CurrentUserProvider.isAdmin()){
			return getMoveDTOById(id);
		}
		return getMoveDTO(moveRepository.getMoveByIdAndUsername(id, username));
	}

	@Override
	public List<MoveDTO> findAllByDateCreation(LocalDate localDate) {
		return mapAllToDTO(moveRepository.findAllByDateCreation(localDate));
	}

	@Override
	public List<MoveDTO> getMovesBasedOnUsername(String username) {
		return mapAllToDTO(moveRepository.getMovesBasedOnUsername(username));
	}

	@Override
	public MoveDTO create(MoveDTO moveDTO) {
        Move move = modelMapper.map(moveDTO, Move.class);

        move = moveRepository.create(move);

        return getMoveDTO(move);
	}

	@Override
	public MoveDTO update(MoveDTO moveDTO) {

		Move move = getMoveById(moveDTO.getId());

        modelMapper.map(moveDTO, move);
        move = moveRepository.update(move);

        return getMoveDTO(move);
	}

	@Override
	public void delete(Long id) {
		moveRepository.delete(getMoveById(id));
	}

	@Override
	public MoveDTO getMoveDTOById(Long id){
		return getMoveDTO(getMoveById(id));
	}

	@Override
    public MoveDTO getMoveDTO(Move move) {
		return modelMapper.map(move, MoveDTO.class);
    }

	private List<MoveDTO> mapAllToDTO(List<Move> moves) {
        return moves.stream()
                .map(this::getMoveDTO)
                .collect(Collectors.toList());
    }

	private Move getMoveById(Long id) {
		return moveRepository.get(id)
				.orElseThrow(() -> new MoveNotFoundException(id));
	}

	public void validateMoveCreation(MoveDTO moveDTO, BindingResult result) {
		userService.verifyUser(moveDTO.getUserDTO().getUsername());

		if (result.hasErrors()) {
			List<ObjectError> errors = result.getAllErrors();
			errors.forEach(err -> log.info(err.getDefaultMessage()));
			throw new BadRequestException("Invalid Move");
		}
	}

	public void validateMoveUpdate(MoveDTO moveDTO) {
		userService.verifyUser(moveDTO.getUserDTO().getUsername());

		if (ObjectUtils.isEmpty(moveDTO.getId())) {
			throw new BadRequestException("Invalid Move: Lack of primary id");
		}
	}
}