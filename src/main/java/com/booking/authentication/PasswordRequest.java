package com.booking.authentication;

import com.booking.validator.PasswordConfirmation;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import lombok.NoArgsConstructor;

@Data
@PasswordConfirmation(
        password = "password",
        confirmPassword = "confirmPassword"
)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRequest {

    private String email;

    @NotEmpty(message = "Password is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*\\d).*$", message = "Password must contain at least one uppercase letter, one lowercase letter, and one number")
    private String password;
    private String confirmPassword;
}