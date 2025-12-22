package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class FollowerService {
    private final FollowerRepository followerRepository;

    private final UserRepository userRepository;

    public FollowerService(FollowerRepository followerRepository, UserRepository userRepository) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
    }

    public Follower follow(Integer userId, Integer userToFollowId) {
        if (userId == null || userId <= 0 || userToFollowId == null || userToFollowId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        if (userId.equals(userToFollowId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot follow itself");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        if (!userRepository.existsById(userToFollowId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found");
        }
        if (followerRepository.existsByUserFollowerIdAndUserToFollowId(userId, userToFollowId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already follows this user");
        }
        Follower follower = new Follower();
        follower.setUserFollowerId(userId);
        follower.setUserToFollowId(userToFollowId);
        follower.setCreatedAt(LocalDateTime.now());
        return followerRepository.save(follower);
    }

    public FollowersCountResponseDTO getFollowersCount(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        long count = followerRepository.countByUserToFollowId(userId);

        return new FollowersCountResponseDTO(user.getUserId(), user.getUserName(), (int) count);
    }
}
