package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.request.ProductRequestDTO;
import br.com.meli.api_social_meli.dto.request.PublishPostRequestDTO;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.entity.Product;
import br.com.meli.api_social_meli.repository.PostRepository;
import br.com.meli.api_social_meli.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class ProductService {

    private final PostRepository postRepository;
    private final ProductRepository productRepository;

    public ProductService(PostRepository postRepository, ProductRepository productRepository) {
        this.postRepository = postRepository;
        this.productRepository = productRepository;
    }

    public Post publishPost(PublishPostRequestDTO publishPostRequestDTO) {
        if (publishPostRequestDTO.getUserId() == null || publishPostRequestDTO.getUserId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        } else if (publishPostRequestDTO.getDate() == null || publishPostRequestDTO.getDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required and must be today or in the future");
        } else if (publishPostRequestDTO.getProduct() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product is required");
        } else if (publishPostRequestDTO.getCategory() == null || publishPostRequestDTO.getCategory() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is required");
        } else if (publishPostRequestDTO.getPrice() == null || publishPostRequestDTO.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price is required and must be greater than 0");
        }

        ProductRequestDTO productDTO = publishPostRequestDTO.getProduct();

        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setType(productDTO.getType());
        product.setBrand(productDTO.getBrand());
        product.setColor(productDTO.getColor());
        product.setNotes(productDTO.getNotes());

        Post post = new Post();
        post.setUserId(publishPostRequestDTO.getUserId());
        post.setDate(publishPostRequestDTO.getDate());
        post.setProduct(product);
        post.setCategory(publishPostRequestDTO.getCategory());
        post.setPrice(publishPostRequestDTO.getPrice());

        Post saved = postRepository.save(post);
        return saved;
    }
}
