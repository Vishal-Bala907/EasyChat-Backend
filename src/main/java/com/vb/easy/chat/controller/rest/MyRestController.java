package com.vb.easy.chat.controller.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.MyFriends;
import com.vb.easy.chat.modals.UserModal;
import com.vb.easy.chat.services.imple.UserServiceImplementation;

@RestController
@RequestMapping("/rest/chat/")
public class MyRestController {

	@Autowired
	UserServiceImplementation implementation;

	@GetMapping("/register-user")
	public ResponseEntity<UserModal> testAPI(@AuthenticationPrincipal Jwt jwt) {
		UserModal saveUser = implementation.saveUser(jwt);
		return new ResponseEntity<UserModal>(saveUser, HttpStatus.OK);
	}

	@GetMapping("/profile")
	public String getUserInfo(@AuthenticationPrincipal Jwt jwt) {
		// Extract realm roles
		Map<String, Object> realmAccess = jwt.getClaim("realm_access");
		@SuppressWarnings("unchecked")
		List<String> realmRoles = (List<String>) realmAccess.get("roles");
		return "Realm Roles: " + realmRoles + ", Client Roles: "; // + clientRoles;
	}

//	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
	@GetMapping("/search/users/{pattern}")
	public ResponseEntity<List<MyFriends>> getUsersByPattern(@AuthenticationPrincipal Jwt jwt,
			@PathVariable("pattern") String pattern) {
		List<MyFriends> usersByPattern = implementation.getUsersByPattern(jwt, pattern);
		System.out.println("OP {} " + usersByPattern);
		return new ResponseEntity<List<MyFriends>>(usersByPattern, HttpStatus.OK);
	}

	@GetMapping("/get/requests")
	public ResponseEntity<List<MyFriends>> getRequests(@AuthenticationPrincipal Jwt jwt) {
		List<MyFriends> requests = implementation.getAllRequests(jwt);
		return new ResponseEntity<List<MyFriends>>(requests, HttpStatus.OK);
	}

	@GetMapping("/get/friends")
	public ResponseEntity<List<MyFriends>> getFriends(@AuthenticationPrincipal Jwt jwt) {
		List<MyFriends> friends = implementation.getAllFriends(jwt);
		return new ResponseEntity<List<MyFriends>>(friends, HttpStatus.OK);
	}

	@GetMapping("/get/chats/{senderId}/{receiverId}")
	public ResponseEntity<List<ChatMessage>> getAllChats(@PathVariable("senderId") String senderId,
			@PathVariable("receiverId") String receiverId) {
		List<ChatMessage> allChats = implementation.getAllChats(senderId, receiverId);
		return new ResponseEntity<List<ChatMessage>>(allChats, HttpStatus.OK);
	}
//	loggedInUSer, selectedUser
	@GetMapping("/delete/chats/{loggedInUSer}/{selectedUser}")
	public ResponseEntity<Boolean> deleteAllChats(@PathVariable("loggedInUSer") String loggedInUSer,
			@PathVariable("selectedUser") String selectedUser) {
		implementation.deleteChats(loggedInUSer, selectedUser);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}

	@PostMapping("/upd-pro-image")
	public ResponseEntity<UserModal> uploadOrUpdateImage(@AuthenticationPrincipal Jwt jwt,
			@RequestParam("file") MultipartFile file) {

		UserModal updateOrSaveProfilePic = implementation.updateOrSaveProfilePic(jwt, file);
		return new ResponseEntity<UserModal>(updateOrSaveProfilePic, HttpStatus.OK);
	}

}
