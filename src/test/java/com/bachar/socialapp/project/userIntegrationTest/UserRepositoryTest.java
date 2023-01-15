package com.bachar.socialapp.project.userIntegrationTest;


import com.bachar.socialapp.project.TestUtil;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Fetching user is succeeded")
    public void findByUserName_whenTheUserExist_returnTheUser() {
       User user = TestUtil.createUser();
        userRepository.save(user);
       User userDB = userRepository.findByUsername("test-user");
        assertThat(userDB).isNotNull();
        assertThat(userDB.getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Fetching not existing user")
    public void findByUserName_whenTheUserDoesNotExist_returnNull() {
        User userDB = userRepository.findByUsername("test-null");
        assertThat(userDB).isNull();
    }

}
