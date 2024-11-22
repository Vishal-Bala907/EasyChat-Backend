package com.vb.easy.chat.repos;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vb.easy.chat.modals.AddRequest;

@Repository
public interface ReqRepository extends JpaRepository<AddRequest, String> {

	@Query("SELECT req FROM AddRequest req WHERE s_userId =:sId AND r_userId =:rId")
	AddRequest isRequestAlreadyExists(String sId , String rId);
	
	@Query("SELECT req FROM AddRequest req WHERE r_userId =:id")
	List<AddRequest> getAllRequests(String id);
}
