package com.example.lab5.framework.dto;

public class CreateUserRequest {
    private String login;
    private String password;
    private String role;

    public CreateUserRequest() {}

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}