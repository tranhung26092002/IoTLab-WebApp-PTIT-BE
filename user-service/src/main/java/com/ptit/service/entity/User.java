package com.ptit.service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ptit.service.entity.enums.Gender;
import com.ptit.service.entity.enums.Provider;
import com.ptit.service.entity.enums.RoleType;
import com.ptit.service.entity.enums.StateUser;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Where(clause = "deleted = false")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@TypeDef(name = "pg-jsonb", typeClass = JsonBinaryType.class)
public class User implements UserDetails, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name_system", length = 50)
    private String userName;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "class_code", length = 50)
    private String classCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "avatar")
    private String avatarUrl;

    @Column(name = "avatar_source")
    @Enumerated(EnumType.STRING)
    private Provider avatarSource;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StateUser status;

    @Type(type = "pg-jsonb")
    @Column(name = "address", columnDefinition = "jsonb")
    private Address address;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "oauth2_id")
    private String oauth2Id;

    @Column(name = "auth_provider")
    @Enumerated(EnumType.STRING)
    private Provider authProvider;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public User(String admin, String admin1, String ioTLab, String number, String password, String mail, String s, Gender gender, LocalDate dob, StateUser stateUser, Address address, boolean b, RoleType roleType) {
        this.userName = admin;
        this.fullName = admin1;
        this.classCode = ioTLab;
        this.phoneNumber = number;
        this.password = password;
        this.email = mail;
        this.avatarUrl = s;
        this.gender = gender;
        this.dateOfBirth = dob;
        this.status = stateUser;
        this.address = address;
        this.deleted = b;
        this.roleType = roleType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
