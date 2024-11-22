package com.vb.easy.chat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.vb.easy.chat.modals.AddRequest;
import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.FileDTO;
import com.vb.easy.chat.modals.MyFriends;
import com.vb.easy.chat.repos.ReqRepository;
import com.vb.easy.chat.repos.UserRepository;
import com.vb.easy.chat.services.imple.ChatServiceImple;
import com.vb.easy.chat.services.imple.UserServiceImplementation;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
class UnfriendRequest {
	private String loggedInUSer;
	private String selectedUser;
}
@Component
@Controller
public class ChatController {
	@Autowired
	private ReqRepository reqRepository;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	@Autowired
	private ChatServiceImple chatService;
	@SuppressWarnings("unused")
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserServiceImplementation userService;
	
	@MessageMapping("/add-req")
	public void handleAddRequest(@Payload AddRequest addRequest) {
		System.out.println(addRequest);
		AddRequest requestAlreadyExists = reqRepository.isRequestAlreadyExists(addRequest.getS_userId(), addRequest.getR_userId());
		if(requestAlreadyExists == null) {
			AddRequest save = reqRepository.save(addRequest);
			messagingTemplate.convertAndSendToUser(addRequest.getR_userId(),"/add/req",save);		
		} else {	
			messagingTemplate.convertAndSendToUser(addRequest.getR_userId(), "/add/req", requestAlreadyExists);
		}
		
	}
	
	@MessageMapping("/acc-rej")
	@Transactional
	public void acceptOrRejectRequest(@Payload AddRequest addRequest) {
		System.out.println(addRequest);
		boolean status = chatService.acceptOrRejectRequest(addRequest);
		if(status) {
			// if accepted then send the updated friend list to the back-end
			System.out.println("sending messages ??? ");
			List<MyFriends> friends1 = userService.getAllFriends(addRequest.getS_userId());
//			List<MyFriends> friends = userRepository.findById(addRequest.getS_userId()).get().getFriends();
			messagingTemplate.convertAndSendToUser(addRequest.getS_userId(),"/acc/rej",friends1);	
			
			
			List<MyFriends> friends2 = userService.getAllFriends(addRequest.getR_userId());
//			List<MyFriends> friends2 = userRepository.findById(addRequest.getR_userId()).get().getFriends();
			messagingTemplate.convertAndSendToUser(addRequest.getR_userId(),"/acc/rej",friends2);
		}	
	}
	
	@MessageMapping("/send")
	@Transactional
	public void sendMesssageToUser(@Payload ChatMessage message) {
		ChatMessage saveChat = chatService.saveChat(message);
		messagingTemplate.convertAndSendToUser(message.getRecipientId(),"/send",saveChat);
		
	}
	@MessageMapping("/send/image")
	@Transactional
	public void sendFileMesssageToUser(@Payload FileDTO message) {
		ChatMessage saveChat = chatService.saveChat(message);
		messagingTemplate.convertAndSendToUser(message.getRecipientId(),"/send",saveChat);
		
	}
	
	

	@MessageMapping("/unfriend")
	@Transactional
	public void unfriendUser(@Payload UnfriendRequest request) {
		System.out.println(request.getLoggedInUSer()); // sender
		System.out.println(request.getSelectedUser()); // receiver
		chatService.removeFriends(request.getLoggedInUSer(), request.getSelectedUser());
		messagingTemplate.convertAndSendToUser(request.getSelectedUser(),"/unfriend",request.getLoggedInUSer());
	}
	
	
	public void sendSurpriseMessages(String id) {
		messagingTemplate.convertAndSendToUser(id,"/send","SURPRISE");
	}
	/*
	 * // Handles messages sent to "/app/sendMessage"
	 * 
	 * @MessageMapping("/sendMessage")
	 * 
	 * @SendTo("/user/messages") public ChatMessage sendMessage(ChatMessage message,
	 * SimpMessageHeaderAccessor headerAccessor) { return null; // Broadcast message
	 * to all clients subscribed to "/topic/messages" }
	 * 
	 * // Handles messages sent to "/app/sendMessage"
	 * 
	 * @MessageMapping("/addUser")
	 * 
	 * @SendTo("/user/messages") public ChatMessage addUser(ChatMessage message,
	 * SimpMessageHeaderAccessor headerAccessor) { return null; // Broadcast message
	 * to all clients subscribed to "/topic/messages" }
	 */
}

/*
 * class ChatMessage { private String content; private String sender;
 * 
 * // getters and setters public String getContent() { return content; } public
 * void setContent(String content) { this.content = content; } public String
 * getSender() { return sender; } public void setSender(String sender) {
 * this.sender = sender; } }
 */