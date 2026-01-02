package br.com.meli.api_social_meli.dto.response;

public class FollowersCountResponseDTO {

    private Integer userId;
    private String userName;
    private Integer followersCount;

    public FollowersCountResponseDTO() {
    }

    public FollowersCountResponseDTO(Integer userId, String userName, Integer followersCount) {
        this.userId = userId;
        this.userName = userName;
        this.followersCount = followersCount;
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

    public Integer getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(Integer followersCount) {
        this.followersCount = followersCount;
    }
}
