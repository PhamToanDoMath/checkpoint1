package com.mycompany.myapp.domain;

public class UserTest {

    private Long id;
    private String firstName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private Integer age;

    public UserTest(Long id, String firstName, Integer age) {
        this.id = id;
        this.firstName = firstName;
        this.age = age;
    }
}
