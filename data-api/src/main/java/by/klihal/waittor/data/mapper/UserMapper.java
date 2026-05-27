package by.klihal.waittor.data.mapper;

import by.klihal.waittor.common.dto.CreateUserDto;
import by.klihal.waittor.common.dto.UserDto;
import by.klihal.waittor.common.enums.UserRole;
import by.klihal.waittor.data.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "dto", target = "role", qualifiedByName = "fromUserRole")
    User toEntity(CreateUserDto dto);

    @Mapping(source = "entity", target = "role", qualifiedByName = "toUserRole")
    UserDto toDto(User entity);

    @Named("fromUserRole")
    default String fromUserRole(CreateUserDto dto) {
        return dto.role().name();
    }

    @Named("toUserRole")
    default UserRole toUserRole(User entity) {
        return UserRole.valueOf(entity.getRole());
    }
}

