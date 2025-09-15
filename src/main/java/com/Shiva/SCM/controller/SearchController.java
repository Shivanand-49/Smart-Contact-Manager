package com.Shiva.SCM.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.Shiva.SCM.dao.ContactRepository;
import com.Shiva.SCM.dao.UserRepository;
import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final UserRepository userRepository;
    private final ContactRepository contactRepository;

    // Search handler
    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable String query, Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated!");
        }

        String username = principal.getName();
        User user = userRepository.getUserByUserName(username);

        if (user == null) {
            log.warn("User not found for username: {}", username);
            return ResponseEntity.badRequest().body("User not found!");
        }

        List<Contact> contacts = contactRepository.findByNameContainingIgnoreCaseAndUser(query, user);

        if (contacts.isEmpty()) {
            log.info("No contacts found for query '{}' and user '{}'", query, username);
            return ResponseEntity.ok("No contacts found matching: " + query);
        }

        log.info("Found {} contacts for user '{}'", contacts.size(), username);
        return ResponseEntity.ok(contacts);
    }
}
