package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
}
