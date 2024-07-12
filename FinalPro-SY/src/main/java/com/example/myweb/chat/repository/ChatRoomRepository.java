package com.example.myweb.chat.repository;

import com.example.myweb.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);
    List<ChatRoom> findBySenderOrReceiver(String sender, String receiver);
}
