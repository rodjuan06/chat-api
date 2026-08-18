package com.rodjuan.chatapi.config;

import org.bson.types.ObjectId;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToObjectIdConverter());
    }

    private static class StringToObjectIdConverter implements Converter<String, ObjectId> {
        @Override
        public ObjectId convert(@NonNull String source) {
            if (!ObjectId.isValid(source)) {
                throw new IllegalArgumentException("Invalid object id format: " + source);
            }
            
            return new ObjectId(source);
        }
    }
}
