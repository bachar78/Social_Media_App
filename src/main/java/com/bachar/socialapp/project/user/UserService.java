package com.bachar.socialapp.project.user;

import com.bachar.socialapp.project.error.NotFoundException;
import com.bachar.socialapp.project.shared.GenericResponse;
import com.bachar.socialapp.project.user.vm.UserUpdateVM;
import com.bachar.socialapp.project.user.vm.UserVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new NotFoundException(username + " is not found");
        }
        return user;
    }

    public UserVM updateUser(long id, UserUpdateVM userUpdated) {
        Optional<User> userInDB = userRepository.findById(id);
        userInDB.get().setDisplayName(userUpdated.getDisplayName());
        userRepository.save(userInDB.get());
        return new UserVM(userInDB.get());
    }

    public GenericResponse deleteUser(long id) {
        userRepository.deleteById(id);
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            throw new RuntimeException("Something went wrong");
        }
        return new GenericResponse("The user was deleted successfully");
    }
}
