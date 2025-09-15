package com.Shiva.SCM.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import jakarta.servlet.http.HttpSession;   // ✅ jakarta

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.Shiva.SCM.dao.ContactRepository;
import com.Shiva.SCM.dao.UserRepository;
import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;
import com.Shiva.SCM.helper.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    private final ContactRepository contactRepo;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByEmail(username); // ✅ updated method
        model.addAttribute("user", user);
    }

    @GetMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
                                 @RequestParam("profileImage") MultipartFile file,
                                 Principal principal, HttpSession session) {
        try {
            User user = userRepository.findByEmail(principal.getName());

            if (file.isEmpty()) {
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File saveDir = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveDir.getAbsolutePath(), file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                log.info("Uploaded image: {}", file.getOriginalFilename());
            }

            contact.setUser(user);
            user.getContacts().add(contact);
            userRepository.save(user);

            session.setAttribute("message", new Message("Your contact is added! Add more..", "success"));

        } catch (Exception e) {
            log.error("Error while adding contact", e);
            session.setAttribute("message", new Message("Something went wrong! Try again", "danger"));
        }
        return "normal/add_contact_form";
    }

    @GetMapping("/show_contacts/{page}")
    public String showContacts(@PathVariable Integer page, Model model, Principal principal) {
        model.addAttribute("title", "Show Contacts");

        User user = userRepository.findByEmail(principal.getName());
        Pageable pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = contactRepo.findContactsByUser(user.getId(), pageable);

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", contacts.getTotalPages());

        return "normal/show_contacts";
    }

    @GetMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable Integer cId, Model model, Principal principal) {
        Contact contact = contactRepo.findById(cId).orElseThrow(() -> new RuntimeException("Contact not found"));
        User user = userRepository.findByEmail(principal.getName());

        if (user.getId().equals(contact.getUser().getId())) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }

        return "normal/contact_detail";
    }

    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId, HttpSession session, Principal principal) {
        Contact contact = contactRepo.findById(cId).orElseThrow(() -> new RuntimeException("Contact not found"));
        User user = userRepository.findByEmail(principal.getName());

        user.getContacts().remove(contact);
        userRepository.save(user);

        session.setAttribute("message", new Message("Contact deleted successfully", "success"));
        return "redirect:/user/show_contacts/0";
    }

    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable Integer cid, Model model) {
        Contact contact = contactRepo.findById(cid).orElseThrow(() -> new RuntimeException("Contact not found"));
        model.addAttribute("title", "Update Contact");
        model.addAttribute("contact", contact);
        return "normal/update_form";
    }

    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                Principal principal, HttpSession session) {
        try {
            Contact oldContact = contactRepo.findById(contact.getId())
                    .orElseThrow(() -> new RuntimeException("Contact not found"));

            if (!file.isEmpty()) {
                File deleteDir = new ClassPathResource("static/img").getFile();
                File oldFile = new File(deleteDir, oldContact.getImage());
                oldFile.delete();

                File saveDir = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveDir.getAbsolutePath(), file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());
            } else {
                contact.setImage(oldContact.getImage());
            }

            User user = userRepository.findByEmail(principal.getName());
            contact.setUser(user);
            contactRepo.save(contact);

            session.setAttribute("message", new Message("Your contact is updated", "success"));

        } catch (Exception e) {
            log.error("Error while updating contact", e);
        }

        return "redirect:/user/" + contact.getId() + "/contact"; // ✅ fixed here
    }

    @GetMapping("/profile")
    public String yourProfile(Model model) {
        model.addAttribute("title", "Profile Page");
        return "normal/profile";
    }

    @GetMapping("/settings")
    public String openSetting() {
        return "normal/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Principal principal, HttpSession session) {

        User currentUser = userRepository.findByEmail(principal.getName());

        if (!bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
            session.setAttribute("message", new Message("Please enter correct old password!!", "danger"));
            return "redirect:/user/settings";
        }

        currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        session.setAttribute("message", new Message("Your password has successfully changed", "success"));
        return "redirect:/user/index";
    }
}
