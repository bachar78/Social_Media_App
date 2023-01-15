package com.bachar.socialapp.project.configuration;

import com.bachar.socialapp.project.Post.Post;
import com.bachar.socialapp.project.Post.PostRepository;
import com.bachar.socialapp.project.user.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostSecurityService {

    PostRepository postRepository;

    public PostSecurityService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public boolean isAllowedToDelete(long postId, User loggedInUser) {
        Optional<Post> postInDB = postRepository.findById(postId);
        if(postInDB.isPresent()) {
            return postInDB.get().getUser().getId()==loggedInUser.getId();
        }
        return false;

    }
}
