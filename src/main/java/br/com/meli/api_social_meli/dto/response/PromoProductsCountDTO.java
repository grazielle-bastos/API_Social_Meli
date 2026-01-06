package br.com.meli.api_social_meli.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PromoProductsCountDTO {

    @JsonProperty("user_id")
    private Integer userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("promo_products_count")
    private Integer promoProductsCount;

    public PromoProductsCountDTO() {
    }

    public PromoProductsCountDTO(Integer userId, String userName, Integer promoProductsCount) {
        this.userId = userId;
        this.userName = userName;
        this.promoProductsCount = promoProductsCount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getPromoProductsCount() {
        return promoProductsCount;
    }

    public void setPromoProductsCount(Integer promoProductsCount) {
        this.promoProductsCount = promoProductsCount;
    }
}
