package com.vb.easy.chat.modals;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "chats")
@NoArgsConstructor
@Data
public class ChatMessage {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String senderId;
	private String recipientId;
	private String message;
	private String sentBy;
	private MessageType type;
	private String viewer1;
	private String viewer2;
	
	@Transient
	private MultipartFile file;

	private long timeStamp;
	private String date;
	private String time;

}
