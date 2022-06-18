package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UserTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HelloResource controller
 */
@RestController
@RequestMapping("/")
public class HelloResource {

    private final Logger log = LoggerFactory.getLogger(HelloResource.class);

    /**
     * GET hello
     */
    @GetMapping("/hello")
    public String hello() {

        return "Hello World ";
    }

    /**
     * GET helloWithName
     */
    @GetMapping("/hello/{id}")
    public String helloWithName(@PathVariable Long id) {
        UserTest user = new UserTest(1L,"Toan",15);
        return "Hello World " + user.getFirstName();
    }
}
