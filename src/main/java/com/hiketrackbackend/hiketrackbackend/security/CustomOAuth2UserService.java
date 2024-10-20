package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.model.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String email = oAuth2User.getAttribute("email");
        if (userRepository.existsUserByEmail(email)) {
            throw new OAuth2AuthenticationException("This email is already used: " + email);
        }
        saveUserToDB(oAuth2User);
        return oAuth2User;
    }

    // TODO make sure that profile is created when register via google
    private void saveUserToDB(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        setUserRole(user);
        userRepository.save(user);
    }

    private void setUserRole(User user) {
        Role roleUser = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(roleUser));
    }
}
