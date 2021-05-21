package com.edu.austral.ingsis.dtos;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChangeUserPasswordDTO {

    private String oldPassword;

    private String password;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
