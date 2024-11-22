package com.vb.easy.chat.services.interfaces;

import com.vb.easy.chat.modals.AddRequest;
import com.vb.easy.chat.modals.ChatMessage;
import com.vb.easy.chat.modals.FileDTO;

public interface ChatServices {
	
	public boolean acceptOrRejectRequest(AddRequest addRequest);
	public String getChatRoomOrCreateOne(String sender , String reveiver);
	public ChatMessage saveChat(ChatMessage chatMessage);
	public ChatMessage saveChat(FileDTO fileDTO);
	public void removeFriends(String loggedinUser , String selectedUser);
	
}
