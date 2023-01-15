package com.bachar.socialapp.project.Post;


import com.bachar.socialapp.project.error.ApiError;
import com.bachar.socialapp.project.shared.CurrentUser;
import com.bachar.socialapp.project.shared.GenericResponse;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.vm.PostVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class PostController {


    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    PostVM createPost(@Valid @RequestBody Post post, @CurrentUser User user) {
        return postService.savePost(user, post);
    }

    @GetMapping("/posts")
    Page<PostVM> getAllPosts(Pageable pageable) {
       return postService.getAllPosts(pageable);
    }

    @GetMapping("/users/{username}/posts")
    Page<PostVM> getUserPosts(@PathVariable String username, Pageable pageable) {
       return postService.getUserPosts(username, pageable);
    }

    @DeleteMapping("/posts/{postId:[0-9]+}")
    @PreAuthorize("@postSecurityService.isAllowedToDelete(#postId, principal)")
    GenericResponse deletePost(@PathVariable long postId) {
        postService.deletePost(postId);
        return new GenericResponse("Post was deleted successfully");
    }
}
