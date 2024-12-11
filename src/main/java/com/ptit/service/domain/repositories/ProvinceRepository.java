package com.ptit.service.domain.repositories;

import com.ptit.service.domain.entities.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, String> {
    Optional<Province> findByCodeProvince(String codeProvince);
}
