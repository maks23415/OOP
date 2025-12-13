package com.example.lab5.framework.controller;

import com.example.lab5.framework.dto.CreateUserRequest;
import com.example.lab5.framework.dto.UserDTO;
import com.example.lab5.framework.entity.User;
import com.example.lab5.framework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody CreateUserRequest request) {
        try {
            User user = userService.createUser(
                    request.getLogin(),
                    request.getRole(),
                    request.getPassword()
            );

            UserDTO response = new UserDTO();
            response.setId(user.getId());
            response.setLogin(user.getLogin());
            response.setRole(user.getRole());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}