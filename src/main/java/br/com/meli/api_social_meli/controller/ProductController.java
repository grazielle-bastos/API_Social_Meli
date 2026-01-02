package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.request.PublishPostRequestDTO;
import br.com.meli.api_social_meli.dto.response.FollowedPostResponseDTO;
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
                            schema = @Schema(implementation = Post.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    })
    @PostMapping("/publish")
    public ResponseEntity<Post> publishPost(@Valid @RequestBody PublishPostRequestDTO publishPostRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.publishPost(publishPostRequestDTO));
    }

    @Operation(
            summary = "Lista posts de vendedores seguidos nas últimas duas semanas",
            description = "Retorna os posts mais recentes (até 2 semanas) publicados pelos fornecedores que o usuário segue, ordenados por data desc."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FollowedPostResponseDTO.class)
                    )),
            @ApiResponse(responseCode = "400", description = "Parâmetro userId inválido", content = @Content)
    })
    @Parameters({
            @Parameter(name = "userId", description = "ID do usuário que segue fornecedores", example = "4698", required = true)
    })
    @GetMapping("/followed/{userId}/list")
    public ResponseEntity<FollowedPostResponseDTO> getFollowedPosts(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getFollowedPostsLastTwoWeeks(userId));
    }

}
