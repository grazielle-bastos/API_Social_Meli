package br.com.meli.api_social_meli.dto.request;

import jakarta.validation.constraints.NotNull;

public class UserCreateRequestDTO {
    @NotNull
    private String userName;

    public UserCreateRequestDTO() {
    }

    public UserCreateRequestDTO(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
