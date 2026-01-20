package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.request.UserCreateRequestDTO;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.exception.ResourceNotFoundException;
import br.com.meli.api_social_meli.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.stereotype.Service;

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
            throw new BadRequestException("User ID is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    @Operation(summary = "Create a new user")
    public User userCreate(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null || userCreateRequestDTO.getUserName() == null || userCreateRequestDTO.getUserName().isBlank()) {
            throw new BadRequestException("User name is required");

        }
        User user = new User();
        user.setUserName(userCreateRequestDTO.getUserName());

        return userRepository.save(user);
    }

}
