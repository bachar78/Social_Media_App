package com.bachar.socialapp.project.Post;

import com.bachar.socialapp.project.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUser(User user, Pageable pageable);
//    Page<Post> findByUserUsername(String username, Pageable pageable);
}
