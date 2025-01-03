package com.Shiva.SCM.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.Shiva.SCM.dao.ContactRepository;
import com.Shiva.SCM.dao.UserRepository;
import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;

@RestController
public class SearchController {
	
	  @Autowired
	private UserRepository userRepository;
	
	  @Autowired
	private ContactRepository contactrepository;
	
	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?>search(@PathVariable("query") String query, Principal principal)
	{
		 User user=this.userRepository.getUserByUserName(principal.getName());
	    List<Contact>contacts=this.contactrepository.findByNameContainingAndUser(query, user);
	    
	    return ResponseEntity.ok(contacts);
	}
  
}
