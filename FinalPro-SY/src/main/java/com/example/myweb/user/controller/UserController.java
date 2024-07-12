package com.example.myweb.user.controller;

import com.example.myweb.user.entity.UserEntity;
import com.example.myweb.user.repository.UserRepository;
import com.example.myweb.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String role, @RequestParam String nickname, Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "아이디가 이미 존재합니다.");
            return "signup";
        }
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "이메일이 이미 존재합니다.");
            return "signup";
        }
        if (userRepository.findByNickname(nickname) != null) {
            model.addAttribute("error", "별명이 이미 존재합니다.");
            return "signup";
        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password); // 평문 비밀번호 저장 (추천하지 않음)
        user.setRole(role);
        user.setNickname(nickname);
        userRepository.save(user);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        UserEntity user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            try {
                String encodedNickname = URLEncoder.encode(user.getNickname(), StandardCharsets.UTF_8.toString());
                return "redirect:/userWelcome?nickname=" + encodedNickname;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("error", "비밀번호가 틀립니다.");
        return "login";
    }

    @GetMapping("/check-duplicate")
    @ResponseBody
    public Map<String, Boolean> checkDuplicate(@RequestParam String field, @RequestParam String value) {
        boolean exists = false;
        switch (field) {
            case "username":
                exists = userRepository.findByUsername(value) != null;
                break;
            case "email":
                exists = userRepository.findByEmail(value) != null;
                break;
            case "nickname":
                exists = userRepository.findByNickname(value) != null;
                break;
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    @GetMapping("/userWelcome")
    public String welcomePage(@RequestParam String nickname, Model model) {
        model.addAttribute("nickname", nickname);
        List<UserEntity> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                                      .map(user -> new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getNickname()))
                                      .collect(Collectors.toList());
        model.addAttribute("users", userDTOs);
        return "welcome";
    }
}
