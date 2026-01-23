package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.response.*;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.service.FollowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "Follow", description = "Operações relacionadas a seguidores e seguidos")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    @Operation(
            summary = "Permite que um usuário siga outro",
            description = "Cria a relação de follow entre userId (seguidor) e userToFollowId (seguido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Follow criado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FollowResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "IDs inválidos ou usuário tentando seguir a si mesmo", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Follow já existe", content = @Content)
    })
    @Parameters({
            @Parameter(name = "userId", description = "ID do seguidor", example = "1", required = true),
            @Parameter(name = "userToFollowId", description = "ID do usuário que será seguido", example = "2", required = true)
    })
    @PostMapping("/{userId}/follow/{userToFollowId}")
    public ResponseEntity<FollowResponseDTO> follow(
            @PathVariable Integer userId,
            @PathVariable Integer userToFollowId) {
        Follower createdFollower = followerService.follow(userId, userToFollowId);
        FollowResponseDTO followResponseDTO = new FollowResponseDTO(createdFollower);
        return ResponseEntity.status(HttpStatus.CREATED).body(followResponseDTO);
    }

    @Operation(summary = "Contagem de seguidores de um usuário", description = "Retorna a contagem de todos os seguidores do userId informado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FollowersCountResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @Parameter(name = "userId", description = "ID do usuário (seller)", example = "10", required = true)
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<FollowersCountResponseDTO> getFollowersCount(@PathVariable Integer userId) {
        FollowersCountResponseDTO followersCountResponseDTO = followerService.getFollowersCount(userId);
        return ResponseEntity.status(HttpStatus.OK).body(followersCountResponseDTO);
    }

    @Operation(summary = "Lista seguidores de um usuário", description = "Retorna detalhamento de todos os seguidores do userId informado com paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FollowersListResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @Parameter(name = "userId", description = "ID do usuário (seller)", example = "10", required = true)
    @Parameter(name = "order", description = "Ordenação por ID opcional")
    @Parameter(name = "page", description = "Número da página", example = "0")
    @Parameter(name = "size", description = "Tamanho da página", example = "5")
    @GetMapping("/{userId}/followers/list")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowersList(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page <UserSummaryDTO> followersPage = followerService.getFollowersList(userId, order, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(followersPage);
    }

    @Operation(summary = "Lista usuários seguidos", description = "Retorna todos os usuários que o userId segue com paginação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FollowedListResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetro inválido", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @Parameter(name = "userId", description = "ID do usuário seguidor", example = "5", required = true)
    @Parameter(name = "order", description = "Ordenação por ID opcional")
    @Parameter(name = "page", description = "Número da página", example = "0")
    @Parameter(name = "size", description = "Tamanho da página", example = "5")
    @GetMapping("/{userId}/followed/list")
    public ResponseEntity<Page<UserSummaryDTO>> getFollowedList(
            @PathVariable Integer userId,
            @RequestParam(required = false) String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<UserSummaryDTO> followedPage =
                followerService.getFollowedList(userId, order, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(followedPage);
    }

    @Operation(
            summary = "Permite que um usuário deixe de seguir outro",
            description = "Remove a relação de follow entre userId (seguidor) e userIdToUnfollow (seguido)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unfollow realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "IDs inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário ou relacionamento não encontrado", content = @Content)
    })
    @Parameters({
            @Parameter(name = "userId", description = "ID do seguidor", example = "1", required = true),
            @Parameter(name = "userIdToUnfollow", description = "ID do usuário a ser deixado de seguir", example = "2", required = true)
    })
    @PostMapping("/{userId}/unfollow/{userIdToUnfollow}")
    public ResponseEntity<Void> unfollow(
            @PathVariable Integer userId,
            @PathVariable Integer userIdToUnfollow) {
        followerService.unfollow(userId, userIdToUnfollow);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
