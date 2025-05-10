package com.nguyensao.ecommerce_layered_architecture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Banner;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

}
