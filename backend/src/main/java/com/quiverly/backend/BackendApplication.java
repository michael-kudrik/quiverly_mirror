package com.quiverly.backend;

import com.quiverly.backend.model.User;
import com.quiverly.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

    static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository, UserRepository userRepository) {
        return args -> {
//            User user = new User();
//            user.setUsername("testmike");
//            user.setEmail("mike@mikekudrik.boats");
//
//            userRepository.save(user);

            userRepository.findAll().forEach(u -> System.out.println(u.getId() + ": " + u.getUsername() + ", " + u.getEmail()));

        };
    }
}
