package com.booking.authentication;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRequest {
    @NotEmpty(message = "Token is required")
    @Length(min = 6, max = 6, message = "Token must be 6 characters")
    private String token;
}