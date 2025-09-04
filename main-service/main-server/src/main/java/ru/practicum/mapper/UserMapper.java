package ru.practicum.mapper;

import ru.practicum.model.User;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;
import ru.practicum.user.UserShortDto;

import java.util.Objects;

public class UserMapper {

    public static UserShortDto toUserShortDto(User user) {
        Objects.requireNonNull(user, "Пользователь (User) не должен быть null");
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }


    public static User toUser(NewUserRequest dto) {
        Objects.requireNonNull(dto, "ДТО (NewUserRequest) не должен быть null");
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        Objects.requireNonNull(user, "Пользователь (User) не должен быть null");
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}