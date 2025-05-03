package com.nguyensao.ecommerce_layered_architecture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nguyensao.ecommerce_layered_architecture.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    List<Address> findAllByUserId(String userId);

}
