package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedPostResponseDTO;
import br.com.meli.api_social_meli.dto.response.PostResponseDTO;
import br.com.meli.api_social_meli.dto.response.ProductResponseDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.entity.Product;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    private final FollowerRepository followerRepository;

    public PostService(PostRepository postRepository, FollowerRepository followerRepository) {
        this.postRepository = postRepository;
        this.followerRepository = followerRepository;
    }

    public FollowedPostResponseDTO getFollowedPostsLastTwoWeeks(
            Integer userId,
            String order,
            Pageable pageable) {

        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        List<Integer> followedIds = followerRepository.findByUserFollowerId(userId).stream()
                .map(Follower::getUserToFollowId)
                .distinct()
                .toList();

        if (followedIds.isEmpty()) {
            return new FollowedPostResponseDTO(userId, Page.empty(pageable));
        }

        LocalDate startDate = LocalDate.now().minusWeeks(2);

        Sort sort;
        if (order == null || order.isBlank() || order.equals("date_desc")) {
            sort = Sort.by("date").descending();
        } else if (order.equals("date_asc")) {
            sort = Sort.by("date").ascending();
        } else {
            throw new BadRequestException("Invalid order parameter. Use 'date_asc' or 'date_desc'.");
        }

        Pageable pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Post> postsPage = postRepository.findByUserIdInAndDateGreaterThanEqual(
                followedIds,
                startDate,
                pageRequest);

        Page<PostResponseDTO> postDTOPage = postsPage.map(this::mapToPostResponseDTO);

        return new FollowedPostResponseDTO(userId, postDTOPage);
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

}
