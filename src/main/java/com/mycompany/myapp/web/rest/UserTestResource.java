package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.UserTest;
import com.mycompany.myapp.repository.UserTestRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.UserTest}.
 */
@RestController
@RequestMapping("/")
@Transactional
public class UserTestResource {

    private final Logger log = LoggerFactory.getLogger(UserTestResource.class);

    private static final String ENTITY_NAME = "userTest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserTestRepository userTestRepository;

    public UserTestResource(UserTestRepository r) {
        this.userTestRepository = r;
    }

    /**
     * GET hello
     */
    @GetMapping("/hello")
    public String hello() {

        return "Hello World ";
    }

    @GetMapping("/all")
    public List<UserTest> getAllUsers() {
        return userTestRepository.findAll();
    }

    @PostMapping("/save")
    public String savePerson(@Valid @RequestBody UserTest userTest){
        userTestRepository.save(userTest);
        return "Saved!";
    }

    @GetMapping("/hello/{id}")
    public String helloWithName(@PathVariable Long id) {
        UserTest user = userTestRepository.findById(id).get();
        return "Hello " + user.getFirstName();
    }

}
