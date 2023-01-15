package com.bachar.socialapp.project;

import com.bachar.socialapp.project.user.User;
import com.bachar.socialapp.project.user.UserRepository;
import com.bachar.socialapp.project.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.stream.IntStream;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        System.out.println("Application is running on port 8081");
    }

    @Bean
    @Profile("!test")
    CommandLineRunner run (UserService userService){
        return args -> IntStream.rangeClosed(1,15).mapToObj(i-> {
            User user = new User();
            user.setUsername("user"+i);
            user.setDisplayName("display"+i);
            user.setPassword("P4ssowrd");
            return user;
        }).forEach(userService::saveUser);
    }
}
