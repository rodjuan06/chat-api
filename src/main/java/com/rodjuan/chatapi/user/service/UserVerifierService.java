package com.rodjuan.chatapi.user.service;

import com.rodjuan.chatapi.user.UserVerifier;
import com.rodjuan.chatapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserVerifierService implements UserVerifier {

    private final UserRepository userRepository;

    @Override
    public boolean allExist(Collection<Long> ids) {
        return userRepository.countByIdIn(ids) == ids.size();
    }
}
