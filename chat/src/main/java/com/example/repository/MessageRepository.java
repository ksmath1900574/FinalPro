package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.model.Message;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(String sender);
    List<Message> findByReceiver(String receiver);
    List<Message> findByReceiverAndIsReadFalse(String receiver); // 메서드 이름 변경
}
