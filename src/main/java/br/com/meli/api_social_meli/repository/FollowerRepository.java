package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Follower;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowerRepository extends JpaRepository<Follower, Integer> {
    boolean existsByUserFollowerIdAndUserToFollowId(Integer userFollowerId, Integer userToFollowId);

    long countByUserToFollowId(Integer userToFollowId);
}
