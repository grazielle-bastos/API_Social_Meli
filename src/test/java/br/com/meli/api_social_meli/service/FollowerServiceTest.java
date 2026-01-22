package br.com.meli.api_social_meli.service;

import br.com.meli.api_social_meli.dto.response.FollowedListResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersCountResponseDTO;
import br.com.meli.api_social_meli.dto.response.FollowersListResponseDTO;
import br.com.meli.api_social_meli.dto.response.UserSummaryDTO;
import br.com.meli.api_social_meli.entity.Follower;
import br.com.meli.api_social_meli.entity.User;
import br.com.meli.api_social_meli.exception.BadRequestException;
import br.com.meli.api_social_meli.exception.ConflictException;
import br.com.meli.api_social_meli.exception.ResourceNotFoundException;
import br.com.meli.api_social_meli.repository.FollowerRepository;
import br.com.meli.api_social_meli.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FollowerService followerService;

    private User user1;
    private User user2;
    private Follower followerRelation;
    private List<Follower> followersList;
    private List<Follower> followedList;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setUserId(1);
        user1.setUserName("User 1");

        user2 = new User();
        user2.setUserId(2);
        user2.setUserName("User 2");

        followerRelation = new Follower();
        followerRelation.setFollowerId(1);
        followerRelation.setUserFollowerId(1);
        followerRelation.setUserToFollowId(2);
        followerRelation.setCreatedAt(LocalDateTime.now());

        followersList = new ArrayList<>();
        followersList.add(followerRelation);

        // Lista de seguidores do user2 (user1 segue user2)
        followersList = new ArrayList<>();
        followersList.add(followerRelation);

        // Lista de usuários que user1 segue (user1 segue user2)
        followedList = new ArrayList<>();
        followedList.add(followerRelation);

    }

        @Test
        void follow_WithValidData_ShouldReturnFollower() {
            Integer followerId = 1;
            Integer followedId = 2;

            when(userRepository.existsById(followerId)).thenReturn(true);
            when(userRepository.existsById(followedId)).thenReturn(true);
            when(followerRepository.existsByUserFollowerIdAndUserToFollowId(followerId, followedId)).thenReturn(false);
            when(followerRepository.save(any(Follower.class))).thenReturn(followerRelation);

            Follower result = followerService.follow(followerId, followedId);

            assertNotNull(result);
            assertEquals(followerId, result.getUserFollowerId());
            assertEquals(followedId, result.getUserToFollowId());

            verify(userRepository, times(1)).existsById(followerId);
            verify(userRepository, times(1)).existsById(followedId);

            verify(followerRepository, times(1)).existsByUserFollowerIdAndUserToFollowId(followerId, followedId);
            verify(followerRepository, times(1)).save(any(Follower.class));

        }

    @Test
    void follow_WithNullUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.follow(null, 2));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void follow_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.follow(0, 2));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void follow_WithNullUserToFollowId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.follow(1, null));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void follow_WithZeroUserToFollowId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.follow(1, 0));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void follow_WithSameUserIds_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.follow(1, 1));

        assertEquals("User cannot follow itself", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).save(any(Follower.class));
    }
    @Test
    void follow_WithNonExistentFollower_ShouldThrowException() {
        when(userRepository.existsById(1)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.follow(1, 2));

        assertEquals("User not found with id: '1'", exception.getMessage());

        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, never()).existsById(2);
        verify(followerRepository, never()).save(any(Follower.class));
    }


    @Test
    void follow_WithNonExistentFollowed_ShouldThrowException() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(2)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.follow(1, 2));

        assertEquals("User to follow not found with id: '2'", exception.getMessage());

        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).existsById(2);
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void follow_WithExistingRelation_ShouldThrowException() {
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.existsById(2)).thenReturn(true);
        when(followerRepository.existsByUserFollowerIdAndUserToFollowId(1, 2)).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> followerService.follow(1, 2));

        assertEquals("User already follows this user", exception.getMessage());

        verify(userRepository, times(1)).existsById(1);
        verify(userRepository, times(1)).existsById(2);
        verify(followerRepository, times(1)).existsByUserFollowerIdAndUserToFollowId(1, 2);
        verify(followerRepository, never()).save(any(Follower.class));
    }


    @Test
    void getFollowersCount_WithValidUserId_ShouldReturnCount() {
        Integer userId = 2;
        String userName = "User 2";
        long expectedCount = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(followerRepository.countByUserToFollowId(userId)).thenReturn(expectedCount);

        FollowersCountResponseDTO result = followerService.getFollowersCount(userId);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(userName, result.getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, times(1)).countByUserToFollowId(userId);
    }

    @Test
    void getFollowersCount_WithNullUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowersCount(null));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).countByUserToFollowId(anyInt());
    }
    @Test
    void getFollowersCount_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowersCount(0));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).countByUserToFollowId(anyInt());
    }

    @Test
    void getFollowersCount_WithNonExistentUser_ShouldThrowException() {
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.getFollowersCount(userId));

        assertEquals("User not found with id: '999'", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, never()).countByUserToFollowId(anyInt());
    }


    @Test
    void getFollowersList_WithValidUserIdAndNameAscOrder_ShouldReturnOrderedList() {
        Integer userId = 2;
        String order = "name_asc";

        User follower1 = new User();
        follower1.setUserId(1);
        follower1.setUserName("Alice");

        User follower3 = new User();
        follower3.setUserId(3);
        follower3.setUserName("Bob");

        Follower relation1 = new Follower();
        relation1.setFollowerId(1);
        relation1.setUserFollowerId(1);
        relation1.setUserToFollowId(2);

        Follower relation2 = new Follower();
        relation2.setFollowerId(2);
        relation2.setUserFollowerId(3);
        relation2.setUserToFollowId(2);

        List<Follower> followerRelations = new ArrayList<>();
        followerRelations.add(relation1);
        followerRelations.add(relation2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(followerRepository.findByUserToFollowId(userId)).thenReturn(followerRelations);
        when(userRepository.findById(1)).thenReturn(Optional.of(follower1));
        when(userRepository.findById(3)).thenReturn(Optional.of(follower3));

        FollowersListResponseDTO result = followerService.getFollowersList(userId, order);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("User 2", result.getUserName());
        assertEquals(2, result.getFollowers().size());
        assertEquals("Alice", result.getFollowers().get(0).getUserName()); // Alice deve vir primeiro em ordem alfabética
        assertEquals("Bob", result.getFollowers().get(1).getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, times(1)).findByUserToFollowId(userId);
        verify(userRepository, times(1)).findById(1);
        verify(userRepository, times(1)).findById(3);
    }

    @Test
    void getFollowersList_WithNullUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowersList(null, "name_asc"));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).findByUserToFollowId(anyInt());
    }

    @Test
    void getFollowersList_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowersList(0, "name_asc"));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).findByUserToFollowId(anyInt());
    }

    @Test
    void getFollowersList_WithNonExistentUser_ShouldThrowException() {
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.getFollowersList(userId, "name_asc"));

        assertEquals("User not found with id: '999'", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, never()).findByUserToFollowId(anyInt());
    }

    @Test
    void getFollowersList_WithInvalidOrder_ShouldThrowException() {
        Integer userId = 2;
        String invalidOrder = "invalid_order";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(followerRepository.findByUserToFollowId(userId)).thenReturn(new ArrayList<>());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowersList(userId, invalidOrder));

        assertEquals("Invalid order parameter. Use 'name_asc' or 'name_desc'.", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, times(1)).findByUserToFollowId(userId);
    }

    @Test
    void getFollowedList_WithValidUserIdAndNameAscOrder_ShouldReturnOrderedList() {
        Integer userId = 1;
        String order = "name_asc";

        User followed1 = new User();
        followed1.setUserId(2);
        followed1.setUserName("Alice");

        User followed3 = new User();
        followed3.setUserId(3);
        followed3.setUserName("Bob");

        Follower relation1 = new Follower();
        relation1.setFollowerId(1);
        relation1.setUserFollowerId(1);
        relation1.setUserToFollowId(2);

        Follower relation2 = new Follower();
        relation2.setFollowerId(2);
        relation2.setUserFollowerId(1);
        relation2.setUserToFollowId(3);

        List<Follower> followedRelations = new ArrayList<>();
        followedRelations.add(relation1);
        followedRelations.add(relation2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(followedRelations);
        when(userRepository.findById(2)).thenReturn(Optional.of(followed1));
        when(userRepository.findById(3)).thenReturn(Optional.of(followed3));

        FollowedListResponseDTO result = followerService.getFollowedList(userId, order);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("User 1", result.getUserName());
        assertEquals(2, result.getFollowed().size());
        assertEquals("Alice", result.getFollowed().get(0).getUserName()); // Alice deve vir primeiro em ordem alfabética
        assertEquals("Bob", result.getFollowed().get(1).getUserName());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, times(1)).findByUserFollowerId(userId);
        verify(userRepository, times(1)).findById(2);
        verify(userRepository, times(1)).findById(3);
    }

    @Test
    void getFollowedList_WithNullUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowedList(null, "name_asc"));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).findByUserFollowerId(anyInt());
    }

    @Test
    void getFollowedList_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowedList(0, "name_asc"));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).findById(anyInt());
        verify(followerRepository, never()).findByUserFollowerId(anyInt());
    }

    @Test
    void getFollowedList_WithNonExistentUser_ShouldThrowException() {
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.getFollowedList(userId, "name_asc"));

        assertEquals("User not found with id: '999'", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, never()).findByUserFollowerId(anyInt());
    }

    @Test
    void getFollowedList_WithInvalidOrder_ShouldThrowException() {
        Integer userId = 1;
        String invalidOrder = "invalid_order";

        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(followerRepository.findByUserFollowerId(userId)).thenReturn(new ArrayList<>());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.getFollowedList(userId, invalidOrder));

        assertEquals("Invalid order parameter. Use 'name_asc' or 'name_desc'.", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(followerRepository, times(1)).findByUserFollowerId(userId);
    }


    @Test
    void unfollow_WithValidData_ShouldDeleteRelation() {
        Integer userId = 1;
        Integer userIdToUnfollow = 2;

        Follower relation = new Follower();
        relation.setFollowerId(1);
        relation.setUserFollowerId(userId);
        relation.setUserToFollowId(userIdToUnfollow);

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.existsById(userIdToUnfollow)).thenReturn(true);
        when(followerRepository.findByUserFollowerIdAndUserToFollowId(userId, userIdToUnfollow))
                .thenReturn(Optional.of(relation));
        doNothing().when(followerRepository).delete(relation);

        followerService.unfollow(userId, userIdToUnfollow);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).existsById(userIdToUnfollow);
        verify(followerRepository, times(1)).findByUserFollowerIdAndUserToFollowId(userId, userIdToUnfollow);
        verify(followerRepository, times(1)).delete(relation);
    }

    @Test
    void unfollow_WithNullUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.unfollow(null, 2));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithZeroUserId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.unfollow(0, 2));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithNullUserToUnfollowId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.unfollow(1, null));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithZeroUserToUnfollowId_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.unfollow(1, 0));

        assertEquals("User ID is required", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithSameUserIds_ShouldThrowException() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> followerService.unfollow(1, 1));

        assertEquals("User cannot unfollow itself", exception.getMessage());

        verify(userRepository, never()).existsById(anyInt());
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithNonExistentFollower_ShouldThrowException() {
        Integer userId = 1;
        Integer userIdToUnfollow = 2;

        when(userRepository.existsById(userId)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.unfollow(userId, userIdToUnfollow));

        assertEquals("User not found with id: '1'", exception.getMessage());

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).existsById(userIdToUnfollow);
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void unfollow_WithNonExistentFollowed_ShouldThrowException() {
        Integer userId = 1;
        Integer userIdToUnfollow = 2;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.existsById(userIdToUnfollow)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> followerService.unfollow(userId, userIdToUnfollow));

        assertEquals("User to unfollow not found with id: '2'", exception.getMessage());

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).existsById(userIdToUnfollow);
        verify(followerRepository, never()).findByUserFollowerIdAndUserToFollowId(anyInt(), anyInt());
        verify(followerRepository, never()).delete(any(Follower.class));
    }

    @Test
    void buildComparator_WithAllPossibleValues_ShouldReturnCorrectly() throws Exception {
        Method buildComparatorMethod = FollowerService.class.getDeclaredMethod("buildComparator", String.class);
        buildComparatorMethod.setAccessible(true);

        Object comparatorAsc = buildComparatorMethod.invoke(followerService, "name_asc");
        assertNotNull(comparatorAsc);

        Object comparatorDesc = buildComparatorMethod.invoke(followerService, "name_desc");
        assertNotNull(comparatorDesc);

        Object comparatorInvalid = buildComparatorMethod.invoke(followerService, "invalid_order");
        assertNull(comparatorInvalid);

        Object comparatorNull = buildComparatorMethod.invoke(followerService, (Object) null);
        assertNull(comparatorNull);

        UserSummaryDTO user1 = new UserSummaryDTO(1, "Alice");
        UserSummaryDTO user2 = new UserSummaryDTO(2, "Bob");

        @SuppressWarnings("unchecked")
        Comparator<UserSummaryDTO> ascComparator = (Comparator<UserSummaryDTO>) comparatorAsc;
        @SuppressWarnings("unchecked")
        Comparator<UserSummaryDTO> descComparator = (Comparator<UserSummaryDTO>) comparatorDesc;

        // Alice deve vir antes de Bob em ordem ascendente
        assertTrue(ascComparator.compare(user1, user2) < 0);

        // Bob deve vir antes de Alice em ordem descendente
        assertTrue(descComparator.compare(user1, user2) > 0);
    }


}
