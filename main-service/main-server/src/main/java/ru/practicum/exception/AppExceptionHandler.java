package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@ControllerAdvice
public class AppExceptionHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiError> handleDataNotFoundException(DataNotFoundException ex) {
        log.warn("Выброшено исключение DataNotFoundException: {}", ex.getMessage());

        ApiError apiError = new ApiError();
        apiError.setErrors(List.of(ex.getMessage()));
        apiError.setMessage("Не найден ресурс");
        apiError.setReason("Запрашиваемый ресурс не был найден");
        apiError.setStatus(HttpStatus.NOT_FOUND);
        apiError.setTimestamp(LocalDateTime.now().format(FORMATTER));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String errorMessage = String.format("%s: %s", error.getField(), error.getDefaultMessage());
            log.error("Ошибка валидации поля {}: {}", error.getField(), error.getDefaultMessage());
            fieldErrors.add(errorMessage);
        });

        ApiError apiError = new ApiError();
        apiError.setErrors(fieldErrors);
        apiError.setMessage("Ошибка валидации данных");
        apiError.setReason("Некорректные параметры запроса");
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();

        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            String errorMessage = String.format("Параметр '%s': %s", fieldName, message);

            log.error("Ошибка валидации параметра {}: {}", fieldName, message);
            errors.add(errorMessage);
        });

        ApiError apiError = new ApiError();
        apiError.setErrors(errors);
        apiError.setMessage("Ошибка валидации параметров");
        apiError.setReason("Некорректные значения параметров запроса");
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleAllExceptions(Throwable ex) {
        log.error("Внутренняя ошибка сервера: ", ex);
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage() != null ? ex.getMessage() : "Неизвестная ошибка");

        ApiError apiError = new ApiError();
        apiError.setErrors(errors);
        apiError.setMessage("Ошибка сервера");
        apiError.setReason("Операция не выполнена из-за ошибки на сервере");
        apiError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ApiOperationException.class)
    public ResponseEntity<ApiError> handleApiOperationException(ApiOperationException ex) {
        log.warn("Выброшено исключение ApiOperationException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError();
        apiError.setErrors(errors);
        apiError.setMessage("Ошибка HTTP-запроса при создании объекта");
        apiError.setReason("Ошибка выполнения операции");
        apiError.setStatus(HttpStatus.valueOf(ex.getHttpStatusCode().value()));
        apiError.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.status(ex.getHttpStatusCode()).body(apiError);
    }

    @ExceptionHandler(ServerResponseException.class)
    public ResponseEntity<ApiError> handleServerResponseException(ServerResponseException ex) {
        log.error("Ошибка сервера: {} {}", ex.getMessage(), ex.getStatus());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                errors,
                "Ошибка сервера",
                "Внутренняя ошибка сервера при обработке запроса",
                HttpStatus.valueOf(ex.getStatus().value()),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.status(ex.getStatus()).body(apiError);
    }

    @ExceptionHandler(NetworkException.class)
    public ResponseEntity<ApiError> handleNetworkException(NetworkException ex) {
        log.error("Сетевая ошибка: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                errors,
                "Сетевая ошибка",
                "Ошибка сетевого соединения",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);
    }

    @ExceptionHandler(BadInputException.class)
    public ResponseEntity<ApiError> handleBadInputException(BadInputException ex) {
        log.warn("Выброшено исключение BadInputException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                errors,
                "Некорректный запрос",
                "Проверьте корректность введенных данных",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflictException(ConflictException ex) {
        log.warn("Выброшено исключение ConflictException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());

        ApiError apiError = new ApiError(
                errors,
                ex.getMessage(),
                "Нарушение уникальности данных",
                HttpStatus.CONFLICT,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String parameterName = ex.getParameterName();
        String errorMessage = String.format("Параметр '%s' обязательный и не был передан", parameterName);
        log.warn("Отсутствует обязательный параметр: {}", parameterName);

        ApiError apiError = new ApiError();
        apiError.setErrors(List.of(errorMessage));
        apiError.setMessage("Ошибка валидации параметров запроса");
        apiError.setReason("Некорректные параметры запроса");
        apiError.setStatus(HttpStatus.BAD_REQUEST);
        apiError.setTimestamp(LocalDateTime.now().format(FORMATTER));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
}