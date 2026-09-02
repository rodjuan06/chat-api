package com.rodjuan.chatapi.user.repository;

import com.rodjuan.chatapi.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByIdIn(Collection<Long> ids);
}
