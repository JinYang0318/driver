package com.example.driver.exception;
import lombok.Builder;

@Builder
public record ErrorMessage(String message) {
}
