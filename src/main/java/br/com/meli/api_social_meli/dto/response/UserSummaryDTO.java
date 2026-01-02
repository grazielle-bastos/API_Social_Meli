package br.com.meli.api_social_meli.dto.response;

public class UserSummaryDTO {
    private Integer userId;
    private String userName;

    public UserSummaryDTO() {
    }

    public UserSummaryDTO(Integer userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}