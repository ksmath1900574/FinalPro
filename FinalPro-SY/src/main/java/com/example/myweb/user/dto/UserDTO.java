package com.example.myweb.user.dto;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String nickname;

    // 기본 생성자
    public UserDTO() {}

    // 매개변수가 있는 생성자
    public UserDTO(Long id, String username, String email, String role, String nickname) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.nickname = nickname;
    }

    // Getters와 Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
