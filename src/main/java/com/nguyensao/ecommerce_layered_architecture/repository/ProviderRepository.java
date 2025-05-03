package com.nguyensao.ecommerce_layered_architecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Provider;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    boolean existsByProviderAndProviderId(String provider, String providerId);

}
