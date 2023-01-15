package com.bachar.socialapp.project.userIntegrationTest;

import com.bachar.socialapp.project.TestUtil;
import com.bachar.socialapp.project.user.UserService;
import com.bachar.socialapp.project.user.vm.UserUpdateVM;
import com.bachar.socialapp.project.user.vm.UserVM;
import org.junit.jupiter.api.*;
import com.bachar.socialapp.project.error.ApiError;
import com.bachar.socialapp.project.shared.GenericResponse;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    public static final String API_V_1_USERS = "/api/v1/users";
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }

    @Test
    @DisplayName("Save User to Database")
    public void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = TestUtil.createUser();
        postSignup(user, Object.class);
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Send message when user is saved")
    public void postUser_whenUserIsValid_sendSuccessMessage() {
        User user = TestUtil.createUser();
        ResponseEntity<GenericResponse> response = postSignup(user, GenericResponse.class);
        assertThat(response.getBody().getMessage()).isEqualTo("User Saved");
    }

    @Test
    @DisplayName("Status is OK when save user")
    public void postUser_whenUserValid_receiveOk() {
        User user = TestUtil.createUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Password is hashed")
    public void postUser_whenUserValid_passwordIsHashed() {
        User user = TestUtil.createUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        List<User> users = userRepository.findAll();
        User userInDB = users.get(0);
        assertThat(user.getPassword()).isNotEqualTo(userInDB.getPassword());
    }

    @Test
    @DisplayName("Bad Request when null username")
    public void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setUsername(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("Bad Request when null displayName")
    public void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setDisplayName(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    @DisplayName("Bad Request when null Password")
    public void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setPassword(null);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when username < 4")
    public void postUser_whenUsernameLessThankRequired_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setUsername("abc");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when username exceeds the limits > 255")
    public void postUser_whenUsernameExceedsRequired_receiveBadRequest() {
        User user = TestUtil.createUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when displayName < 4")
    public void postUser_whenDisplayNameLessThankRequired_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setDisplayName("abc");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when displayName exceeds the limits > 255")
    public void postUser_whenDisplayNameExceedsRequired_receiveBadRequest() {
        User user = TestUtil.createUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setDisplayName(valueOf256Chars);
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when password < 8")
    public void postUser_whenPasswordLessThankRequired_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setPassword("Abcde");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when password > 255")
    public void postUser_whenPasswordExceedsLimit_receiveBadRequest() {
        User user = TestUtil.createUser();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword(valueOf256Chars + "A1");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when password with only lowercase")
    public void postUser_whenPasswordWithAllLowerCase_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setPassword("adfadfadf");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when password with only Uppercase")
    public void postUser_whenPasswordWithAllUpperCase_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setPassword("AAAAAAAAA");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Bad Request when password with only numbers")
    public void postUser_whenPasswordWithOnlyNumber_receiveBadRequest() {
        User user = TestUtil.createUser();
        user.setPassword("1111111111111");
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("User invalid receive apiError url")
    public void postUser_whenUserIsInvalid_receiveApiError() {
        User user = new User();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_V_1_USERS);
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiErrorMessage() {
        User user = new User();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        assertThat(response.getBody().getMessage()).isEqualTo("Validation Error");
    }

    @Test
    public void postUser_whenUserIsInvalid_receiveApiErrorWithValidationError() {
        User user = new User();
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        assertThat(response.getBody().getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("Receive message of null username")
    public void postUser_whenUserIsNull_receiveNullMessageUsername() {
        User user = new User();
        user.setUsername(null);
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    @DisplayName("Receive message of invalid username length")
    public void postUser_whenUserHasInvalidUsernameLength_receiveMessageUsername() {
        User user = TestUtil.createUser();
        user.setUsername("abc");
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("username")).isEqualTo("It must have minimum 4 and maximum 255 characters");
    }

    @Test
    @DisplayName("Receive message of invalid password")
    public void postUser_whenUserHasInvalidPassword_receiveMessagePassword() {
        User user = TestUtil.createUser();
        user.setPassword("abcjlkjl");
        ResponseEntity<ApiError> response = postSignup(user, ApiError.class);
        Map<String, String> validationErrors = response.getBody().getValidationErrors();
        assertThat(validationErrors.get("password")).isEqualTo("Password must have at least one uppercase, one lowercase letter and one number");
    }

    @Test
    @DisplayName("Receive Bad Request of an existing username")
    public void postUser_whenUsernameIsRepeated_receiveStatusOfBadRequest() {
        userRepository.save(TestUtil.createUser());
        User user = TestUtil.createUser();
        ResponseEntity<Object> response = postSignup(user, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("When no users receive ok status")
    public void getUsers_whenThereIsNoUserInDB_receiveOk() {
        ResponseEntity<Object> response = testRestTemplate.getForEntity(API_V_1_USERS, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("When no users receive page with 0 element")
    public void getUsers_whenThereIsNoUserInDB_receivePage() {
        ResponseEntity<TestPage<Object>> response = testRestTemplate.exchange(API_V_1_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }


    @Test
    @DisplayName("When one users receive page with 1 element")
    public void getUsers_whenThereIsUserInDB_receivePageWithUser() {
        userService.saveUser(TestUtil.createUser());
        ResponseEntity<TestPage<Object>> response = testRestTemplate.exchange(API_V_1_USERS, HttpMethod.GET, null, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getNumberOfElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("When one users receive users without PW")
    public void getUsers_whenThereIsUserInDB_receiveUserWithoutPW() {
        userRepository.save(TestUtil.createUser());
        ResponseEntity<TestPage<Map<String, Object>>> response = getUsers(new ParameterizedTypeReference<TestPage<Map<String, Object>>>() {
        });
        Map<String, Object> entity = response.getBody().getContent().get(0);
        assertThat(entity.containsKey("password")).isFalse();
    }

    @Test
    @DisplayName("When 3 iterm required in page and DB has 20 items return 3 pages")
    public void getUser_whenPageIsRequestedFor3ItemsPerPageWhereTheDatabaseHas20items_receive3Users() {
        IntStream.rangeClosed(1, 20).mapToObj(i -> "test-user-" + i)
                .map(TestUtil::createUserWithUsername)
                .forEach(userRepository::save);
        String path = API_V_1_USERS + "?page=0&size=3";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getContent().size()).isEqualTo(3);
    }

    @Test
    public void getUsers_whenPageSizeNotProvided_receivePageSizeAs10() {
        ResponseEntity<TestPage<Object>> response = getUsers(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageSizeIsGreaterThan100_receivePageSizeAs100() {
        String path = API_V_1_USERS + "?size=500";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(100);
    }

    @Test
    public void getUsers_whenPageSizeIsNegative_receivePageSizeAs10() {
        String path = API_V_1_USERS + "?size=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getSize()).isEqualTo(10);
    }

    @Test
    public void getUsers_whenPageIsNegative_receiveFirstPage() {
        String path = API_V_1_USERS + "?page=-5";
        ResponseEntity<TestPage<Object>> response = getUsers(path, new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("Get ok status when get user by name success")
    public void getUserByUsername_whenUserExists_receiveOk() {
        String username = "test-user";
        userService.saveUser(TestUtil.createUserWithUsername(username));
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Get ok status when get user by name success")
    public void getUserByUsername_whenUserExists_receiveUserWithoutPW() {
        String username = "test-user";
        userService.saveUser(TestUtil.createUserWithUsername(username));
        ResponseEntity<String> response = getUser(username, String.class);
        assertThat(response.getBody().contains("password")).isFalse();
    }

    @Test
    @DisplayName("Get Not found status when get user by name not found")
    public void getUserByUsername_whenUserDoesNotExists_receiveNotFound() {
        String username = "test-user";
        ResponseEntity<Object> response = getUser(username, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Get Not found status when get user by name not found")
    public void getUserByUsername_whenUserDoesNotExists_receiveApiError() {
        String username = "test-user";
        ResponseEntity<ApiError> response = getUser(username, ApiError.class);
        assertThat(response.getBody().getUrl()).isEqualTo(API_V_1_USERS + "/" + username);
        assertThat(response.getBody().getMessage()).isEqualTo("test-user is not found");
    }

    @Test
    @DisplayName("Get UnAuthorized when not authenticated")
    public void updateUser_notAuthenticated_getUnAuthorized() {
        ResponseEntity<Object> response = putUser(1, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("When update for another user receive forbidden")
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveForbidden() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        long anotherUserId = user.getId() + 123;
        ResponseEntity<Object> response = putUser(anotherUserId, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("When unauthorized receive ApiError")
    public void putUser_whenUnauthorizedUserSendsTheRequest_receiveApiError() {
        ResponseEntity<ApiError> response = putUser(123, null, ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/123");
    }

    @Test
    @DisplayName("When update another profile receive ApiError")
    public void putUser_whenAuthorizedUserSendsUpdateForAnotherUser_receiveApiError() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        long anotherUserId = user.getId() + 123;
        ResponseEntity<ApiError> response = putUser(anotherUserId, null, ApiError.class);
        assertThat(response.getBody().getUrl()).contains("users/" + anotherUserId);
    }

    @Test
    @DisplayName("Authorized updates receive Ok")
    public void putUser_whenAuthorizedUserUpdate_receiveOk() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM userUpdateVM = TestUtil.createUserUpdateVM();
        HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
        ResponseEntity<Object> response = putUser(user.getId(), request, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    @DisplayName("Verify updated DisplayName")
    public void putUser_whenAuthorizedUserUpdate_verifyUpdates() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM userUpdateVM = TestUtil.createUserUpdateVM();
        HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
        putUser(user.getId(), request, Object.class);
        User userInDB = userRepository.findByUsername("user1");
        assertThat(userInDB.getDisplayName()).isEqualTo(userUpdateVM.getDisplayName());
    }

    @Test
    @DisplayName("Verify the given display with the returned one")
    public void putUser_WhenUpdatesDisplayName_returnsUserVMWithTheSameDisplayName() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM userUpdateVM = TestUtil.createUserUpdateVM();
        HttpEntity<UserUpdateVM> request = new HttpEntity<>(userUpdateVM);
        ResponseEntity<UserVM> response = putUser(user.getId(), request, UserVM.class);
        assertThat(userUpdateVM.getDisplayName()).isEqualTo(response.getBody().getDisplayName());
    }

    @Test
    @DisplayName("update with null displayName")
    public void putUser_withInValidRequestBodyWithoutDisplayName_receiveBadRequest() throws IOException {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = new UserUpdateVM();
        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserVM> response = putUser(user.getId(), requestEntity, UserVM.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("update with displayName < 4")
    public void putUser_withUpdatedDisplayNameLessThanFour_receiveBadRequest() throws IOException {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = new UserUpdateVM();
        updatedUser.setDisplayName("abs");
        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserVM> response = putUser(user.getId(), requestEntity, UserVM.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("update with displayName > 255")
    public void putUser_withUpdatedDisplayNameOutOfTheLimits_receiveBadRequest() throws IOException {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        UserUpdateVM updatedUser = new UserUpdateVM();
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        updatedUser.setDisplayName(valueOf256Chars);
        HttpEntity<UserUpdateVM> requestEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<UserVM> response = putUser(user.getId(), requestEntity, UserVM.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Unauthorized delete")
    public void deleteUser_unauthorizedUser_receiveNotAuthorized() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        ResponseEntity<Object> response = testRestTemplate.exchange(API_V_1_USERS+"/"+123, HttpMethod.DELETE, null, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Unauthorized delete")
    public void deleteUser_authorizedUser_receiveOkStatus() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        ResponseEntity<Object> response = deleteUser(user.getId(), Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("authorized delete for another user")
    public void deleteUser_authorized_receiveOkStatus() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        long anotherUserId = user.getId() + 123;
        ResponseEntity<Object> response = deleteUser(anotherUserId, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("When user is deleted successfully")
    public void deleteUser_authorized_userIsDeleted() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
        deleteUser(user.getId(), Object.class);
        long usersInDB = userRepository.count();
        assertThat(usersInDB).isEqualTo(0);
    }

    @Test
    @DisplayName("When user deleted receive message")
    public void deleteUser_authorized_receiveGenericResponse() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user1"));
        authenticate(user.getUsername());
         ResponseEntity<GenericResponse> response = deleteUser(user.getId(), GenericResponse.class);
        assertThat(response.getBody().getMessage()).isEqualTo("The user was deleted successfully");
    }

    public <T> ResponseEntity<T> postSignup(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_V_1_USERS, request, response);
    }

    public <T> ResponseEntity<T> getUsers(ParameterizedTypeReference<T> response) {
        return testRestTemplate.exchange(API_V_1_USERS, HttpMethod.GET, null, response);
    }

    public <T> ResponseEntity<T> getUsers(String path, ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(path, HttpMethod.GET, null, responseType);
    }

    public <T> ResponseEntity<T> getUser(String username, Class<T> response) {
        return testRestTemplate.getForEntity(API_V_1_USERS + "/" + username, response);
    }

    public <T> ResponseEntity<T> putUser(long id, HttpEntity<?> requestEntity, Class<T> responseType) {
        String path = API_V_1_USERS + "/" + id;
        return testRestTemplate.exchange(path, HttpMethod.PUT, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> deleteUser(long id, Class<T> responseType) {
        String path = API_V_1_USERS + "/" + id;
        return testRestTemplate.exchange(path, HttpMethod.DELETE,null, responseType);
    }

    private void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }
}
