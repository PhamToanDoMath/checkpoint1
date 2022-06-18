package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.UserTest;
import com.mycompany.myapp.repository.UserTestRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link UserTestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class UserTestResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_AGE = 1;
    private static final Integer UPDATED_AGE = 2;

    private static final String ENTITY_API_URL = "/api/user-tests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserTestRepository userTestRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserTestMockMvc;

    private UserTest userTest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTest createEntity(EntityManager em) {
        UserTest userTest = new UserTest().firstName(DEFAULT_FIRST_NAME).age(DEFAULT_AGE);
        return userTest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTest createUpdatedEntity(EntityManager em) {
        UserTest userTest = new UserTest().firstName(UPDATED_FIRST_NAME).age(UPDATED_AGE);
        return userTest;
    }

    @BeforeEach
    public void initTest() {
        userTest = createEntity(em);
    }

    @Test
    @Transactional
    void createUserTest() throws Exception {
        int databaseSizeBeforeCreate = userTestRepository.findAll().size();
        // Create the UserTest
        restUserTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userTest)))
            .andExpect(status().isCreated());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeCreate + 1);
        UserTest testUserTest = userTestList.get(userTestList.size() - 1);
        assertThat(testUserTest.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserTest.getAge()).isEqualTo(DEFAULT_AGE);
    }

    @Test
    @Transactional
    void createUserTestWithExistingId() throws Exception {
        // Create the UserTest with an existing ID
        userTest.setId(1L);

        int databaseSizeBeforeCreate = userTestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserTestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userTest)))
            .andExpect(status().isBadRequest());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllUserTests() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        // Get all the userTestList
        restUserTestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTest.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE)));
    }

    @Test
    @Transactional
    void getUserTest() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        // Get the userTest
        restUserTestMockMvc
            .perform(get(ENTITY_API_URL_ID, userTest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userTest.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE));
    }

    @Test
    @Transactional
    void getNonExistingUserTest() throws Exception {
        // Get the userTest
        restUserTestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewUserTest() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();

        // Update the userTest
        UserTest updatedUserTest = userTestRepository.findById(userTest.getId()).get();
        // Disconnect from session so that the updates on updatedUserTest are not directly saved in db
        em.detach(updatedUserTest);
        updatedUserTest.firstName(UPDATED_FIRST_NAME).age(UPDATED_AGE);

        restUserTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedUserTest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedUserTest))
            )
            .andExpect(status().isOk());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
        UserTest testUserTest = userTestList.get(userTestList.size() - 1);
        assertThat(testUserTest.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserTest.getAge()).isEqualTo(UPDATED_AGE);
    }

    @Test
    @Transactional
    void putNonExistingUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, userTest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(userTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(userTest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateUserTestWithPatch() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();

        // Update the userTest using partial update
        UserTest partialUpdatedUserTest = new UserTest();
        partialUpdatedUserTest.setId(userTest.getId());

        restUserTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserTest))
            )
            .andExpect(status().isOk());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
        UserTest testUserTest = userTestList.get(userTestList.size() - 1);
        assertThat(testUserTest.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testUserTest.getAge()).isEqualTo(DEFAULT_AGE);
    }

    @Test
    @Transactional
    void fullUpdateUserTestWithPatch() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();

        // Update the userTest using partial update
        UserTest partialUpdatedUserTest = new UserTest();
        partialUpdatedUserTest.setId(userTest.getId());

        partialUpdatedUserTest.firstName(UPDATED_FIRST_NAME).age(UPDATED_AGE);

        restUserTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedUserTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedUserTest))
            )
            .andExpect(status().isOk());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
        UserTest testUserTest = userTestList.get(userTestList.size() - 1);
        assertThat(testUserTest.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testUserTest.getAge()).isEqualTo(UPDATED_AGE);
    }

    @Test
    @Transactional
    void patchNonExistingUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, userTest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(userTest))
            )
            .andExpect(status().isBadRequest());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamUserTest() throws Exception {
        int databaseSizeBeforeUpdate = userTestRepository.findAll().size();
        userTest.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restUserTestMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(userTest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the UserTest in the database
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteUserTest() throws Exception {
        // Initialize the database
        userTestRepository.saveAndFlush(userTest);

        int databaseSizeBeforeDelete = userTestRepository.findAll().size();

        // Delete the userTest
        restUserTestMockMvc
            .perform(delete(ENTITY_API_URL_ID, userTest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserTest> userTestList = userTestRepository.findAll();
        assertThat(userTestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
