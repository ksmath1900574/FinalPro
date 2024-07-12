package com.example.myweb.chat.controller;

import com.example.myweb.chat.model.ChatMessage;
import com.example.myweb.chat.model.ChatRoom;
import com.example.myweb.chat.repository.ChatMessageRepository;
import com.example.myweb.chat.repository.ChatRoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        String roomId = generateRoomId(chatMessage.getSender(), chatMessage.getReceiver());

        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomId(roomId);
        ChatRoom chatRoom;

        if (chatRoomOpt.isEmpty()) {
            chatRoom = new ChatRoom();
            chatRoom.setRoomId(roomId);
            chatRoom.setSender(chatMessage.getSender());
            chatRoom.setReceiver(chatMessage.getReceiver());
            chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = chatRoomOpt.get();
        }

        chatMessage.setRoomId(roomId);
        chatMessage.setTimestamp(LocalDateTime.now().toString());
        chatMessageRepository.save(chatMessage);

        messagingTemplate.convertAndSend("/topic/" + roomId, chatMessage);
        updateChatList(chatMessage.getSender());
        updateChatList(chatMessage.getReceiver());
    }

    @GetMapping("/chat/messages")
    public List<ChatMessage> getMessages(@RequestParam String sender, @RequestParam String receiver) {
        String roomId = generateRoomId(sender, receiver);
        return chatMessageRepository.findByRoomId(roomId);
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoomDto> getChatRooms(@RequestParam String username) {
        List<ChatRoom> rooms = chatRoomRepository.findBySenderOrReceiver(username, username);
        return rooms.stream().map(room -> {
            String roomId = room.getRoomId();
            ChatMessage latestMessage = chatMessageRepository.findTopByRoomIdOrderByTimestampDesc(roomId);
            return new ChatRoomDto(room, latestMessage != null ? latestMessage.getContent() : "최근 메시지 없음");
        }).collect(Collectors.toList());
    }

    private String generateRoomId(String sender, String receiver) {
        return sender.compareTo(receiver) > 0 ? sender + "_" + receiver : receiver + "_" + sender;
    }

    private void updateChatList(String username) {
        List<ChatRoomDto> chatRooms = getChatRooms(username);
        messagingTemplate.convertAndSend("/topic/chat-list/" + username, chatRooms);
    }

    public static class ChatRoomDto {
        private ChatRoom room;
        private String latestMessage;

        public ChatRoomDto(ChatRoom room, String latestMessage) {
            this.room = room;
            this.latestMessage = latestMessage;
        }

        public ChatRoom getRoom() {
            return room;
        }

        public String getLatestMessage() {
            return latestMessage;
        }
    }
}
