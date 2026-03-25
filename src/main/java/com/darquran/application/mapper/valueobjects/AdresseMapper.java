package com.darquran.application.mapper.valueobjects;

import com.darquran.application.dto.valueobjects.AdresseRequest;
import com.darquran.application.dto.valueobjects.AdresseResponse;
import com.darquran.domain.model.valueobjects.Adresse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdresseMapper {

    Adresse toEntity(AdresseRequest dto);

    @Mapping(target = "adresseComplete", expression = "java(entity.adresseComplete())")
    AdresseResponse toResponse(Adresse entity);
}