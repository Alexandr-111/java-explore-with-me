package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.PageResponse;
import ru.practicum.service.admin.AdminUserService;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.net.URI;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final AdminUserService service;


    @GetMapping
    public ResponseEntity<PageResponse<UserDto>> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("AdminUserController. Получение списка пользователей.");
        return service.getUsers(ids, from, size);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody NewUserRequest dto) {
        log.debug("AdminUserController. Добавление нового пользователя. Получен объект NewUserRequest {}", dto);
        UserDto savedDto = service.createUser(dto);
        log.debug("Пользователь создан с ID: {}", savedDto.getId());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedDto.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUser(@PathVariable Long userId) {
        log.debug("AdminUserController. Удаление пользователя с id {}", userId);
        service.removeUser(userId);
        return ResponseEntity.noContent().build();
    }
}