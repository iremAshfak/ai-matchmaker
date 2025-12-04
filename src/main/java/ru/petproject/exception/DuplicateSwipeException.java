package ru.petproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateSwipeException extends RuntimeException {
    public DuplicateSwipeException(String message) {
        super(message);
    }
}
