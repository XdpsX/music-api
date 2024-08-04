package com.xdpsx.music.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/helloworld")
public class TestController {
    @GetMapping
    public String helloWolrd(){
        return "Hello World";
    }

    @PostMapping
    public String postHelloWorld(){
        return "Post - Hello World!";
    }
}
