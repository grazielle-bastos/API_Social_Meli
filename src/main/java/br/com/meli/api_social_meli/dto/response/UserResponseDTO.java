package br.com.meli.api_social_meli.dto.response;

import br.com.meli.api_social_meli.entity.User;

public class UserResponseDTO {
    private Integer userId;
    private String userName;

    public UserResponseDTO(User user) {
        this.userId = user.getUserId();
        this.userName = user.getUserName();
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }
}
