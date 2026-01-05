package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedListResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersListResponseDTO;
import br.com.meli.api_social_meli.dto.response.UserSummaryDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FollowerService {
    private static final String USER_NOT_FOUND = "User not found";
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        long count = followerRepository.countByUserToFollowId(userId);

        return new FollowersCountResponseDTO(user.getUserId(), user.getUserName(), (int) count);
    }

    public FollowersListResponseDTO getFollowersList(Integer userId, String order) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        List<Follower> relations = followerRepository.findByUserToFollowId(userId);

        List<UserSummaryDTO> followers = new ArrayList<>();

        for (Follower relation : relations) {
            Integer followerId = relation.getUserFollowerId();
            User followerUser = userRepository.findById(followerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
            followers.add(new UserSummaryDTO(followerUser.getUserId(), followerUser.getUserName()));
        }

        Comparator<UserSummaryDTO> comparator = buildComparator(order);
        if (comparator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }
        followers.sort(comparator);
        return new FollowersListResponseDTO(seller.getUserId(), seller.getUserName(), followers);
    }

    public FollowedListResponseDTO getFollowedList(Integer userId, String order) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));

        List<Follower> relations = followerRepository.findByUserFollowerId(userId);

        List<UserSummaryDTO> followed = new ArrayList<>();

        for (Follower relation : relations) {
            Integer followedId = relation.getUserToFollowId();
            User followedUser = userRepository.findById(followedId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND));
            followed.add(new UserSummaryDTO(followedUser.getUserId(), followedUser.getUserName()));
        }

        Comparator<UserSummaryDTO> comparator = buildComparator(order);
        if (comparator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order parameter. Use 'name_asc' or 'name_desc'.");
        }
        followed.sort(comparator);

        return new FollowedListResponseDTO(user.getUserId(), user.getUserName(), followed);
    }

    public void unfollow(Integer userId, Integer userIdToUnfollow) {
        if (userId == null || userId <= 0 || userIdToUnfollow == null || userIdToUnfollow <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }
        if (userId.equals(userIdToUnfollow)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User cannot unfollow itself");
        }
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        if (!userRepository.existsById(userIdToUnfollow)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User to unfollow not found");
        }

        Follower relation = followerRepository
                .findByUserFollowerIdAndUserToFollowId(userId, userIdToUnfollow)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found"));

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

