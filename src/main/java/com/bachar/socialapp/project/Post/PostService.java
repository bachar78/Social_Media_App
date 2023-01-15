package com.bachar.socialapp.project.Post;

import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserService;
import com.bachar.socialapp.project.user.vm.PostVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    public PostService(PostRepository postRepository, UserService userService) {
        this.postRepository = postRepository;
        this.userService = userService;
    }

    public PostVM savePost(User user, Post post) {
        post.setTimestamp(new Date());
        post.setUser(user);
        postRepository.save(post);
        return new PostVM(post);
    }

    public Page<PostVM> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(PostVM::new);
    }

    public Page<PostVM> getUserPosts(String username, Pageable pageable) {
       User user = userService.getByUsername(username);
       return postRepository.findByUser(user, pageable).map(PostVM::new);
    }

    public void deletePost(long postId) {
        postRepository.deleteById(postId);
    }
}
