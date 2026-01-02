package br.com.meli.api_social_meli.dto.response;

import java.util.List;

public class FollowedListResponseDTO {
    private Integer userId;
    private String userName;
    private List<UserSummaryDTO> followed;

    public FollowedListResponseDTO() {
    }

    public FollowedListResponseDTO(Integer userId, String userName, List<UserSummaryDTO> followed) {
        this.userId = userId;
        this.userName = userName;
        this.followed = followed;
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

    public List<UserSummaryDTO> getFollowed() {
        return followed;
    }

    public void setFollowed(List<UserSummaryDTO> followed) {
        this.followed = followed;
    }
}
