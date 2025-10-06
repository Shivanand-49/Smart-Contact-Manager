package com.Shiva.SCM.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // Pagination ke liye (user ke sare contacts fetch karne ke liye)
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    Page<Contact> findContactsByUser(int userId, Pageable pageable);

    // Case-insensitive search by name for a specific user
    List<Contact> findByNameContainingIgnoreCaseAndUser(String name, User user);

    // Agar tumhare purane code me ye method use ho raha hai:
    default List<Contact> findByNameContainingAndUser(String name, User user) {
        return findByNameContainingIgnoreCaseAndUser(name, user);
    }
}
