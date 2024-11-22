package com.vb.easy.chat.modals;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class AddRequest {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	public String req_id;
	public String s_userName;
	public String s_userId;
	public String r_userName;
	public String r_userId;
	public boolean status; 
}
