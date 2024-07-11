package com.example.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.model.Message;
import com.example.model.User;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    // 회원가입 페이지로 이동
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signup(@RequestParam String username, @RequestParam String email, @RequestParam String password, @RequestParam String role, @RequestParam String nickname, Model model) {
        // 중복 체크
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

        // 새로운 사용자 생성 및 저장
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setNickname(nickname);
        userRepository.save(user);
        return "redirect:/";
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return "redirect:/welcome?username=" + username + "&nickname=" + user.getNickname();
        }
        model.addAttribute("error", "비밀번호가 틀립니다.");
        return "login";
    }

    // 중복 체크 API
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

    // 사용자 정보 페이지로 이동
    @GetMapping("/user/info")
    public String userInfo(@RequestParam String username, Model model) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            model.addAttribute("user", user);
            return "userinfo";
        }
        return "redirect:/login";
    }

    // 메시지 보내기 페이지로 이동
    @GetMapping("/message/send")
    public String sendMessagePage(@RequestParam String username, Model model) {
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equals(username))
                .collect(Collectors.toList());
        model.addAttribute("users", users);
        model.addAttribute("username", username);
        return "sendMessage";
    }

    // 메시지 보내기 처리
    @PostMapping("/message/send")
    public String sendMessage(@RequestParam String sender, @RequestParam String receiver, @RequestParam String title, @RequestParam String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTitle(title);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        return "redirect:/message/sent?username=" + sender;
    }

    // 보낸 메시지 보기
    @GetMapping("/message/sent")
    public String viewSentMessages(@RequestParam String username, Model model) {
        List<Message> sentMessages = messageRepository.findBySender(username);
        model.addAttribute("username", username);
        model.addAttribute("messages", sentMessages);
        return "viewSentMessages";
    }

    // 받은 메시지 보기
    @GetMapping("/message/received")
    public String viewReceivedMessages(@RequestParam String username, Model model) {
        List<Message> receivedMessages = messageRepository.findByReceiver(username);
        model.addAttribute("messages", receivedMessages);
        return "viewReceivedMessages";
    }

    // 메시지 상세 페이지로 이동
    @GetMapping("/message/detail")
    public String messageDetail(@RequestParam Long id, @RequestParam String username, Model model) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setRead(true);
            messageRepository.save(message);
            model.addAttribute("message", message);
            model.addAttribute("currentUsername", username);
            return "messageDetail";
        }
        return "redirect:/messageBox?username=" + username;
    }

    // 메시지 답장 페이지로 이동
    @GetMapping("/message/reply")
    public String replyMessagePage(@RequestParam String sender, @RequestParam String receiver, @RequestParam String title, @RequestParam String originalContent, Model model) {
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("title", title);
        model.addAttribute("originalContent", originalContent); 
        return "replyMessage";
    }

    // 메시지 답장 처리
    @PostMapping("/message/reply")
    public String replyMessage(@RequestParam String sender, @RequestParam String receiver, @RequestParam String title, @RequestParam String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setTitle(title);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);
        return "redirect:/messageBox?username=" + sender;
    }

    // 메시지함 페이지로 이동
    @GetMapping("/messageBox")
    public String messageBox(Model model, @RequestParam String username) {
        model.addAttribute("username", username);
        model.addAttribute("receivedMessages", messageRepository.findByReceiver(username));
        model.addAttribute("sentMessages", messageRepository.findBySender(username));
        return "messageBox";
    }

    // 환영 페이지로 이동
    @GetMapping("/userWelcome")
    public String welcomePage(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "welcome";
    }

    // 새 메시지 확인 API
    @GetMapping("/message/check-new")
    @ResponseBody
    public Map<String, Boolean> checkNewMessages(@RequestParam String username) {
        List<Message> newMessages = messageRepository.findByReceiverAndIsReadFalse(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasNewMessages", !newMessages.isEmpty());
        return response;
    }
}
