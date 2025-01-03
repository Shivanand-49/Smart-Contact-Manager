package com.Shiva.SCM.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	//pagination
	@Query("from Contact as c where c.user.id=:userId")
	// current page=0[page]
	// per page=5[5]
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pageable);
	
	// search
	public List<Contact>findByNameContainingAndUser(String name,User user);

}
