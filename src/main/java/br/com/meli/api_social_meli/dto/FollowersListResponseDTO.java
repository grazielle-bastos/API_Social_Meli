package br.com.meli.api_social_meli.dto;

import java.util.List;

public class FollowersListResponseDTO {
    private Integer userId;
    private String userName;
    private List<UserSummaryDTO> followers;

    public FollowersListResponseDTO() {
    }

    public FollowersListResponseDTO(Integer userId, String userName, List<UserSummaryDTO> followers) {
        this.userId = userId;
        this.userName = userName;
        this.followers = followers;
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

    public List<UserSummaryDTO> getFollowers() {
        return followers;
    }

    public void setFollowers(List<UserSummaryDTO> followers) {
        this.followers = followers;
    }
}
