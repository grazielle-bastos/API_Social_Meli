package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Page<User> findAll(Pageable pageable);

    Page<User> findByUserNameContainingIgnoreCase(String userName, Pageable pageable);
}
