package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.request.PromoPostRequestDTO;
import br.com.meli.api_social_meli.dto.request.PublishPostRequestDTO;
import br.com.meli.api_social_meli.dto.response.FollowedPostResponseDTO;
import br.com.meli.api_social_meli.dto.response.PostResponseDTO;
import br.com.meli.api_social_meli.dto.response.PromoPostResponseDTO;
import br.com.meli.api_social_meli.dto.response.PromoProductsCountDTO;
import br.com.meli.api_social_meli.entity.Post;
import br.com.meli.api_social_meli.service.PostService;
import br.com.meli.api_social_meli.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@Tag(
        name = "Products",
        description = "Operações relacionadas a posts e produtos (publicação e listagem de posts de seguidos)."
)
public class ProductController {

    private final ProductService productService;

    private final PostService postService;

    public ProductController(ProductService productService, PostService postService) {
        this.productService = productService;
        this.postService = postService;
    }

    @Operation(
            summary = "Registra uma nova publicação (post de produto)",
            description = "Cria um post para o seller informado no payload."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    })
    @PostMapping("/publish")
    public ResponseEntity<PostResponseDTO> publishPost(@Valid @RequestBody PublishPostRequestDTO publishPostRequestDTO) {
        Post createdPost = productService.publishPost(publishPostRequestDTO);
        PostResponseDTO responseDTO = PostResponseDTO.fromEntity(createdPost);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(
            summary = "Lista posts de vendedores seguidos nas últimas duas semanas",
            description = "Retorna os posts publicados pelos fornecedores que o usuário segue nas últimas duas semanas. Ordenação opcional por data: 'date_asc' (crescente, mais antiga primeiro) ou 'date_desc' (decrescente, mais recente primeiro). Padrão: date_desc."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowedPostResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Parâmetro userId inválido ou order inválido", content = @Content)
    })
    @Parameters({
            @Parameter(name = "userId", description = "ID do usuário que segue fornecedores", example = "4698", required = true),
            @Parameter(name = "order", description = "Ordenação por data: 'date_asc' ou 'date_desc'. Opcional.", example = "date_desc", required = false)
    })
    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowedPostResponseDTO> getFollowedPosts(
            @PathVariable Integer userId,
            @RequestParam(value = "order", required = false) String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        FollowedPostResponseDTO response =
                postService.getFollowedPostsLastTwoWeeks(userId, order, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Registra uma nova publicação promocional",
            description = "Cria um post promocional para o seller informado no payload, com desconto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post promocional criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PromoPostResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    })
    @PostMapping("/promo-pub")
    public ResponseEntity<PromoPostResponseDTO> publishPromoPost(@Valid @RequestBody PromoPostRequestDTO promoPostRequestDTO) {
        Post createdPost = productService.publishPromoPost(promoPostRequestDTO);
        PromoPostResponseDTO responseDTO = PromoPostResponseDTO.fromEntity(createdPost);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(
            summary = "Conta produtos promocionais de um vendedor",
            description = "Retorna a quantidade de produtos em promoção para um determinado vendedor."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contagem realizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PromoProductsCountDTO.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/promo-pub/count")
    public ResponseEntity<PromoProductsCountDTO> getPromoProductsCount(@RequestParam(name = "user_id") Integer userId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getPromoProductsCount(userId));
    }

}
