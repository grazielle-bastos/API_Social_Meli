package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.PublishPostRequestDTO;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/publish")
    public ResponseEntity<Post> publishPost(@Valid @RequestBody PublishPostRequestDTO publishPostRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.publishPost(publishPostRequestDTO));
    }
}
