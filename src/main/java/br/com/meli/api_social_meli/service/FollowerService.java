package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedListResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersListResponseDTO;
import br.com.meli.api_social_meli.dto.response.UserSummaryDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.exception.ConflictException;
import br.com.meli.api_social_meli.exception.ResourceNotFoundException;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public FollowersListResponseDTO getFollowersList(Integer userId, String order) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Follower> relations = followerRepository.findByUserToFollowId(userId);

        List<UserSummaryDTO> followers = new ArrayList<>();

        for (Follower relation : relations) {
            Integer followerId = relation.getUserFollowerId();
            User followerUser = userRepository.findById(followerId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
            followers.add(new UserSummaryDTO(followerUser.getUserId(), followerUser.getUserName()));
        }

        Comparator<UserSummaryDTO> comparator = buildComparator(order);
        if (comparator == null) {
            throw new BadRequestException("Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }
        followers.sort(comparator);
        return new FollowersListResponseDTO(seller.getUserId(), seller.getUserName(), followers);
    }

    public FollowedListResponseDTO getFollowedList(Integer userId, String order) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Follower> relations = followerRepository.findByUserFollowerId(userId);

        List<UserSummaryDTO> followed = new ArrayList<>();

        for (Follower relation : relations) {
            Integer followedId = relation.getUserToFollowId();
            User followedUser = userRepository.findById(followedId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", followedId));
            followed.add(new UserSummaryDTO(followedUser.getUserId(), followedUser.getUserName()));
        }

        Comparator<UserSummaryDTO> comparator = buildComparator(order);
        if (comparator == null) {
            throw new BadRequestException("Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }
        followed.sort(comparator);

        return new FollowedListResponseDTO(user.getUserId(), user.getUserName(), followed);
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

    private Comparator<UserSummaryDTO> buildComparator(String order) {
        String normalizedOrder = normalizeOrder(order);
        if (normalizedOrder == null) {
            return null;
        }

        Comparator<UserSummaryDTO> comparator =
                Comparator.comparing(UserSummaryDTO::getUserName, String.CASE_INSENSITIVE_ORDER);

        return normalizedOrder.equals("name_desc") ? comparator.reversed() : comparator;
    }

    private String normalizeOrder(String order) {
        if (order == null) {
            return null;
        }
        if ("name_asc".equals(order) || "name_desc".equals(order)) {
            return order;
        }
        return null;
    }
}

