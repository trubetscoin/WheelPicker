package com.wheelpicker.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wheelpicker.model.User;
import com.wheelpicker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.util.List;

// Used for manual testing. Populates users table with the User model data
//@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public DataSeeder(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            System.out.println("Database already populated. Skipping seeding.");
            return;
        }

        File file = new File("src/test/resources/mock-users.json");
        List<User> users = objectMapper.readValue(
                file,
                new TypeReference<List<User>>() {}
        );

        userRepository.saveAll(users);
        System.out.println("Database seeded with " + users.size() + " users.");
    }
}
