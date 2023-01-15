package com.bachar.socialapp.project.postIntegrationTest;


import com.bachar.socialapp.project.Post.Post;
import com.bachar.socialapp.project.Post.PostRepository;
import com.bachar.socialapp.project.Post.PostService;
import com.bachar.socialapp.project.error.ApiError;
import com.bachar.socialapp.project.shared.GenericResponse;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserRepository;
import com.bachar.socialapp.project.user.UserService;
import com.bachar.socialapp.project.TestUtil;
import com.bachar.socialapp.project.user.vm.PostVM;
import com.bachar.socialapp.project.userIntegrationTest.TestPage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class postControllerTest {

    public static final String API_V_1_POSTS = "/api/v1/posts";
    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostService postService;

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    public void cleanup() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
    }


    @Test
    @DisplayName("Create post receives Ok")
    public void createPost_whenPostIsValidAndUserAuthenticated_receiveStatusOk() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        ResponseEntity<Object> response = createPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Unauthorized receives UNAUTHORIZED")
    public void createPost_whenUserUnauthorized_receiveStatusUnauthorized() {
        userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        Post post = TestUtil.createPost();
        ResponseEntity<Object> response = createPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Unauthorized receive ApiError")
    public void createPost_whenUserUnauthorized_receiveApiError() {
        userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        Post post = TestUtil.createPost();
        ResponseEntity<ApiError> response = createPost(post, ApiError.class);
        assertThat(response.getBody().getMessage()).isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Save Post to Database")
    public void createPost_whenUserUnauthorizedCreatePost_beSavedInDatabase() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        testRestTemplate.postForEntity(API_V_1_POSTS, post, Object.class);
        assertThat(postRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Save Post with timestamp")
    public void createPost_whenUserAuthorizedCreatePost_beSavedInDatabaseWithTimeStamp() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        createPost(post, Object.class);
        Post postInDB = postRepository.findAll().get(0);
        assertThat(postInDB.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Receive Bad Request when Null Post")
    public void createPost_whenNullPost_receiveBadRequest() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        ResponseEntity<Object> response = createPost(new Post(), Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Post < 10 receive Bad Request")
    public void createPost_whenUserAuthorizedAndPostLessThan10_receiveBadRequest() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        Post post = new Post();
        post.setContent("123ert");
        ResponseEntity<Object> response = createPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Post = 5000 char receives OK")
    public void createPost_whenUserAuthorizedAnd5000CharPost_receiveOk() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        String longString = IntStream.rangeClosed(1, 5000).mapToObj(i -> "x").collect(Collectors.joining());
        Post post = new Post();
        post.setContent(longString);
        ResponseEntity<Object> response = createPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Post > 5000 char receives Bad Request")
    public void createPost_whenUserAuthorizedAndPostMoreThan5000Char_receiveBadRequest() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate(user.getUsername());
        String longString = IntStream.rangeClosed(1, 5001).mapToObj(i -> "x").collect(Collectors.joining());
        Post post = new Post();
        post.setContent(longString);
        ResponseEntity<Object> response = createPost(post, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Receive validationErrors when invalid Post")
    public void createPost_whenNullPost_receiveApiErrorWithValidationErrors() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        ResponseEntity<ApiError> response = createPost(new Post(), ApiError.class);
        assertThat(response.getBody().getValidationErrors()).containsKey("content");
    }

    @Test
    @DisplayName("Receive User Information")
    public void createPost_whenPostValidAndUserAuthorized_receiveWithPostUserInformation() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        createPost(post, Object.class);
        Post postInDB = postRepository.findAll().get(0);
        assertThat(postInDB.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Receive User Information")
    public void createPost_whenPostValidAndUserAuthorized_receiveWithPostVM() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        ResponseEntity<PostVM> response = createPost(post, PostVM.class);
        assertThat(response.getBody().getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("Receive User Information")
    public void createPost_whenPostValidAndUserAuthorized_accessPostFromUserEntity() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        Post post = TestUtil.createPost();
        createPost(post, Object.class);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User userInDB = entityManager.find(User.class, user.getId());
        assertThat(userInDB.getPosts().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("List empty receive Ok")
    public void getPosts_ifListOfPostsEmpty_receiveOkStatus() {
        ResponseEntity<Object> response = getAllPosts(new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("List empty receive Ok")
    public void getPosts_ifListOfPostsEmpty_receivePageWithZeroItems() {
        ResponseEntity<TestPage<Object>> response = getAllPosts(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("List not empty receive Page with items")
    public void getPosts_ifListNotEmpty_receivePageWithItems() {
        User user = userService.saveUser(TestUtil.createUser());
        postService.savePost(user, TestUtil.createPost());
        postService.savePost(user, TestUtil.createPost());
        postService.savePost(user, TestUtil.createPost());
        ResponseEntity<TestPage<Object>> response = getAllPosts(new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("List not empty receive Page with PostVM")
    public void getPosts_ifListNotEmpty_receivePageWithPostVM() {
        User user = userService.saveUser(TestUtil.createUser());
        postService.savePost(user, TestUtil.createPost());
        ResponseEntity<TestPage<PostVM>> response = getAllPosts(new ParameterizedTypeReference<TestPage<PostVM>>() {
        });
        PostVM storedPostVM = response.getBody().getContent().get(0);
        assertThat(storedPostVM.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("receive ok when user exists")
    public void getUserPosts_whenUserExists_receiveOk() {
        User user = userService.saveUser(TestUtil.createUser());
        ResponseEntity<TestPage<Object>> response = getUserPosts(user.getUsername(), new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("receive not found when user doesn't exist")
    public void getUserPosts_whenUserDoesNotExist_receiveNotFound() {
        ResponseEntity<Object> response = getUserPosts("username", new ParameterizedTypeReference<Object>() {
        });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("receive Page with 0 elements")
    public void getUserPosts_whenUserExists_receivePageWithZeroElement() {
        User user = userService.saveUser(TestUtil.createUser());
        ResponseEntity<TestPage<Object>> response = getUserPosts(user.getUsername(), new ParameterizedTypeReference<TestPage<Object>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("receive Page with 0 elements")
    public void getUserPosts_whenUserExistsWithPost_receivePageWithPostVM() {
        User user = userService.saveUser(TestUtil.createUser());
        postService.savePost(user, TestUtil.createPost());
        ResponseEntity<TestPage<PostVM>> response = getUserPosts(user.getUsername(), new ParameterizedTypeReference<TestPage<PostVM>>() {
        });
        PostVM postVM = response.getBody().getContent().get(0);
        assertThat(postVM.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getBody().getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("receive Page with 0 elements")
    public void getUserPosts_whenUserExistsWithPost_receivePageWith3Elements() {
        User user = userService.saveUser(TestUtil.createUser());
        postService.savePost(user, TestUtil.createPost());
        postService.savePost(user, TestUtil.createPost());
        postService.savePost(user, TestUtil.createPost());
        ResponseEntity<TestPage<PostVM>> response = getUserPosts(user.getUsername(), new ParameterizedTypeReference<TestPage<PostVM>>() {
        });
        assertThat(response.getBody().getTotalElements()).isEqualTo(3);
    }

    @Test
    @DisplayName("receive Page with 0 elements")
    public void getUserPosts_whenUserExistsWithPosts_receivePageWith3Elements() {
        User userWithThreePosts = userService.saveUser(TestUtil.createUserWithUsername("user-3"));
        IntStream.rangeClosed(1, 3).forEach(i -> postService.savePost(userWithThreePosts, TestUtil.createPost()));

        User userWithFivePosts = userService.saveUser(TestUtil.createUserWithUsername("user-5"));
        IntStream.rangeClosed(1, 5).forEach(i -> postService.savePost(userWithFivePosts, TestUtil.createPost()));

        ResponseEntity<TestPage<PostVM>> response = getUserPosts(userWithFivePosts.getUsername(), new ParameterizedTypeReference<TestPage<PostVM>>() {
        });

        assertThat(response.getBody().getTotalElements()).isEqualTo(5);
    }

    @Test
    @DisplayName("user unauthorized receive unauthorized")
    public void deletePost_whenUserUnauthorized_receiveUnauthorized() {
        ResponseEntity<Object> response = deletePost(555, Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("user authorized receive OK")
    public void deletePost_whenUserAuthorized_receiveOk() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        PostVM post = postService.savePost(user,TestUtil.createPost());
        ResponseEntity<Object> response = deletePost(post.getId(), Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Receive Generic message when deleted")
    public void deletePost_whenUserAuthorized_receiveGenericResponse() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        PostVM post = postService.savePost(user,TestUtil.createPost());
        ResponseEntity<GenericResponse> response = deletePost(post.getId(), GenericResponse.class);
        assertThat(response.getBody().getMessage()).contains("Post was deleted successfully");
    }

    @Test
    @DisplayName("delete post from database")
    public void deletePost_whenUserAuthorizedDeletePost_deletePostFromDatabase() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        PostVM post = postService.savePost(user,TestUtil.createPost());
        deletePost(post.getId(), Object.class);
        Optional<Post> postInDB = postRepository.findById(post.getId());
        assertThat(postInDB.isPresent()).isFalse();
        assertThat(postRepository.count()).isEqualTo(0);
    }
    @Test
    @DisplayName("Deleting user deletes all his posts")
    public void deletePost_whenUserDeleted_deleteAllHisPosts() {
        User user = userService.saveUser(TestUtil.createUser());
        authenticate(user.getUsername());
        IntStream.rangeClosed(1,5).forEach(i -> postService.savePost(user, TestUtil.createPost()));
        postService.savePost(user,TestUtil.createPost());
        userService.deleteUser(user.getId());
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Deleting posts of others receive forbidden")
    public void deletePost_deletingThePostsOfOthers_receiveForbidden() {
        User user = userService.saveUser(TestUtil.createUserWithUsername("user-1"));
        authenticate("user-1");
        User userOwner = userService.saveUser(TestUtil.createUserWithUsername("user-2"));
        PostVM post = postService.savePost(userOwner, TestUtil.createPost());
        ResponseEntity<Object> response = deletePost(post.getId(), Object.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }


    @AfterEach
    public void cleanupAfter() {
        postRepository.deleteAll();
    }

    public <T> ResponseEntity<T> createPost(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_V_1_POSTS, request, response);
    }

    public <T> ResponseEntity<T> getAllPosts(ParameterizedTypeReference<T> response) {
        return testRestTemplate.exchange(API_V_1_POSTS, HttpMethod.GET, null, response);
    }

    public <T> ResponseEntity<T> getUserPosts(String username, ParameterizedTypeReference<T> response) {
        String path = "/api/v1/users/" + username + "/posts";
        return testRestTemplate.exchange(path, HttpMethod.GET, null, response);
    }

    public <T> ResponseEntity<T> deletePost(long postId, Class<T> response) {
        String path = API_V_1_POSTS + "/" + postId;
        return testRestTemplate.exchange(path, HttpMethod.DELETE, null, response);
    }

    public void authenticate(String username) {
        testRestTemplate.getRestTemplate().getInterceptors().add(new BasicAuthenticationInterceptor(username, "P4ssword"));
    }
}
