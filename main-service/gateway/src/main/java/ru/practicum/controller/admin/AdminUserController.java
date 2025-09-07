package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.client.AdminUserClient;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;

@Slf4j
@Controller
@Validated
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserClient client;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @Min(0) @RequestParam(defaultValue = "0") Integer from,
            @Min(1) @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminUserController. Получение списка пользователей.");
        return client.getUsers(ids, from, size);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody NewUserRequest dto) {
        log.debug("AdminUserController. Добавление нового пользователя. Получен объект NewUserRequest {}", dto);
        return client.createUser(dto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@Positive @PathVariable Long userId) {
        log.debug("AdminUserController. Удаление пользователя с id {}", userId);
        return client.removeUser(userId);
    }
}