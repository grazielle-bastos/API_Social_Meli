package br.com.meli.api_social_meli.dto.response;

import br.com.meli.api_social_meli.entity.Follower;

import java.time.LocalDateTime;

public class FollowResponseDTO {
    private Integer followerId;
    private Integer userFollowerId;
    private Integer userToFollowId;
    private LocalDateTime createdAt;

    public FollowResponseDTO(Follower follower) {
        this.followerId = follower.getFollowerId();
        this.userFollowerId = follower.getUserFollowerId();
        this.userToFollowId = follower.getUserToFollowId();
        this.createdAt = follower.getCreatedAt();
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public Integer getUserFollowerId() {
        return userFollowerId;
    }

    public Integer getUserToFollowId() {
        return userToFollowId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
