package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Follower;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    boolean existsByUserFollowerIdAndUserToFollowId(Integer userFollowerId, Integer userToFollowId);

    long countByUserToFollowId(Integer userToFollowId);

    List<Follower> findByUserToFollowId(Integer userToFollowId);

    // Pagination of a user's followers (who follows the userId)
    Page<Follower> findByUserToFollowId(Integer userId, Pageable pageable);

    List<Follower> findByUserFollowerId(Integer userFollowerId);

    // Pagination of a user's followed (who the userId follows)
    Page<Follower> findByUserFollowerId(Integer userId, Pageable pageable);

    Optional<Follower> findByUserFollowerIdAndUserToFollowId(Integer userFollowerId, Integer userToFollowId);
}
