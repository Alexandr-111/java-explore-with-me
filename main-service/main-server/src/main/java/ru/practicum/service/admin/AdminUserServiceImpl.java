package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.PageResponse;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;
import ru.practicum.user.NewUserRequest;
import ru.practicum.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.mapper.UserMapper.toUser;
import static ru.practicum.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<PageResponse<UserDto>> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        Page<User> usersPage;

        if (ids != null && !ids.isEmpty()) {
            usersPage = userRepository.findByIdIn(ids, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }

        List<UserDto> userDtos = usersPage.getContent()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        PageResponse<UserDto> pageResponse = PageResponse.<UserDto>builder()
                .content(userDtos)
                .page(usersPage.getNumber())
                .size(usersPage.getSize())
                .totalElements(usersPage.getTotalElements())
                .totalPages(usersPage.getTotalPages())
                .build();

        return ResponseEntity.ok(pageResponse);
    }


    @Override
    @Transactional
    public UserDto createUser(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже существует");
        }
        User user = toUser(dto);
        User savedUser = userRepository.save(user);

        return toUserDto(savedUser);
    }

    @Override
    @Transactional
    public void removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.delete(user);
    }
}