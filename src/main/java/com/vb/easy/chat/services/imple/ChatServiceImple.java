package com.vb.easy.chat.services.imple;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vb.easy.chat.controller.ChatController;
import com.vb.easy.chat.modals.AddRequest;
import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.ChatRoom;
import com.vb.easy.chat.modals.FileDTO;
import com.vb.easy.chat.modals.MessageType;
import com.vb.easy.chat.modals.MyFriends;
import com.vb.easy.chat.modals.UserModal;
import com.vb.easy.chat.repos.ChatMessageRepo;
import com.vb.easy.chat.repos.ChatRoomRepo;
import com.vb.easy.chat.repos.ReqRepository;
import com.vb.easy.chat.repos.UserRepository;
import com.vb.easy.chat.services.interfaces.ChatServices;

@Service
public class ChatServiceImple implements ChatServices {
	@Autowired
	private ReqRepository repository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChatRoomRepo roomRepo;
	@Autowired
	private ChatMessageRepo chatMessageRepo;
//	@Autowired
//	private ChatController chatController;

	@Override
	public boolean acceptOrRejectRequest(AddRequest addRequest) {
		System.out.println(addRequest);
		AddRequest addRequest2 = repository.isRequestAlreadyExists(addRequest.getS_userId(), addRequest.getR_userId());
		if (addRequest.isStatus()) {

			// if request is accepted // 1st step // get both users objects
			UserModal firstUser = userRepository.findById(addRequest.getS_userId()).get();

			MyFriends firstUserAsFriend = MyFriends.builder().og_id(firstUser.getUserId()).name(firstUser.getUsername())
					.image(firstUser.getImage()).build();

			System.out.println(firstUserAsFriend);

			UserModal secondUser = userRepository.findById(addRequest.getR_userId()).get();

			MyFriends secondUserAsFriend = MyFriends.builder().og_id(secondUser.getUserId())
					.name(secondUser.getUsername()).image(secondUser.getImage()).build(); //

			System.out.println(secondUserAsFriend);
			/* create 2 friend object */ // save these objects to each user
			firstUser.getFriends().add(secondUserAsFriend); //
			secondUser.getFriends().add(firstUserAsFriend); //
			userRepository.save(firstUser); // userRepository.save(secondUser);
			userRepository.save(secondUser); // userRepository.save(secondUser);

			repository.delete(addRequest2);

			return true;
		} else {
			// just delete the request
			repository.delete(addRequest2);
			return false;
		}
	}

	@Override
	public String getChatRoomOrCreateOne(String sender, String receiver) {
		String roomId = String.format("%s_%s", sender, receiver);
		System.out.println("Searching for roomId: " + roomId);

		List<ChatRoom> existingRoom = null;

		try {
			existingRoom = roomRepo.findByRoomId(roomId);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if (!existingRoom.isEmpty()) {
			return existingRoom.get(0).getRoomId();
		}

		System.out.println("Creating chat room: " + roomId);
		ChatRoom room1 = roomRepo.save(new ChatRoom(roomId, sender, receiver));
		ChatRoom room2 = roomRepo.save(new ChatRoom(roomId, receiver, sender));
		System.out.println("Chat Rooms Created: " + room1 + ", " + room2);

		return room1.getRoomId();
	}

	@Override
	public ChatMessage saveChat(ChatMessage chatMessage) {
			chatMessage.setDate(LocalDate.now().toString());
			chatMessage.setTime(LocalTime.now().toString());
			chatMessage.setTimeStamp(new Date().getTime());
		return chatMessageRepo.save(chatMessage);

	}

	@Override
	@Transactional
	public void removeFriends(String loggedinUser, String selectedUser) {
		// get the both users Object
		// get their friend list]
		// remove each other from that list
		// save their list again
		// now remove all their chats
		UserModal logged = userRepository.findById(loggedinUser).get();
		List<MyFriends> list = logged.getFriends().stream().filter(user -> {
			return !user.getOg_id().equals(selectedUser);
		}).collect(Collectors.toCollection(ArrayList::new));
		logged.setFriends(list);
		userRepository.save(logged);

		UserModal selected = userRepository.findById(selectedUser).get();
		List<MyFriends> list2 = selected.getFriends().stream().filter(user -> {
			return !user.getOg_id().equals(loggedinUser);
		}).collect(Collectors.toCollection(ArrayList::new));
		selected.setFriends(list2);
		userRepository.save(selected);

		// delete chats
		chatMessageRepo.deleteChats(loggedinUser, selectedUser);
		chatMessageRepo.deleteChats(selectedUser, loggedinUser);

	}

	@Override
	public ChatMessage saveChat(FileDTO fileDTO) {
		var file = fileDTO.getFile();
		System.out.println(file.getOriginalFilename());
		return null;
	}

}
