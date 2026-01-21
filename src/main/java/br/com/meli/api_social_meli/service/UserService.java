package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.request.UserCreateRequestDTO;
import br.com.meli.api_social_meli.dto.response.UserResponseDTO;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.exception.ResourceNotFoundException;
import br.com.meli.api_social_meli.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponseDTO> listAll(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(UserResponseDTO::new);
    }

    public Page<UserResponseDTO> searchByName(String userName, Pageable pageable){
        Page<User> usersPage;

        if (userName == null || userName.isBlank()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            usersPage = userRepository.findByUserNameContainingIgnoreCase(userName, pageable);
        }

        return usersPage.map(UserResponseDTO::new);
    }

    public User getUserById(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new BadRequestException("User ID is required");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    public User userCreate(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null || userCreateRequestDTO.getUserName() == null || userCreateRequestDTO.getUserName().isBlank()) {
            throw new BadRequestException("User name is required");

        }
        User user = new User();
        user.setUserName(userCreateRequestDTO.getUserName());

        return userRepository.save(user);
    }

}
