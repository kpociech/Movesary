package com.consoul.movesary.security;

import com.consoul.movesary.dtos.UserDTO;
import com.consoul.movesary.repositories.UserRepository;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@Configuration
public class CurrentUserProvider {

    private static Logger log = LoggerFactory.getLogger(CurrentUserProvider.class);

    public static AccessToken getAccessToken() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && KeycloakAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
            KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;

            final Principal principal = (Principal) token.getPrincipal();

            if (principal instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
                return kPrincipal.getKeycloakSecurityContext()
                                 .getToken();
            }
            log.info("Cannot get username because no user is logged in");
        }
        return null;
    }


    public static UserDTO getCurrentUser() throws ClassCastException{
        UserDTO userDTO = new UserDTO();
        AccessToken accessToken = getAccessToken();
        if(accessToken != null) {
            userDTO.setUsername(accessToken.getPreferredUsername());
            userDTO.setEmail(accessToken.getEmail());
            userDTO.setFullName(accessToken.getName());
        }
        return userDTO;
    }

    public static boolean isAdmin() {
        Optional<String> role = getAccessToken().getRealmAccess().getRoles().stream()
                                               .filter(s -> s.equals("app-admin"))
                                               .findAny();
        return role.isPresent();
    }

}
