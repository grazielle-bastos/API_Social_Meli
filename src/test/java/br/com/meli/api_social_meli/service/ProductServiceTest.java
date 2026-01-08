package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.request.ProductRequestDTO;
import br.com.meli.api_social_meli.dto.request.PromoPostRequestDTO;
import br.com.meli.api_social_meli.dto.request.PublishPostRequestDTO;
import br.com.meli.api_social_meli.dto.response.PromoProductsCountDTO;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.entity.Product;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.repository.PostRepository;
import br.com.meli.api_social_meli.repository.ProductRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;
    private PublishPostRequestDTO validPublishPostRequestDTO;
    private PromoPostRequestDTO validPromoPostRequestDTO;
    private Post savedPost;
    private User user;

    @BeforeEach
    void setUp() {
        ProductRequestDTO productRequestDTO = new ProductRequestDTO();
        productRequestDTO.setProductId(1);
        productRequestDTO.setProductName("Cadeira Gamer");
        productRequestDTO.setType("Gamer");
        productRequestDTO.setBrand("Racer");
        productRequestDTO.setColor("Red & Black");
        productRequestDTO.setNotes("Special Edition");

        validPublishPostRequestDTO = new PublishPostRequestDTO();
        validPublishPostRequestDTO.setUserId(1);
        validPublishPostRequestDTO.setDate(LocalDate.now().plusDays(1));
        validPublishPostRequestDTO.setProduct(productRequestDTO);
        validPublishPostRequestDTO.setCategory(100);
        validPublishPostRequestDTO.setPrice(1500.50);

        validPromoPostRequestDTO = new PromoPostRequestDTO();
        validPromoPostRequestDTO.setUserId(1);
        validPromoPostRequestDTO.setDate(LocalDate.now().plusDays(1));
        validPromoPostRequestDTO.setProduct(productRequestDTO);
        validPromoPostRequestDTO.setCategory(100);
        validPromoPostRequestDTO.setPrice(1500.50);
        validPromoPostRequestDTO.setHasPromo(true);
        validPromoPostRequestDTO.setDiscount(0.25);

        Product product = new Product();
        product.setProductId(1);
        product.setProductName("Cadeira Gamer");
        product.setType("Gamer");
        product.setBrand("Racer");
        product.setColor("Red & Black");
        product.setNotes("Special Edition");

        savedPost = new Post();
        savedPost.setPostId(1);
        savedPost.setUserId(1);
        savedPost.setDate(LocalDate.now().plusDays(1));
        savedPost.setProduct(product);
        savedPost.setCategory(100);
        savedPost.setPrice(1500.50);
        savedPost.setHasPromo(true);
        savedPost.setDiscount(0.25);

        user = new User();
        user.setUserId(1);
        user.setUserName("vendedor1");
    }

    @Test
    void publishPost_WithValidData_ShouldReturnSavedPost() {
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        Post result = productService.publishPost(validPublishPostRequestDTO);

        assertNotNull(result);
        assertEquals(savedPost.getPostId(), result.getPostId());
        assertEquals(savedPost.getUserId(), result.getUserId());
        assertEquals(savedPost.getCategory(), result.getCategory());
        assertEquals(savedPost.getPrice(), result.getPrice());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void publishPost_WithNullUserId_ShouldThrowException() {
        validPublishPostRequestDTO.setUserId(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithZeroUserId_ShouldThrowException() {
        validPublishPostRequestDTO.setUserId(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithPastDate_ShouldThrowException() {
        validPublishPostRequestDTO.setDate(LocalDate.now().minusDays(1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Date is required and must be today or in the future", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithNullDate_ShouldThrowException() {
        validPublishPostRequestDTO.setDate(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Date is required and must be today or in the future", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithNullProduct_ShouldThrowException() {
        validPublishPostRequestDTO.setProduct(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Product is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithNullCategory_ShouldThrowException() {
        validPublishPostRequestDTO.setCategory(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Category is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithZeroCategory_ShouldThrowException() {
        validPublishPostRequestDTO.setCategory(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Category is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithNullPrice_ShouldThrowException() {
        validPublishPostRequestDTO.setPrice(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Price is required and must be greater than 0", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPost_WithZeroPrice_ShouldThrowException() {
        validPublishPostRequestDTO.setPrice(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPost(validPublishPostRequestDTO));

        assertEquals("Price is required and must be greater than 0", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }


    @Test
    void publishPromoPost_WithValidData_ShouldReturnSavedPost() {
        when(postRepository.save(any(Post.class))).thenReturn(savedPost);

        Post result = productService.publishPromoPost(validPromoPostRequestDTO);

        assertNotNull(result);
        assertEquals(savedPost.getPostId(), result.getPostId());
        assertEquals(savedPost.getUserId(), result.getUserId());
        assertEquals(savedPost.getCategory(), result.getCategory());
        assertEquals(savedPost.getPrice(), result.getPrice());
        assertEquals(savedPost.getHasPromo(), result.getHasPromo());
        assertEquals(savedPost.getDiscount(), result.getDiscount());

        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullUserId_ShouldThrowException() {
        validPromoPostRequestDTO.setUserId(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithZeroUserId_ShouldThrowException() {
        validPromoPostRequestDTO.setUserId(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithPastDate_ShouldThrowException() {
        validPromoPostRequestDTO.setDate(LocalDate.now().minusDays(1));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Date is required and must be today or in the future", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullDate_ShouldThrowException() {
        validPromoPostRequestDTO.setDate(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Date is required and must be today or in the future", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullProduct_ShouldThrowException() {
        validPromoPostRequestDTO.setProduct(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Product is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullCategory_ShouldThrowException() {
        validPromoPostRequestDTO.setCategory(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Category is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithZeroCategory_ShouldThrowException() {
        validPromoPostRequestDTO.setCategory(0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Category is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullPrice_ShouldThrowException() {
        validPromoPostRequestDTO.setPrice(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Price is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithZeroPrice_ShouldThrowException() {
        validPromoPostRequestDTO.setPrice(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Price is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullHasPromo_ShouldThrowException() {
        validPromoPostRequestDTO.setHasPromo(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Has Promo is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithNullDiscount_ShouldThrowException() {
        validPromoPostRequestDTO.setDiscount(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Discount is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void publishPromoPost_WithZeroDiscount_ShouldThrowException() {
        validPromoPostRequestDTO.setDiscount(0.0);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.publishPromoPost(validPromoPostRequestDTO));

        assertEquals("Discount is required and must be greater than zero", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void getPromoProductsCount_WithValidUserId_ShouldReturnCount() {
        Integer userId = 1;
        String userName = "vendedor1";
        int expectedCount = 5;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(postRepository.countByUserIdAndHasPromoTrue(userId)).thenReturn(expectedCount);

        PromoProductsCountDTO result = productService.getPromoProductsCount(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(userName, result.getUserName());
        assertEquals(expectedCount, result.getPromoProductsCount());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).countByUserIdAndHasPromoTrue(userId);
    }

    @Test
    void getPromoProductsCount_WithInvalidUserId_ShouldThrowException() {
        Integer userId = 999;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> productService.getPromoProductsCount(userId));

        assertEquals("User not found", exception.getReason());
        assertEquals(404, exception.getStatusCode().value());

        verify(userRepository, times(1)).findById(userId);
        verify(postRepository, never()).countByUserIdAndHasPromoTrue(anyInt());
    }
}
