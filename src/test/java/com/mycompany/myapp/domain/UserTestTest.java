package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserTestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTest.class);
        UserTest userTest1 = new UserTest();
        userTest1.setId(1L);
        UserTest userTest2 = new UserTest();
        userTest2.setId(userTest1.getId());
        assertThat(userTest1).isEqualTo(userTest2);
        userTest2.setId(2L);
        assertThat(userTest1).isNotEqualTo(userTest2);
        userTest1.setId(null);
        assertThat(userTest1).isNotEqualTo(userTest2);
    }
}
