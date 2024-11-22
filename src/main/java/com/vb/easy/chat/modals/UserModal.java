package com.vb.easy.chat.modals;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserModal {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String userId;
	private String username;
	private String email;
	private String image;
	private boolean status; // true == online

	
	/*
	 * @OneToMany private List<ChatRoom> chatRooms;
	 */
	
	@OneToMany
	@Cascade(CascadeType.ALL)
	private List<MyFriends> friends;

}
