package com.rodjuan.chatapi.support;

import com.rodjuan.chatapi.chat.model.objectid.ObjectIdDeserializer;
import com.rodjuan.chatapi.chat.model.objectid.ObjectIdSerializer;
import com.rodjuan.chatapi.config.WebConfig;
import com.rodjuan.chatapi.exception.GlobalExceptionHandler;
import com.rodjuan.chatapi.security.JwtAuthFilter;
import com.rodjuan.chatapi.security.RestAuthenticationEntryPoint;
import com.rodjuan.chatapi.security.SecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration(proxyBeanMethods = false)
@Import({
        SecurityConfig.class,
        JwtAuthFilter.class,
        RestAuthenticationEntryPoint.class,
        GlobalExceptionHandler.class,
        WebConfig.class,
        ObjectIdSerializer.class,
        ObjectIdDeserializer.class
})
public class ControllerTestConfiguration {
}
