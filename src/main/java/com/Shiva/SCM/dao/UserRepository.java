package com.Shiva.SCM.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Shiva.SCM.entity.User;

public interface UserRepository  extends JpaRepository<User,Integer>{
	 
	@Query("select u from User u where u.email=:email")
	public User getUserByUserName(@Param("email") String email);

}
