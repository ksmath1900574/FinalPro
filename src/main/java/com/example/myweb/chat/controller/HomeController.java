package com.example.myweb.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.myweb.user.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/")
//    public String home() {
//        return "home";
//    }
    
    
    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            username = "게스트"; // 닉네임이 없으면 기본값 설정
        }
        model.addAttribute("nickname", username); // 모델에 닉네임 저장
        model.addAttribute("users", userRepository.findAll()); // 모든 사용자 목록을 모델에 추가
        return "welcome";
    }
}