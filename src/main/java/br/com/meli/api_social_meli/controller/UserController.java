package br.com.meli.api_social_meli.controller;

import br.com.meli.api_social_meli.dto.request.UserCreateRequestDTO;
import br.com.meli.api_social_meli.dto.response.UserResponseDTO;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Operações relacionadas a usuários")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Lista todos os usuários",
            description = "Retorna todos os usuários cadastrados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserResponseDTO.class))
                    ))
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listAll() {
        List<User> users = userService.listAll();
        List<UserResponseDTO> userDTOs = users.stream()
                .map(user -> new UserResponseDTO(user))
                .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(userDTOs);
    }

    @Operation(
            summary = "Busca usuário pelo ID",
            description = "Retorna os dados do usuário informado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @Parameter(name = "userId", description = "ID do usuário", example = "1", required = true)
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer userId) {
        User user = userService.getUserById(userId);
        UserResponseDTO userDTO = new UserResponseDTO(user);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @Operation(
            summary = "Cria um novo usuário",
            description = "Registra um usuário com o nome fornecido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário criado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Payload inválido", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponseDTO> userCreate(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        User createdUser = userService.userCreate(userCreateRequestDTO);
        UserResponseDTO userDTO = new UserResponseDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }

}