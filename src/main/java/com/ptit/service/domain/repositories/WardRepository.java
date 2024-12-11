package com.ptit.service.domain.repositories;

import com.ptit.service.app.dtos.filter.WardFilterDto;
import com.ptit.service.domain.entities.Ward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, String> {

    Optional<Ward> findByCodeWard(String codeWard);

    @Query("SELECT w FROM Ward w "
            + "JOIN w.district d "
            + "JOIN d.province p WHERE "
            + "(:#{#filter.codeWard} IS NULL OR LOWER(w.codeWard) LIKE LOWER(CONCAT('%', :#{#filter.codeWard}, '%'))) AND "
            + "(:#{#filter.nameWard} IS NULL OR LOWER(w.nameWard) LIKE LOWER(CONCAT('%', :#{#filter.nameWard}, '%'))) AND "
            + "(:#{#filter.codeDistrict} IS NULL OR LOWER(d.codeDistrict) LIKE LOWER(CONCAT('%', :#{#filter.codeDistrict}, '%'))) AND "
            + "(:#{#filter.nameDistrict} IS NULL OR LOWER(d.nameDistrict) LIKE LOWER(CONCAT('%', :#{#filter.nameDistrict}, '%'))) AND "
            + "(:#{#filter.codeProvince} IS NULL OR LOWER(p.codeProvince) LIKE LOWER(CONCAT('%', :#{#filter.codeProvince}, '%'))) AND "
            + "(:#{#filter.nameProvince} IS NULL OR LOWER(p.nameProvince) LIKE LOWER(CONCAT('%', :#{#filter.nameProvince}, '%')))")
    Page<Ward> filterWards(
            @Param("filter") WardFilterDto filter,
            Pageable pageable);
}
