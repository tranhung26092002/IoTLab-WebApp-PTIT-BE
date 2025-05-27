package com.ptit.service.database;

import com.ptit.service.entity.Address;
import com.ptit.service.entity.User;
import com.ptit.service.entity.enums.Gender;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import com.ptit.service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseUser implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) { // Chỉ tạo nếu chưa có user nào
            String password = passwordEncoder.encode("admin123");
            LocalDate dob = LocalDate.of(1990, 1, 1);
            Address address = new Address("", "", "", "", "", "", "");
            List<User> users = List.of(
                    new User(
                            "ADMIN", "Admin", "IoT Lab", "0123456789",
                            password, "openlab.user@gmail.com", "",
                            Gender.MALE, dob, StateUser.ACTIVE,
                            address, false, RoleType.ADMIN
                    ),
                    new User(
                            "ADMIN1", "Admin1", "IoT Lab", "0123456789",
                            password, "openlab.user@gmail.com", "",
                            Gender.MALE, dob, StateUser.ACTIVE,
                            address, false, RoleType.TEACHER
                    )
            );

            userRepository.saveAll(users);
            System.out.println("💾 Đã tạo user mặc định thành công!");
        } else {
            System.out.println("✅ User đã tồn tại, không cần khởi tạo.");
        }
    }
}
