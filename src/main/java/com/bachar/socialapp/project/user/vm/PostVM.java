package com.bachar.socialapp.project.user.vm;


import com.bachar.socialapp.project.Post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class PostVM {

    private long id;
    private String content;
    private long date;
    private UserVM user;

    public PostVM(Post post) {
        this.setId(post.getId());
        this.setContent(post.getContent());
        this.setDate(post.getTimestamp().getTime());
        this.user = new UserVM(post.getUser());
    }
}
