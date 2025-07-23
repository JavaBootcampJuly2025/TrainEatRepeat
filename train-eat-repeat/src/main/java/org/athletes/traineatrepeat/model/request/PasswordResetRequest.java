package org.athletes.traineatrepeat.model.request;

public record PasswordResetRequest(
        String email,
        String password,
        String confirmPassword) {}