package br.com.meli.api_social_meli.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

public class FollowedPostResponseDTO {
    @JsonProperty("user_id")
    private Integer userId;
    private Page<PostResponseDTO> posts;

    public FollowedPostResponseDTO() {
    }

    public FollowedPostResponseDTO(Integer userId, Page<PostResponseDTO> posts) {
        this.userId = userId;
        this.posts = posts;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Page<PostResponseDTO> getPosts() {
        return posts;
    }

    public void setPosts(Page<PostResponseDTO> posts) {
        this.posts = posts;
    }
}
