package com.bachar.socialapp.project.user;

import com.bachar.socialapp.project.error.ApiError;
import com.bachar.socialapp.project.error.NotFoundException;
import com.bachar.socialapp.project.shared.GenericResponse;
import com.bachar.socialapp.project.user.vm.UserUpdateVM;
import com.bachar.socialapp.project.user.vm.UserVM;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.aspectj.apache.bcel.classfile.Module;
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
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    GenericResponse createUser(@Valid @RequestBody User user) {
        userService.saveUser(user);
        return new GenericResponse("User Saved");
    }

    @GetMapping
    Page<UserVM> getUsers(Pageable pageable){
        return userService.getUsers(pageable).map(UserVM::new);
    }

    @GetMapping("/{username}")
    UserVM getUser(@PathVariable String username) {
        return new UserVM(userService.getByUsername(username));
    }

    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    UserVM updateUser(@PathVariable long id, @Valid @RequestBody(required = false) UserUpdateVM userUpdated) {
        return userService.updateUser(id, userUpdated);
    }

    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("#id == principal.id")
    GenericResponse deleteUser(@PathVariable long id) {
        return userService.deleteUser(id);
    }
}
