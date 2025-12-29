package br.com.meli.api_social_meli.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "follower")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follower_id")
    private Integer followerId;

    @Column(name = "user_follower_id")
    @NotNull
    private Integer userFollowerId;

    @Column(name = "user_to_follow_id")
    @NotNull
    private Integer userToFollowId;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt;

    public Follower() {
    }
    public Follower(Integer followerId, Integer userFollowerId, Integer userToFollowId, LocalDateTime createdAt) {
        this.followerId = followerId;
        this.userFollowerId = userFollowerId;
        this.userToFollowId = userToFollowId;
        this.createdAt = createdAt;
    }

    public Integer getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Integer followerId) {
        this.followerId = followerId;
    }

    public Integer getUserFollowerId() {
        return userFollowerId;
    }

    public void setUserFollowerId(Integer userFollowerId) {
        this.userFollowerId = userFollowerId;
    }

    public Integer getUserToFollowId() {
        return userToFollowId;
    }

    public void setUserToFollowId(Integer userToFollowId) {
        this.userToFollowId = userToFollowId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
