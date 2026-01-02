package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.response.FollowedListResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersListResponseDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.service.FollowerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "Follow", description = "Operações relacionadas a seguidores")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @PostMapping("/{userId}/follow/{userToFollowId}")
    public ResponseEntity<Follower> follow(@PathVariable Integer userId, @PathVariable Integer userToFollowId) {
        Follower createdFollower = followerService.follow(userId, userToFollowId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFollower);
    }

    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountResponseDTO> getFollowersCount(@PathVariable Integer userId) {
        FollowersCountResponseDTO followersCountResponseDTO = followerService.getFollowersCount(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followersCountResponseDTO);
    }

    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<FollowersListResponseDTO> getFollowersList(@PathVariable Integer userId) {
        FollowersListResponseDTO followersListResponseDTO = followerService.getFollowersList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followersListResponseDTO);
    }

    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<FollowedListResponseDTO> getFollowedList(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(followerService.getFollowedList(userId));
    }

}
