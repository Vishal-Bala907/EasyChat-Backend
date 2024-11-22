package com.vb.easy.chat.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vb.easy.chat.modals.ChatRoom;

@Repository
public interface ChatRoomRepo extends JpaRepository<ChatRoom, String> {
	@Query("SELECT room FROM chatroom room WHERE room.roomId =:roomId")
	List<ChatRoom> findByRoomId(String roomId);
}
