package com.vb.easy.chat.services.imple;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.MyFriends;
import com.vb.easy.chat.modals.UserModal;
import com.vb.easy.chat.repos.ChatMessageRepo;
import com.vb.easy.chat.repos.ReqRepository;
import com.vb.easy.chat.repos.UserRepository;
import com.vb.easy.chat.services.interfaces.UserServices;

@Service
public class UserServiceImplementation implements UserServices {

	@Autowired
	private UserRepository repository;
	@Autowired
	private ReqRepository reqRepository;
	@Autowired
	private ChatServiceImple chatServiceImple;
	@Autowired
	private ChatMessageRepo messageRepo;

	@Value("${profile.dp}")
	private String dpLocation;

	@Value("${frontend.dp}")
	private String frontendDP;

	@Override
	public UserModal saveUser(Jwt jwt) {
		String username = jwt.getClaim("preferred_username");
		String email = jwt.getClaim("email");
		UserModal byEmail = repository.findByEmail(email);
		System.out.println(byEmail);
		if (byEmail == null) {
			UserModal newUser = UserModal.builder().email(email).username(username).image("").status(true).build();
			try {
				UserModal savedUser = repository.save(newUser);
				System.out.println("Saved new User: " + savedUser);
				return savedUser;
			} catch (Exception e) {
				System.err.println("Error saving user: " + e.getMessage());
				e.printStackTrace();
				// Handle exception as necessary
			}
		}

		return byEmail;
	}

	@Override
	public List<MyFriends> getUsersByPattern(Jwt jwt, String pattern) {

//		System.out.println(0);
		List<UserModal> usernameByPattern = repository.findUsernameByPattern(pattern + "%"); // this may be empty
		if (usernameByPattern == null) {
			List<MyFriends> of = List.of(new MyFriends());
			return of;
		}
//		System.out.println(1);
		// send only those who are not already being added
		// also remove users vital infos like friendList etc
		// Getting the logged in users data to sort out all already added users
		String email = jwt.getClaim("email");
		UserModal byEmail = repository.findByEmail(email);
		List<MyFriends> loggedInUserFriends = byEmail.getFriends();
//		System.out.println(2);
		// getting all og_id of the existing friends
		List<String> ogIds = loggedInUserFriends.stream().map(user -> {
			return user.getOg_id();
		}).toList();
		// remove all the friends from the search result to send only those users
		// who are not already a friend
//		System.out.println(3);
		List<UserModal> allUniqueFriens = usernameByPattern.stream().filter(user -> {
			return !ogIds.contains(user.getUserId());
		}).toList();

		// removing self name

		List<UserModal> UNIQUE = allUniqueFriens.stream().filter(user -> {
			return user.getEmail() != byEmail.getEmail();
		}).toList();

		System.out.println(UNIQUE);
		// convert and return this to List
		return converUserModalToMyFriends(UNIQUE);
	}

	public List<MyFriends> converUserModalToMyFriends(List<UserModal> list) {
		return list.stream().map((user) -> {
			return MyFriends.builder().og_id(user.getUserId()).name(user.getUsername()).image(user.getImage()).build();
		}).toList();
	}

	@Override
	public List<MyFriends> getAllRequests(Jwt jwt) {
		String email = jwt.getClaim("email");
		String userId = repository.findByEmail(email).getUserId();
		List<MyFriends> list = reqRepository.getAllRequests(userId).stream().map(user -> {
			UserModal userModal = repository.findById(user.getS_userId()).get();
			return MyFriends.builder().og_id(userModal.getUserId()).name(userModal.getUsername())
					.image(userModal.getImage()).build();
		}).toList();
		return list;
	}

	@Override
	public List<MyFriends> getAllFriends(Jwt jwt) {
		String email = jwt.getClaim("email");
		List<MyFriends> friends = repository.findByEmail(email).getFriends();

		// set the images of all friends
		List<MyFriends> list = friends.stream().map(user -> {
			String image = repository.findById(user.getOg_id()).get().getImage();
			user.setImage(image);
			return user;
		}).toList();
		return list;
	}

	@Override
	public List<ChatMessage> getAllChats(String sender, String receiver) {
		// get the chat room
		// if charRoom is not available then create one
		@SuppressWarnings("unused")
		String roomId = chatServiceImple.getChatRoomOrCreateOne(sender, receiver);
		System.out.println(sender + "\t\t" + receiver);
		List<ChatMessage> bySenderIdAndRecipientId = messageRepo.findBySenderIdAndRecipientId(sender, receiver);
		List<ChatMessage> bySenderIdAndRecipientId2 = messageRepo.findBySenderIdAndRecipientId(receiver, sender);
		bySenderIdAndRecipientId.addAll(bySenderIdAndRecipientId2);

		System.out.println("bySenderIdAndRecipientId " + bySenderIdAndRecipientId);

		System.out.println("LIST " + bySenderIdAndRecipientId);
		if (bySenderIdAndRecipientId.size() > 1) {
			Collections.sort(bySenderIdAndRecipientId, new Comparator<ChatMessage>() {
				@Override
				public int compare(ChatMessage o1, ChatMessage o2) {
					return Long.compare(o1.getTimeStamp(), o2.getTimeStamp());
				}
			});
		}

		// extracting all the deleted chats
		List<ChatMessage> list = bySenderIdAndRecipientId.stream().filter(message -> {
			return message.getViewer1().equals(sender) || message.getViewer2().equals(sender);
		}).toList();

		System.out.println("CHATS HERE {} " + list);
		return list;
	}

	@Override
	public UserModal updateOrSaveProfilePic(Jwt jwt, MultipartFile file) {
		String fileName = file.getOriginalFilename();

		// rename the file
		try {
			InputStream inputStream = file.getInputStream();
			String email = jwt.getClaim("email");

			UserModal byEmail = repository.findByEmail(email);
			String fileSavePath = dpLocation + File.separator + byEmail.getUserId() + fileName;
//		    File fileToSave = new File(fileSavePath);
			File fileDirectory = new File(dpLocation + File.separator);
			// making directory
			if (!fileDirectory.exists()) {
				boolean mkdirs = fileDirectory.mkdirs();
				if (mkdirs) {
					System.out.println("Folders created");
				} else {
					System.err.println("Unable to create");
				}
			}

			// copying
			Files.copy(inputStream, Paths.get(fileSavePath), StandardCopyOption.REPLACE_EXISTING);
			byEmail.setImage(frontendDP + "/" + byEmail.getUserId() + fileName);
			repository.save(byEmail);
			return byEmail;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<MyFriends> getAllFriends(String userId) {
		// TODO Auto-generated method stub
//		String email = jwt.getClaim("email");
		List<MyFriends> friends = repository.findById(userId).get().getFriends();

		// set the images of all friends
		List<MyFriends> list = friends.stream().map(user -> {
			String image = repository.findById(user.getOg_id()).get().getImage();
			user.setImage(image);
			return user;
		}).toList();
		return list;
	}

	@Override
	public void deleteChats(String loggedInUSer, String selectedUser) {
		List<ChatMessage> bySenderIdAndRecipientId = messageRepo.findBySenderIdAndRecipientId(loggedInUSer,
				selectedUser);
		List<ChatMessage> bySenderIdAndRecipientId2 = messageRepo.findBySenderIdAndRecipientId(selectedUser,
				loggedInUSer);
		bySenderIdAndRecipientId.addAll(bySenderIdAndRecipientId2);

		// delete all those chats that were mark to be deleted by both the users
		bySenderIdAndRecipientId.forEach(message -> {
			if (message.getViewer1().equals(loggedInUSer) && message.getViewer2().equals("0")) {
				messageRepo.delete(message);
			} else if (message.getViewer2().equals(loggedInUSer) && message.getViewer1().equals("0")) {
				messageRepo.delete(message);
			} else if (message.getViewer1().equals(loggedInUSer) && !message.getViewer2().equals("0")) {
				message.setViewer1("0");
				messageRepo.save(message);
			} else if (message.getViewer2().equals(loggedInUSer) && !message.getViewer1().equals("0")) {
				message.setViewer2("0");
				messageRepo.save(message);
			}
		});

	}

}
