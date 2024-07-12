package com.example.myweb.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatPageController {

    @GetMapping("/chat")
    public String chatPage(@RequestParam String sender, @RequestParam String receiver, Model model) {
        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        return "chat/chat";
    }
}
