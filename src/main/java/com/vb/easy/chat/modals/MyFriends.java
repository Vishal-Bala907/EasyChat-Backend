package com.vb.easy.chat.modals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="friends")
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@ToString
public class MyFriends {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	private String og_id;
	private String name;
	private String image;

}
