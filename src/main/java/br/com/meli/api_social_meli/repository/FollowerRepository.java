package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    boolean existsByUserFollowerIdAndUserToFollowId(Integer userFollowerId, Integer userToFollowId);

    long countByUserToFollowId(Integer userToFollowId);

    List<Follower> findByUserToFollowId(Integer userToFollowId);

    List<Follower> findByUserFollowerId(Integer userFollowerId);
}
