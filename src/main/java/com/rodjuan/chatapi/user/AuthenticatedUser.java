package com.rodjuan.chatapi.user;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticatedUser extends UserDetails {

    Long getId();
}
