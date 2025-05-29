package com.wheelpicker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wheelpicker.model.Role;
import com.wheelpicker.model.User;
import net.datafaker.Faker;

import java.io.File;
import java.io.IOException;
import java.util.*;

// Generates User sample to populate the database with mocked data
public class FakeDataGenerator {
    Faker faker = new Faker();
    List<User> users = new ArrayList<>();
    Set<String> usedUsernames = new HashSet<>();
    Set<String> usedEmails = new HashSet<>();


    public void generateFakeData() throws IOException {
        for (int i = 0; i < 4000; i++) {
            User user = new User();

            String username;
            String email;
            user.setId(UUID.randomUUID());
            user.setPassword(faker.internet().password());
            user.setRole(Role.ROLE_USER);
            user.setIsEnabled(faker.bool().bool());

            do {
                username = faker.internet().username() + faker.number().randomNumber(5);
            }
            while (usedUsernames.contains(username));
            usedUsernames.add(username);

            do {
                email = faker.internet().username() + "@" + faker.internet().domainName();
            }
            while (usedEmails.contains(email));
            usedEmails.add(username);

            user.setUsername(username);
            user.setEmail(email);

            users.add(user);
        }


        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("src/test/resources/mock-users.json"), users);

    }

    public static void main(String[] args) throws IOException {
        new FakeDataGenerator().generateFakeData();
    }
}
