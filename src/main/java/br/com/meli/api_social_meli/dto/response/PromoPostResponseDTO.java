package br.com.meli.api_social_meli.dto.response;

import br.com.meli.api_social_meli.entity.Post;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class PromoPostResponseDTO extends PostResponseDTO {
    
    @JsonProperty("has_promo")
    private Boolean hasPromo;
    
    private Double discount;
    
    public PromoPostResponseDTO() {
    }
    
    public PromoPostResponseDTO(Integer userId, Integer postId, LocalDate date, 
                               ProductResponseDTO product, Integer category, 
                               Double price, Boolean hasPromo, Double discount) {
        super(userId, postId, date, product, category, price);
        this.hasPromo = hasPromo;
        this.discount = discount;
    }
    
    public static PromoPostResponseDTO fromEntity(Post post) {
        ProductResponseDTO productDTO = new ProductResponseDTO(
                post.getProduct().getProductId(),
                post.getProduct().getProductName(),
                post.getProduct().getType(),
                post.getProduct().getBrand(),
                post.getProduct().getColor(),
                post.getProduct().getNotes()
        );
        
        return new PromoPostResponseDTO(
                post.getUserId(),
                post.getPostId(),
                post.getDate(),
                productDTO,
                post.getCategory(),
                post.getPrice(),
                post.getHasPromo(),
                post.getDiscount()
        );
    }
    
    public Boolean getHasPromo() {
        return hasPromo;
    }
    
    public Double getDiscount() {
        return discount;
    }
}
