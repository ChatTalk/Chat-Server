package com.example.chatserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// ./gradlew build 실패 극복을 위한 classes 속성 할당
@SpringBootTest(classes = ChatServerApplication.class)
class ChatServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
