package com.example.lab5.manual.dto;

import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String login;
    private String role;
    private String password;


    public UserDTO() {}

    public UserDTO(String login, String role, String password) {
        this.login = login;
        this.role = role;
        this.password = password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }


    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}