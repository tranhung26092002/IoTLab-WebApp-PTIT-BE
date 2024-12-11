package com.ptit.service.domain.utils;

import com.ommanisoft.common.exceptions.ExceptionOm;
import com.ptit.service.app.dtos.AddressDto;
import com.ptit.service.app.responses.address.AddressResponse;
import com.ptit.service.domain.entities.Address;
import com.ptit.service.domain.entities.District;
import com.ptit.service.domain.entities.Province;
import com.ptit.service.domain.entities.Ward;
import com.ptit.service.domain.exceptions.ErrorMessage;
import com.ptit.service.domain.repositories.DistrictRepository;
import com.ptit.service.domain.repositories.ProvinceRepository;
import com.ptit.service.domain.repositories.WardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddressUtil {
    private final WardRepository wardRepository;
    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;

    public AddressResponse getAddressDetails(Address address) {
        Optional<Ward> ward = wardRepository.findByCodeWard(address.getCodeWard());
        Optional<District> district = districtRepository.findByCodeDistrict(address.getCodeDistrict());
        Optional<Province> province = provinceRepository.findByCodeProvince(address.getCodeProvince());

        return AddressResponse.builder()
                .addressDetail(address.getAddressDetail())
                .codeWard(address.getCodeWard())
                .nameWard(ward.map(Ward::getNameWard).orElse(null))
                .codeDistrict(address.getCodeDistrict())
                .nameDistrict(district.map(District::getNameDistrict).orElse(null))
                .codeProvince(address.getCodeProvince())
                .nameProvince(province.map(Province::getNameProvince).orElse(null))
                .build();
    }

    public Address generateAddress(AddressDto addressDto) {
        // Tìm kiếm Ward theo mã
        Ward ward = wardRepository.findByCodeWard(addressDto.getCodeWard())
                .orElseThrow(() -> {
                    log.error("Failed to create Admin: Ward with code '{}' not found", addressDto.getCodeWard());
                    return new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.WARD_NOT_FOUND.val());
                });

        // Kiểm tra tính hợp lệ của địa chỉ
        if (!checkAddress(ward, addressDto)) {
            log.error("Failed to create Admin: Address does not match with ward");
            throw new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.ADDRESS_NOT_MATCH_WARD.val());
        }

        Address newAddress = new Address();
        newAddress.setAddressDetail(addressDto.getAddressDetail());
        newAddress.setCodeWard(ward.getCodeWard());
        newAddress.setNameWard(ward.getNameWard());
        newAddress.setCodeDistrict(ward.getDistrict().getCodeDistrict());
        newAddress.setNameDistrict(ward.getDistrict().getNameDistrict());
        newAddress.setCodeProvince(ward.getDistrict().getProvince().getCodeProvince());
        newAddress.setNameProvince(ward.getDistrict().getProvince().getNameProvince());

        return newAddress;
    }

    public boolean checkAddress(Ward ward, AddressDto addressDto) {
        // Kiểm tra mã District có khớp với mã Ward không
        if (!Objects.equals(ward.getDistrict().getCodeDistrict(), addressDto.getCodeDistrict())) {
            log.error("Failed to create Admin: District code '{}' does not match with ward code '{}'",
                    addressDto.getCodeDistrict(), addressDto.getCodeWard());
            throw new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.DISTRICT_NOT_FOUND.val());
        }

        // Kiểm tra mã Province có khớp với mã District không
        if (!Objects.equals(ward.getDistrict().getProvince().getCodeProvince(), addressDto.getCodeProvince())) {
            log.error("Failed to create Admin: Province code '{}' does not match with district code '{}'",
                    addressDto.getCodeProvince(), addressDto.getCodeDistrict());
            throw new ExceptionOm(HttpStatus.NOT_FOUND, ErrorMessage.PROVINCE_NOT_FOUND.val());
        }

        return true;
    }

    public boolean isSameAddress(Address currentAddress, Address newAddress) {
        if (currentAddress == null && newAddress == null) {
            return true;
        }

        if (currentAddress == null || newAddress == null) {
            return false;
        }

        return Objects.equals(currentAddress.getAddressDetail(), newAddress.getAddressDetail()) &&
                Objects.equals(currentAddress.getCodeWard(), newAddress.getCodeWard()) &&
                Objects.equals(currentAddress.getCodeDistrict(), newAddress.getCodeDistrict()) &&
                Objects.equals(currentAddress.getCodeProvince(), newAddress.getCodeProvince());
    }
}
