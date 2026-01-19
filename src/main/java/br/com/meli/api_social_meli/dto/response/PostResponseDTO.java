package br.com.meli.api_social_meli.dto.response;

import br.com.meli.api_social_meli.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class PostResponseDTO {
    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("post_id")
    private Integer postId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private ProductResponseDTO product;

    private Integer category;

    private Double price;

    public PostResponseDTO() {
    }

    public PostResponseDTO(Integer userId, Integer postId, LocalDate date, ProductResponseDTO product, Integer category, Double price) {
        this.userId = userId;
        this.postId = postId;
        this.date = date;
        this.product = product;
        this.category = category;
        this.price = price;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ProductResponseDTO getProduct() {
        return product;
    }

    public void setProduct(ProductResponseDTO product) {
        this.product = product;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public static PostResponseDTO fromEntity(Post post) {
        ProductResponseDTO productDTO = new ProductResponseDTO(
                post.getProduct().getProductId(),
                post.getProduct().getProductName(),
                post.getProduct().getType(),
                post.getProduct().getBrand(),
                post.getProduct().getColor(),
                post.getProduct().getNotes()
        );
        
        return new PostResponseDTO(
                post.getUserId(),
                post.getPostId(),
                post.getDate(),
                productDTO,
                post.getCategory(),
                post.getPrice()
        );
    }
}
