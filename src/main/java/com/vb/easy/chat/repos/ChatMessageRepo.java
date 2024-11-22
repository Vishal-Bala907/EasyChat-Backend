package com.vb.easy.chat.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.vb.easy.chat.modals.ChatMessage;

public interface ChatMessageRepo extends JpaRepository<ChatMessage, String> {

	List<ChatMessage> findBySenderIdAndRecipientId(String senderId, String recipientId);
	
	@Query("SELECT c FROM chats c WHERE c.viewer1 =:viewer OR c.viewer2 =:viewer")
	List<ChatMessage> findByViewer(String viewer);
	
	@Modifying
	@Query("DELETE FROM chats c WHERE c.senderId =:sender AND c.recipientId =:receiver")
	void deleteChats(String sender , String receiver);
	
}
