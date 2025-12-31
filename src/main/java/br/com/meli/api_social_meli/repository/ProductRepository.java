package br.com.meli.api_social_meli.repository;

import br.com.meli.api_social_meli.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
