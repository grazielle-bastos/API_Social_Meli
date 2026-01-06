package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUserIdInAndDateGreaterThanEqualOrderByDateDesc(List<Integer> userIds, LocalDate startDate);

    int countByUserIdAndHasPromoTrue(Integer userId);
}
