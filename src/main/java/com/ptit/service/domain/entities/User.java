package com.ptit.service.domain.entities;

import com.fasterxml.jackson.annotation.*;
import com.ptit.service.domain.enums.Gender;
import com.ptit.service.domain.enums.RoleType;
import com.ptit.service.domain.enums.StateUser;
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

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "avatar")
    private String avatarUrl;

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
    private Boolean deleted;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return phoneNumber;
    }

    public String getUserName(){
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
