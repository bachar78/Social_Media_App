package com.bachar.socialapp.project;

import com.bachar.socialapp.project.Post.Post;
import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.vm.UserUpdateVM;

public class TestUtil {
    public static User createUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");
        return user;
    }

    public static User createUserWithUsername(String username) {
        User user = createUser();
        user.setUsername(username);
        return user;
    }

    public static UserUpdateVM createUserUpdateVM() {
        UserUpdateVM userUpdateVM = new UserUpdateVM();
        userUpdateVM.setDisplayName("new displayName");
        return userUpdateVM;
    }

    public static Post createPost() {
        Post post = new Post();
        post.setContent("This post is for testing purposes");
        return post;
    }


}
