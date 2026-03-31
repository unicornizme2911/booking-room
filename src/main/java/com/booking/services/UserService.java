package com.booking.services;

import com.booking.authentication.AuthenticationResponse;
import com.booking.configuration.ApplicationConfig;
import com.booking.dto.request.UserRequest;
import com.booking.dto.response.MessageResponse;
import com.booking.dto.response.UserResponse;
import com.booking.exception.UserNotFoundException;
import com.booking.models.Provider;
import com.booking.models.Role;
import com.booking.models.TokenModel;
import com.booking.models.UserModel;
import com.booking.repository.TokenRepository;
import com.booking.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    private ApplicationConfig applicationConfig;

    public UserResponse toResponse(UserModel user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .gender(user.getGender())
                .contact(user.getContact())
                .address(user.getAddress())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserModel user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user;
    }

    @Modifying
    public void updatePassword(UserModel user) {
        user.setPassword(applicationConfig.passwordEncoder().encode(user.getPassword()));
    }

    public UserResponse findUserByToken(String token) {
        var userToken = tokenRepository.findByToken(token).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (userToken != null) {
            var user = userRepository.findByEmail(userToken.getUsers().getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
            return toResponse(user);
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

//    public String findCartIdByToken(String token) {
//        String user_id = findUserByToken(token).getId();
//        return cartRepository.findByUserId(user_id).orElseThrow(() -> new UserNotFoundException("User not found")).getId();
//    }


    public MessageResponse updateUser(UserRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user != null) {
            user.setFirstname(request.getFirstName());
            user.setLastname(request.getLastName());
            user.setGender(request.getGender());
            user.setContact(request.getContact());
            user.setAddress(request.getAddress());
            user.setDate_updated(Date.from(java.time.Instant.now()));
            userRepository.save(user);
            return MessageResponse.builder()
                    .message("User updated")
                    .build();
        } else {
            return MessageResponse.builder()
                    .message("User not found")
                    .build();
        }
    }

    public List<UserResponse> getAllUsers() {
        var users = userRepository.findAll().stream().map(this::toResponse);
        return users.toList();
    }

    public MessageResponse deleteUserById(String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user != null) {
            List<TokenModel> tokens = tokenRepository.findAllByUsersId(id);
            tokenRepository.deleteAll(tokens);
            userRepository.delete(user);
        } else {
            throw new UserNotFoundException("User not found");
        }
        return MessageResponse.builder()
                .message("User deleted")
                .build();
    }

    public MessageResponse deleteAllUsers() {
        var users = userRepository.findAll();
        if (!users.isEmpty()) {
            tokenRepository.deleteAll();
            users.forEach(user -> {
//                cartService.deleteCartOfUser(user);
                userRepository.delete(user);
            });
            return MessageResponse.builder()
                    .message("All users deleted")
                    .build();
        } else {
            throw new UserNotFoundException("All Users not found");
        }
    }

    public boolean isExistUser(String email) {
        return userRepository.existsUserModelByEmail(email);
    }

    public void processOAuthPostLogin(String email, HttpServletResponse httpServletResponse) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user != null) {
            user.setDate_updated(Date.from(java.time.Instant.now()));
            var accessToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            authenticationService.revokeAllUserTokens(user);
            authenticationService.saveUserToken(user, refreshToken);
            authenticationService.createAccessTokenCookie(accessToken,  httpServletResponse);
            authenticationService.createRefreshTokenCookie(refreshToken, httpServletResponse);
            authenticationService.createCookieForRole(user.getRole().toString(), httpServletResponse);
            userRepository.save(user);
        }
    }

    public void processOAuthPostRegister(String email, HttpServletResponse response) {
        var user = UserModel.builder()
                .email(email)
                .provider(Provider.GOOGLE)
                .role(Role.USER)
                .avatar("/images/avtDefault.jpg")
                .date_created(Date.from(java.time.Instant.now()))
                .build();
        var savedUser = userRepository.save(user);
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        authenticationService.saveUserToken(savedUser, refreshToken);
        authenticationService.createAccessTokenCookie(accessToken, response);
        authenticationService.createRefreshTokenCookie(refreshToken, response);
        authenticationService.createCookieForRole(savedUser.getRole().toString(), response);
    }

    public AuthenticationResponse findUserByEmail(String email) {
        String tokenValid = "";
        var user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<TokenModel> tokens = tokenRepository.findAllValidTokenByUser(String.valueOf(user.getId()));
        for (TokenModel token : tokens) {
            tokenValid = token.getToken();
        }
        return AuthenticationResponse.builder()
                .refreshToken(tokenValid)
                .build();
    }
}
