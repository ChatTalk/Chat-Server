package com.example.chatserver.domain.test;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class TestController {

    @GetMapping("/test")
    public void test() {
        System.out.println("************************");
        log.info("test success");
        System.out.println("************************");
    }
}
