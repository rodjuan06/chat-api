package com.rodjuan.chatapi.user.model;

import com.rodjuan.chatapi.user.AuthenticatedUser;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements AuthenticatedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + Objects.requireNonNull(role, "User role must not be null").name()));
    }

    @Override
    @NonNull
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return AuthenticatedUser.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return AuthenticatedUser.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return AuthenticatedUser.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return AuthenticatedUser.super.isEnabled();
    }
}
