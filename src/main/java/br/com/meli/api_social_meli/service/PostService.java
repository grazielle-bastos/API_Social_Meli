package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedPostResponseDTO;
import br.com.meli.api_social_meli.dto.response.PostResponseDTO;
import br.com.meli.api_social_meli.dto.response.ProductResponseDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.entity.Product;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final FollowerRepository followerRepository;

    public PostService(PostRepository postRepository, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    public FollowedPostResponseDTO getFollowedPostsLastTwoWeeks(Integer userId, String order) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        List<Integer> followedIds = followerRepository.findByUserFollowerId(userId).stream()
                .map(Follower::getUserToFollowId)
                .distinct()
                .toList();

        if (followedIds.isEmpty()) {
            return new FollowedPostResponseDTO(userId, Collections.emptyList());
        }

        LocalDate startDate = LocalDate.now().minusWeeks(2);
        List<Post> posts = postRepository.findByUserIdInAndDateGreaterThanEqualOrderByDateDesc(followedIds, startDate);

        List<PostResponseDTO> postDTOs = posts.stream()
                .map(this::mapToPostResponseDTO)
                .toList();

        Comparator<PostResponseDTO> comparator = buildComparator(order);
        if (comparator == null && order != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order parameter. Use 'date_asc' or 'date_desc'.");
        }
        if (comparator != null) {
            postDTOs = postDTOs.stream().sorted(comparator).toList();
        }

        return new FollowedPostResponseDTO(userId, postDTOs);
    }

    private PostResponseDTO mapToPostResponseDTO(Post post) {
        Product product = post.getProduct();
        ProductResponseDTO productResponseDTO = new ProductResponseDTO(
                product.getProductId(),
                product.getProductName(),
                product.getType(),
                product.getBrand(),
                product.getColor(),
                product.getNotes()
        );
        return new PostResponseDTO(
                post.getUserId(),
                post.getPostId(),
                post.getDate(),
                productResponseDTO,
                post.getCategory(),
                post.getPrice()
        );
    }

    private String normalizeOrder(String order) {
        if (order == null) {
            return null;
        }
        if ("date_asc".equals(order) || "date_desc".equals(order)) {
            return order;
        }
        return null;
    }

    private Comparator<PostResponseDTO> buildComparator(String order) {
        String normalizedOrder = normalizeOrder(order);
        if (normalizedOrder == null) {
            return null;
        }

        Comparator<PostResponseDTO> comparator =
                Comparator.comparing(PostResponseDTO::getDate);

        return normalizedOrder.equals("date_desc") ? comparator.reversed() : comparator;
    }
}
