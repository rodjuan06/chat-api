package com.rodjuan.chatapi.user;

import java.util.Collection;

public interface UserVerifier {

    boolean allExist(Collection<Long> ids);
}
