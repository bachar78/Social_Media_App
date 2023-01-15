package com.bachar.socialapp.project.user;

import com.bachar.socialapp.project.shared.CurrentUser;
import com.bachar.socialapp.project.user.vm.UserVM;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
public class LoginController {

    @PostMapping
    UserVM handleLogin(@CurrentUser User loggedInUser) {
        return new UserVM(loggedInUser);
    }
}
