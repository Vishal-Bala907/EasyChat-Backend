package com.vb.easy.chat.modals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@Entity(name="chatroom")
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String roomId;
	private String senderId;
	private String recipientId;
	public ChatRoom(String roomId, String senderId, String recipientId) {
		super();
		this.roomId = roomId;
		this.senderId = senderId;
		this.recipientId = recipientId;
	}
}
