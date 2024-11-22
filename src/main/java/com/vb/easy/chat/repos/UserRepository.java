package com.vb.easy.chat.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vb.easy.chat.modals.UserModal;

@Repository
public interface UserRepository extends JpaRepository<UserModal, String> {
	UserModal findByEmail(String email);
	
	@Query("SELECT user FROM users user WHERE user.username LIKE :pattern")
	List<UserModal> findUsernameByPattern(String pattern);
}
