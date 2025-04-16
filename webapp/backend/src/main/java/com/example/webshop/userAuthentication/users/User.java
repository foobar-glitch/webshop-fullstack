package com.example.webshop.userAuthentication.users;

public class User {
    private Long userId;
    private String username;
    private String email;
    private String role;

    public User(Long userId, String username, String email, String role){
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin(){
        return role.equals("admin");
    }

    public boolean isUser(){
        return role.equals("user");
    }

}
