package com.mycompany.myapp.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mycompany.myapp.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Test class for the HelloResource REST controller.
 *
 * @see HelloResource
 */
@IntegrationTest
class HelloResourceIT {

    private MockMvc restMockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        HelloResource helloResource = new HelloResource();
        restMockMvc = MockMvcBuilders.standaloneSetup(helloResource).build();
    }

    /**
     * Test hello
     */
    @Test
    void testHello() throws Exception {
        restMockMvc.perform(get("/api/hello-resource/hello")).andExpect(status().isOk());
    }

    /**
     * Test helloWithName
     */
    @Test
    void testHelloWithName() throws Exception {
        restMockMvc.perform(get("/api/hello-resource/hello-with-name")).andExpect(status().isOk());
    }
}
