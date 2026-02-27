package com.darquran.application.mapper.users.admin;

import com.darquran.application.dto.users.admin.AdminRequest;
import com.darquran.application.dto.users.admin.AdminResponse;
import com.darquran.domain.model.entities.users.Admin;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Admin toEntity(AdminRequest dto);

    AdminResponse toResponse(Admin entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(AdminRequest request, @MappingTarget Admin entity);
}

