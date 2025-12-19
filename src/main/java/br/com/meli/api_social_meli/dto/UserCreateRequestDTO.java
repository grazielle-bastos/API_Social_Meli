package br.com.meli.api_social_meli.dto;

public class UserCreateRequestDTO {
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
