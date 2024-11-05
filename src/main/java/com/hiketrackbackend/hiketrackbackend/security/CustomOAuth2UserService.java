package com.hiketrackbackend.hiketrackbackend.security;

import com.hiketrackbackend.hiketrackbackend.model.user.Role;
import com.hiketrackbackend.hiketrackbackend.model.user.User;
import com.hiketrackbackend.hiketrackbackend.repository.RoleRepository;
import com.hiketrackbackend.hiketrackbackend.repository.UserRepository;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");

        if (!userRepository.existsUserByEmail(email)) {
            saveUserToDB(oauth2User);
        }
        return oauth2User;
    }

    private void saveUserToDB(OAuth2User oauth2User) {
        User user = new User();
        user.setEmail(oauth2User.getAttribute("email"));
        user.setFirstName(oauth2User.getAttribute("given_name"));
        if (oauth2User.getAttribute("family_name") == null) {
            user.setLastName("");
        }
        user.setLastName(oauth2User.getAttribute("family_name"));
        user.setPassword(encoder.encode(UUID.randomUUID().toString()));
        setUserRole(user);
        userRepository.save(user);
    }

    private void setUserRole(User user) {
        Role roleUser = roleRepository.findByName(Role.RoleName.ROLE_USER);
        user.setRoles(Set.of(roleUser));
    }
}
