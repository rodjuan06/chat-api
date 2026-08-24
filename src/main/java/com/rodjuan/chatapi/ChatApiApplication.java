package com.rodjuan.chatapi;

import com.rodjuan.chatapi.chat.repository.ChatRepository;
import com.rodjuan.chatapi.chat.repository.MessageRepository;
import com.rodjuan.chatapi.user.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
@EnableMongoRepositories(basePackageClasses = {ChatRepository.class, MessageRepository.class})
public class ChatApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatApiApplication.class, args);
    }

}
