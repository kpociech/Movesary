package com.consoul.movesary.aspects;

import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.models.User;
import com.consoul.movesary.repositories.UserRepository;
import com.consoul.movesary.security.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.keycloak.representations.AccessToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Aspect
@Component
public class AccountChangesAspect {

    UserRepository userRepository;
    String keycloakUsername;
    String keycloakName;
    String keycloakEmail;

    public AccountChangesAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //executes before any method in 'controllers.rest` package to check for changes in Keycloak's userAccount
    @Before("execution(* com.consoul.movesary.controllers.rest.*.*(..))")
    public void compareKeycloakAccountWithDB() {
        AccessToken accessToken = CurrentUserProvider.getAccessToken();
        if(accessToken != null) {
            keycloakUsername = accessToken.getPreferredUsername();
            Optional<User> optionalUser = userRepository.get(keycloakUsername);

            compare(accessToken, optionalUser);
        }
    }

    private boolean compare(AccessToken accessToken, Optional<User> optionalUser) {
        boolean different = false;

        keycloakEmail = accessToken.getEmail();
        keycloakName = accessToken.getName();

        if(optionalUser.isPresent()) {
            User user = optionalUser.get();

            if(!user.getEmail().equals(keycloakEmail)) {
                user.setEmail(keycloakEmail);
                different = true;
            }
            if(!user.getFullName().equals(keycloakName)) {
                user.setFullName(keycloakName);
                different = true;
            }

            if(different) {
                userRepository.update(user);
                log.info("Data of user: " + keycloakUsername + " was updated in DB");
            }
        }
        else{
            User createdUser = createUser(keycloakUsername, keycloakName, keycloakEmail);
            log.info("user: " + createdUser.getUsername() + " was added to DB");
        }
        return !different;
    }

    private User createUser(String keycloakUsername, String keycloakName, String keycloakEmail) {
        User user = new User(keycloakUsername, keycloakName, keycloakEmail);
        return userRepository.create(user);
    }
}
