package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedPostResponseDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.entity.Product;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private FollowerRepository followerRepository;

    @InjectMocks
    private PostService postService;

    private Integer userId;
    private List<Follower> followers;
    private List<Post> posts;
    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        userId = 1;

        Follower follower1 = new Follower();
        follower1.setFollowerId(1);
        follower1.setUserFollowerId(userId);
        follower1.setUserToFollowId(2);
        follower1.setCreatedAt(LocalDateTime.now());

        Follower follower2 = new Follower();
        follower2.setFollowerId(2);
        follower2.setUserFollowerId(userId);
        follower2.setUserToFollowId(3);
        follower2.setCreatedAt(LocalDateTime.now());

        followers = Arrays.asList(follower1, follower2);

        Product product1 = new Product();
        product1.setProductId(1);
        product1.setProductName("Produto 1");
        product1.setType("Tipo 1");
        product1.setBrand("Marca 1");
        product1.setColor("Cor 1");
        product1.setNotes("Notas 1");

        Product product2 = new Product();
        product2.setProductId(2);
        product2.setProductName("Produto 2");
        product2.setType("Tipo 2");
        product2.setBrand("Marca 2");
        product2.setColor("Cor 2");
        product2.setNotes("Notas 2");

        post1 = new Post();
        post1.setPostId(1);
        post1.setUserId(2);
        post1.setDate(LocalDate.now().minusDays(1));
        post1.setProduct(product1);
        post1.setCategory(1);
        post1.setPrice(100.0);

        post2 = new Post();
        post2.setPostId(2);
        post2.setUserId(3);
        post2.setDate(LocalDate.now().minusDays(3));
        post2.setProduct(product2);
        post2.setCategory(2);
        post2.setPrice(200.0);

        posts = Arrays.asList(post1, post2);
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithValidUserIdAndNoOrder_ShouldReturnPosts() {
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(followers);

        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> postsPage = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class))).thenReturn(postsPage);
        FollowedPostResponseDTO result = postService.getFollowedPostsLastTwoWeeks(userId, null, pageable);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());

        assertNotNull(result.getPosts());
        assertEquals(2, result.getPosts().getContent().size());
        assertEquals(post1.getPostId(), result.getPosts().getContent().get(0).getPostId());
        assertEquals(post2.getPostId(), result.getPosts().getContent().get(1).getPostId());

        verify(followerRepository, times(1)).findByUserFollowerId(userId);
        verify(postRepository, times(1)).findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithNullUserId_ShouldThrowException() {
        Pageable pageable = PageRequest.of(0, 5);
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> postService.getFollowedPostsLastTwoWeeks(null, null, pageable));

        assertEquals("User ID is required", exception.getMessage());

        verify(followerRepository, never()).findByUserFollowerId(anyInt());
        verify(postRepository, never()).findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> postService.getFollowedPostsLastTwoWeeks(0, null, PageRequest.of(0, 5)));

        assertEquals("User ID is required", exception.getMessage());

        verify(followerRepository, never()).findByUserFollowerId(anyInt());
        verify(postRepository, never()).findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithNoFollowed_ShouldReturnEmptyList() {
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(Collections.emptyList());

        FollowedPostResponseDTO result = postService.getFollowedPostsLastTwoWeeks(userId, null, PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertTrue(result.getPosts().isEmpty());

        verify(followerRepository, times(1)).findByUserFollowerId(userId);
        verify(postRepository, never()).findByUserIdInAndDateGreaterThanEqualOrderByDateDesc(
                anyList(), any(LocalDate.class));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithInvalidOrder_ShouldThrowException() {
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(followers);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> postService.getFollowedPostsLastTwoWeeks(userId, "invalid_order", PageRequest.of(0, 5)));

        assertEquals("Invalid order parameter. Use 'date_asc' or 'date_desc'.", exception.getMessage());

        verify(followerRepository, times(1)).findByUserFollowerId(userId);
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithValidUserIdAndDateAscOrder_ShouldReturnOrderedPosts() {
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(followers);

        Pageable pageable = PageRequest.of(0, 5);
        List<Post> ascPosts = Arrays.asList(post2, post1);
        Page<Post> postsPage = new PageImpl<>(ascPosts, pageable, ascPosts.size());

        when(postRepository.findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class))).thenReturn(postsPage);

        FollowedPostResponseDTO result = postService.getFollowedPostsLastTwoWeeks(userId, "date_asc", pageable);

        assertNotNull(result.getPosts());
        assertEquals(2, result.getPosts().getContent().size());
        // Em ordem ascendente, post2 (mais antigo) deve vir primeiro
        assertEquals(post2.getPostId(), result.getPosts().getContent().get(0).getPostId());
        assertEquals(post1.getPostId(), result.getPosts().getContent().get(1).getPostId());

        verify(followerRepository, times(1)).findByUserFollowerId(userId);
        verify(postRepository, times(1)).findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void getFollowedPostsLastTwoWeeks_WithValidUserIdAndDateDescOrder_ShouldReturnOrderedPosts() {
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(followers);
        Pageable pageable = PageRequest.of(0, 5);
        Page<Post> postsPage = new PageImpl<>(posts, pageable, posts.size());
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class))).thenReturn(postsPage);

        FollowedPostResponseDTO result = postService.getFollowedPostsLastTwoWeeks(userId, "date_desc", PageRequest.of(0, 5));

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(2, result.getPosts().getContent().size());
        // Em ordem descendente, post1 (mais recente) deve vir primeiro
        assertEquals(post1.getPostId(), result.getPosts().getContent().get(0).getPostId());
        assertEquals(post2.getPostId(), result.getPosts().getContent().get(1).getPostId());

        verify(followerRepository, times(1)).findByUserFollowerId(userId);
        verify(postRepository, times(1)).findByUserIdInAndDateGreaterThanEqual(
                anyList(), any(LocalDate.class), any(Pageable.class));
    }
}