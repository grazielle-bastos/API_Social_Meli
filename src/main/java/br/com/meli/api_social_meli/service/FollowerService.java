package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.dto.response.UserSummaryDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.exception.ConflictException;
import br.com.meli.api_social_meli.exception.ResourceNotFoundException;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
            throw new BadRequestException("User ID is required");
        }
        if (userId.equals(userToFollowId)) {
            throw new BadRequestException("User cannot follow itself");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!userRepository.existsById(userToFollowId)) {
            throw new ResourceNotFoundException("User to follow", "id", userToFollowId);
        }
        if (followerRepository.existsByUserFollowerIdAndUserToFollowId(userId, userToFollowId)) {
            throw new ConflictException("User already follows this user");
        }
        Follower follower = new Follower();
        follower.setUserFollowerId(userId);
        follower.setUserToFollowId(userToFollowId);
        follower.setCreatedAt(LocalDateTime.now());
        return followerRepository.save(follower);
    }

    public FollowersCountResponseDTO getFollowersCount(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        long count = followerRepository.countByUserToFollowId(userId);

        return new FollowersCountResponseDTO(user.getUserId(), user.getUserName(), (int) count);
    }

    public Page<UserSummaryDTO> getFollowersList        (
            Integer userId,
            String order,
            Pageable pageable) {

        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Sort sort;
        if (order == null || order.isBlank() || order.equals("name_asc")) {
            sort = Sort.by("userFollowerId").ascending();
        } else if (order.equals("name_desc")) {
            sort = Sort.by("userFollowerId").descending();
        } else {
            throw new BadRequestException("Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }

        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Follower> followersPage =
                followerRepository.findByUserToFollowId(userId, pageRequest);

        return followersPage.map(relation -> {
            Integer followerId = relation.getUserFollowerId();

            User followerUser = userRepository.findById(followerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));

            return new UserSummaryDTO(
                    followerUser.getUserId(),
                    followerUser.getUserName()
            );
        });
    }

    public Page<UserSummaryDTO> getFollowedList(
            Integer userId,
            String order,
            Pageable pageable) {

        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Sort sort;
        if (order == null || order.isBlank() || order.equals("name_asc")) {
            sort = Sort.by("userToFollowId").ascending();
        } else if (order.equals("name_desc")) {
            sort = Sort.by("userToFollowId").descending();
        } else {
            throw new BadRequestException("Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }

        Pageable pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Follower> followedPage = followerRepository.findByUserFollowerId(userId, pageRequest);

        return followedPage.map(relation -> {
            Integer followedId = relation.getUserToFollowId();

            User followedUser = userRepository.findById(followedId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", followedId));

            return new UserSummaryDTO(
                    followedUser.getUserId(),
                    followedUser.getUserName()
            );
        });

        }

    public void unfollow(Integer userId, Integer userIdToUnfollow) {
        if (userId == null || userId <= 0 || userIdToUnfollow == null || userIdToUnfollow <= 0) {
            throw new BadRequestException("User ID is required");
        }
        if (userId.equals(userIdToUnfollow)) {
            throw new BadRequestException("User cannot unfollow itself");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        if (!userRepository.existsById(userIdToUnfollow)) {
            throw new ResourceNotFoundException("User to unfollow", "id", userIdToUnfollow);
        }

        Follower relation = followerRepository
                .findByUserFollowerIdAndUserToFollowId(userId, userIdToUnfollow)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relationship", "between users", userId + " and " + userIdToUnfollow));

        followerRepository.delete(relation);
    }

}

