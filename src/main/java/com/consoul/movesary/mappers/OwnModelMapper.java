package com.consoul.movesary.mappers;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OwnModelMapper{
	@Bean
	public ModelMapper modelMapper() {
        ModelMapper ownModelMapper = new ModelMapper();
        ownModelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        //1. allows not to override the other properties with null values when you update only one property
        //2. allows to pass a body to updateMove method without foreign key(username)
        ownModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return ownModelMapper;
	}





}
