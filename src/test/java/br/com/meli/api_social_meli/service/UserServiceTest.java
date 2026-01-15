package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.request.UserCreateRequestDTO;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;
    private List<User> userList;
    private UserCreateRequestDTO validUserDTO;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUserId(1);
        user1.setUserName("Alice");

        user2 = new User();
        user2.setUserId(2);
        user2.setUserName("Bob");

        userList = Arrays.asList(user1, user2);

        validUserDTO = new UserCreateRequestDTO();
        validUserDTO.setUserName("Charlie");
    }

    @Test
    void listAll_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.listAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getUserName());
        assertEquals("Bob", result.get(1).getUserName());

        verify(userRepository, times(1)).findAll();
    }


    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        Integer userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("Alice", result.getUserName());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_WithNullId_ShouldThrowException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.getUserById(null));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(userRepository, never()).findById(any());
    }

    @Test
    void getUserById_WithZeroId_ShouldThrowException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.getUserById(0));

        assertEquals("User ID is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(userRepository, never()).findById(any());
    }


    @Test
    void userCreate_WithValidData_ShouldCreateAndReturnUser() {
        User newUser = new User();
        newUser.setUserName(validUserDTO.getUserName());

        User savedUser = new User();
        savedUser.setUserId(3);
        savedUser.setUserName(validUserDTO.getUserName());

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.userCreate(validUserDTO);

        assertNotNull(result);
        assertEquals(3, result.getUserId());
        assertEquals("Charlie", result.getUserName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void userCreate_WithNullDTO_ShouldThrowException() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.userCreate(null));

        assertEquals("User name is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void userCreate_WithNullUserName_ShouldThrowException() {
        UserCreateRequestDTO invalidDTO = new UserCreateRequestDTO();
        invalidDTO.setUserName(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.userCreate(invalidDTO));

        assertEquals("User name is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void userCreate_WithBlankUserName_ShouldThrowException() {
        UserCreateRequestDTO invalidDTO = new UserCreateRequestDTO();
        invalidDTO.setUserName("   ");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> userService.userCreate(invalidDTO));

        assertEquals("User name is required", exception.getReason());
        assertEquals(400, exception.getStatusCode().value());

        verify(userRepository, never()).save(any(User.class));
    }
}

