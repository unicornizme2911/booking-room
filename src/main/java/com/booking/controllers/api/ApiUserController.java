package com.booking.controllers.api;

import com.booking.authentication.AuthenticationResponse;
import com.booking.dto.response.UserResponse;
import com.booking.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ApiUserController {
    @Autowired
    private UserService userService;

    @PostMapping("/get-user")
    public ResponseEntity<UserResponse> getUser(@RequestBody String token) {
        return ResponseEntity.ok(userService.findUserByToken(token));
    }

    @PostMapping("/get-user-by-email")
    public ResponseEntity<AuthenticationResponse> getUserByEmail(@RequestBody Map<String,String> request) {
        var email = request.get("email");
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }


    @PostMapping("/get-all-user")
    public ResponseEntity<List<UserResponse>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}