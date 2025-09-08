package ru.practicum.service.admin;

import org.springframework.http.ResponseEntity;
import ru.practicum.PageResponse;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;

public interface AdminUserService {
    ResponseEntity<PageResponse<UserDto>> getUsers(
            List<Long> ids,
            Integer from,
            Integer size);

    UserDto createUser(NewUserRequest dto);

    void removeUser(Long userId);
}