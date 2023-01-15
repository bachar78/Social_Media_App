package com.bachar.socialapp.project.userIntegrationTest;

import com.bachar.socialapp.project.TestUtil;
import com.bachar.socialapp.project.error.ApiError;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserRepository;
import com.bachar.socialapp.project.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    public static final String API_V_1_LOGIN = "/api/v1/login";
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @BeforeEach
    public void cleanup() {
        userRepository.deleteAll();
        restTemplate.getRestTemplate().getInterceptors().clear();
    }


    @Test
    @DisplayName("Login without credential")
    void postLogin_withoutUserCredential_receiveUnAuthorized() {
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    @Test
    @DisplayName("Login with incorrect user credential")
    void postLogin_withInCorrectUserCredential_receiveUnAuthorized() {
        authenticate();
        ResponseEntity<Object> response = login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Login with incorrect user credential without validationError")
    void postLogin_withInCorrectUserCredential_receiveWithoutValidationErrorMap() {
        authenticate();
        ResponseEntity<String> response = login(String.class);
        assertThat(response.getBody().contains("validationError")).isFalse();
    }

    @Test
    @DisplayName("User invalid receive apiError url")
    public void postLogin_whenUserIsInvalid_receiveApiError() {
        User user = new User();
        ResponseEntity<ApiError> response =  login(ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_V_1_LOGIN);
    }

    @Test
    @DisplayName("User invalid receive without authenticate header")
    public void postLogin_whenUserIsInvalid_receiveWithout_authenticateHeader() {
       authenticate();
        ResponseEntity<Object> response =  login(Object.class);
        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    @DisplayName("User with valid credentials status ok")
    public void postLogin_whenUserIsValid_receiveOKSTATUS() {
        userService.saveUser(TestUtil.createUser());
        authenticate();
        ResponseEntity<Object> response =  login(Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Receive LoggedIn user's id")
    public void postLogin_withValidCredentials_receiveLoggedInUserId() {
        User inDB = userService.saveUser(TestUtil.createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> body = response.getBody();
        Integer id = (Integer) body.get("id");
        assertThat(id).isEqualTo(inDB.getId());
    }


    @Test
    @DisplayName("Receive LoggedIn user's displayName ")
    public void postLogin_withValidCredentials_receiveLoggedInUserDisplayName() {
        User userInDB = userService.saveUser(TestUtil.createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {
        });
        Map<String, Object> body = response.getBody();
        String displayName = (String) body.get("displayName");
        assertThat(displayName).isEqualTo(userInDB.getDisplayName());
    }

    @Test
    @DisplayName("Receive LoggedIn user's username ")
    public void postLogin_withValidCredentials_receiveLoggedInUsername() {
        User userInDB = userService.saveUser(TestUtil.createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {
        });
        Map<String, Object> body = response.getBody();
        String username = (String) body.get("username");
        assertThat(username).isEqualTo(userInDB.getUsername());
    }

    @Test
    @DisplayName("Receive LoggedIn without password ")
    public void postLogin_withValidCredentials_receiveWithoutPassowrd() {
        User userInDB = userService.saveUser(TestUtil.createUser());
        authenticate();
        ResponseEntity<Map<String, Object>> response = login(new ParameterizedTypeReference<Map<String, Object>>() {
        });
        Map<String, Object> body = response.getBody();
        assertThat(body.containsKey("password")).isFalse();
    }

    <T> ResponseEntity<T> login(Class<T> response) {
        return restTemplate.postForEntity(API_V_1_LOGIN, null, response);
    }
    private <T> ResponseEntity<T> login(ParameterizedTypeReference<T> response) {
        return restTemplate.exchange(API_V_1_LOGIN, HttpMethod.POST, null, response);
    }
    private void authenticate() {
        restTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor("test-user", "P4ssword"));
    }

}
