package br.com.meli.api_social_meli.dto;

import br.com.meli.api_social_meli.entity.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class PublishPostRequestDTO {

    @NotNull
    @Positive
    private Integer userId;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    @Valid
    @NotNull
    private ProductRequestDTO product;

    @NotNull
    private Integer category;

    @NotNull
    @Positive
    private Double price;

    public PublishPostRequestDTO() {
    }

    public PublishPostRequestDTO(Integer userId, LocalDate date, ProductRequestDTO product, Integer category, Double price) {
        this.userId = userId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ProductRequestDTO getProduct() {
        return product;
    }

    public void setProduct(ProductRequestDTO product) {
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

}
