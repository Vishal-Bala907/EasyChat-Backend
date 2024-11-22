package com.vb.easy.chat.modals;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class FileDTO {
	private String senderId;
	private String recipientId;
	private String message;
	private String sentBy;
	private MessageType type;
	private String viewer1;
	private String viewer2;
	private MultipartFile file;
}
