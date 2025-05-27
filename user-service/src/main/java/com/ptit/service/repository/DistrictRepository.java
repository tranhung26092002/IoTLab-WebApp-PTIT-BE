package com.ptit.service.repository;

import com.ptit.service.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, String>{
    Optional<District> findByCodeDistrict(String codeDistrict);
}
