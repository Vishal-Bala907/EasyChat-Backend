package com.vb.easy.chat.services.interfaces;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.MyFriends;
import com.vb.easy.chat.modals.UserModal;

public interface UserServices {
	public UserModal saveUser(Jwt jwt);

	List<MyFriends> getUsersByPattern(Jwt jwt, String pattern);

	UserModal updateOrSaveProfilePic(Jwt jwt, MultipartFile file);

	List<MyFriends> getAllRequests(Jwt jwt);

	List<MyFriends> getAllFriends(Jwt jwt);

	List<MyFriends> getAllFriends(String userId);

	List<ChatMessage> getAllChats(String sender, String receiver);

//	selectedUser
	void deleteChats(String loggedInUSer, String selectedUser);
}
