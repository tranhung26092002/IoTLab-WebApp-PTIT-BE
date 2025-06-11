package com.ptit.service.util;

import com.ptit.service.dto.AddressDto;
import com.ptit.service.entity.Address;
import com.ptit.service.entity.District;
import com.ptit.service.entity.Province;
import com.ptit.service.entity.Ward;
import com.ptit.service.exception.BaseException;
import com.ptit.service.exception.ErrorCode;
import com.ptit.service.repository.DistrictRepository;
import com.ptit.service.repository.ProvinceRepository;
import com.ptit.service.repository.WardRepository;
import com.ptit.service.response.AddressResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                    return new BaseException(ErrorCode.ADDRESS_NOT_FOUND);
                });

        // Kiểm tra tính hợp lệ của địa chỉ
        if (!checkAddress(ward, addressDto)) {
            throw new BaseException(ErrorCode.ADDRESS_NOT_MATCH_WARD);
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
            throw new BaseException(ErrorCode.ADDRESS_DISTRICT_NOT_FOUND);
        }

        // Kiểm tra mã Province có khớp với mã District không
        if (!Objects.equals(ward.getDistrict().getProvince().getCodeProvince(), addressDto.getCodeProvince())) {
            throw new BaseException(ErrorCode.ADDRESS_PROVINCE_NOT_FOUND);
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
