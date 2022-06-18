package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.UserTest;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the UserTest entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTestRepository extends JpaRepository<UserTest, Long> {}
