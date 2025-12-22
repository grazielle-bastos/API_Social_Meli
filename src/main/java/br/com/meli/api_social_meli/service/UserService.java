package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.UserCreateRequestDTO;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Operation(summary = "List all users")
    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Operation(summary = "Get user by ID")
    public User getUserById(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Operation(summary = "Create a new user")
    public User userCreate(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null || userCreateRequestDTO.getUserName() == null || userCreateRequestDTO.getUserName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User name is required");
        }
        User user = new User();
        user.setUserName(userCreateRequestDTO.getUserName());

        return userRepository.save(user);
    }

}
