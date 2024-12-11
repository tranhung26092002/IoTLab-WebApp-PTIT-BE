package com.ptit.service.Database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ptit.service.domain.entities.District;
import com.ptit.service.domain.entities.Province;
import com.ptit.service.domain.entities.Ward;
import com.ptit.service.domain.repositories.DistrictRepository;
import com.ptit.service.domain.repositories.ProvinceRepository;
import com.ptit.service.domain.repositories.WardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@Order(1)
@RequiredArgsConstructor
public class DatabaseLoader implements CommandLineRunner {
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (provinceRepository.count() > 0) {
            return; // Data already exists, no need to import
        }

        JsonNode jsonNode;
        try (InputStream inputStream = getClass().getResourceAsStream("/data/simplified_json_generated_data_vn_units.json")) {
            jsonNode = objectMapper.readTree(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON data", e);
        }

        // Parse and save provinces
        for (JsonNode provinceNode : jsonNode) {
            Province province = new Province();
            province.setCodeProvince(provinceNode.get("Code").asText());
            province.setNameProvince(provinceNode.get("Name").asText());
            province.setFullName(provinceNode.get("FullName").asText());
            province.setNameEn(provinceNode.get("NameEn").asText());
            province.setFullNameEn(provinceNode.get("FullNameEn").asText());
            province.setCodeName(provinceNode.get("CodeName").asText());
            // Set administrative region and unit if needed
            provinceRepository.save(province);

            // Parse and save districts
            for (JsonNode districtNode : provinceNode.get("District")) {
                District district = new District();
                district.setCodeDistrict(districtNode.get("Code").asText());
                district.setNameDistrict(districtNode.get("Name").asText());
                district.setFullName(districtNode.get("FullName").asText());
                district.setNameEn(districtNode.get("NameEn").asText());
                district.setFullNameEn(districtNode.get("FullNameEn").asText());
                district.setCodeName(districtNode.get("CodeName").asText());
                district.setProvince(province);
                districtRepository.save(district);

                // Parse and save wards
                for (JsonNode wardNode : districtNode.get("Ward")) {
                    Ward ward = new Ward();
                    ward.setCodeWard(wardNode.get("Code").asText());
                    ward.setNameWard(wardNode.get("Name").asText());
                    ward.setFullName(wardNode.get("FullName").asText());
                    ward.setNameEn(wardNode.get("NameEn").asText());
                    ward.setFullNameEn(wardNode.get("FullNameEn").asText());
                    ward.setCodeName(wardNode.get("CodeName").asText());
                    ward.setDistrict(district);
                    wardRepository.save(ward);
                }
            }
        }
    }
}
