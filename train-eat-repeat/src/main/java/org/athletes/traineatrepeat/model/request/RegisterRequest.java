package org.athletes.traineatrepeat.model.request;

public record RegisterRequest(
        String username,
        String email,
        String password,
        String confirmPassword,
        Integer age,
        String gender,
        Float weight,
        Float height) {

    public static RegisterRequest ofInitialRequestForm() {
        return new RegisterRequest("", "", "", "", null, "", null, null);
    }
}