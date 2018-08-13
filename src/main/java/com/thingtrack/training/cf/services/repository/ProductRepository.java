package com.thingtrack.training.cf.services.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thingtrack.training.cf.services.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {    
}
