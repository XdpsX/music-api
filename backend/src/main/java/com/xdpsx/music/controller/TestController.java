package com.xdpsx.music.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/helloworld")
@Slf4j
@Hidden
public class TestController {
    @Autowired
    private MessageSource messageSource;

    @GetMapping
    public String helloWolrd(){
        return "Hello World";
    }

    @PostMapping
    public String postHelloWorld(){
        return "Post - Hello World!";
    }

    @GetMapping("/greeting")
    public String greeting(){
        log.info("Locale: " + LocaleContextHolder.getLocale());
        return messageSource.getMessage("greeting", null, LocaleContextHolder.getLocale());
    }
}
