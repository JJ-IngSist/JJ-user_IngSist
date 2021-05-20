package com.edu.austral.ingsis.dtos;

import com.edu.austral.ingsis.utils.Patterns;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
public class CredentialsDTO {

    @Pattern(regexp = Patterns.PATTERN_EMAIL)
    private String email;

    @Pattern(regexp = Patterns.PATTERN_PASSWORD)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
