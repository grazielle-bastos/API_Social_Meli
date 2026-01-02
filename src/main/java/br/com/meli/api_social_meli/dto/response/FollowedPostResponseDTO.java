package br.com.meli.api_social_meli.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FollowedPostResponseDTO {
    @JsonProperty("user_id")
    private Integer userId;
    private List<PostResponseDTO> posts;

    public FollowedPostResponseDTO() {
    }

    public FollowedPostResponseDTO(Integer userId, List<PostResponseDTO> posts) {
        this.userId = userId;
        this.posts = posts;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<PostResponseDTO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResponseDTO> posts) {
        this.posts = posts;
    }
}
