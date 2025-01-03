package com.Shiva.SCM.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Shiva.SCM.dao.ContactRepository;
import com.Shiva.SCM.dao.UserRepository;
import com.Shiva.SCM.entity.Contact;
import com.Shiva.SCM.entity.User;
import com.Shiva.SCM.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepo;

	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();
		System.out.println("Username:" + userName);

		// get the user using username(Email)

		User user = userRepository.getUserByUserName(userName);

		System.out.println("USER:" + user);
		model.addAttribute("user", user);

	}

	// dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "user Dashboard");
		return "normal/user_dashboard";
	}

	// open add form handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	// processing add contact form

	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Principal principal, HttpSession session) {

		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// processing and uploading file
			if (file.isEmpty()) {
				// if the file is empty then try our message
				System.out.println("File is empty ");
				contact.setImage("contact.png");

			}

			else {
				// file the to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());

				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				System.out.println("Image is uploading");
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			this.userRepository.save(user);

			System.out.println("DaTA " + contact);

			System.out.println("added to the database ");
			// success message
			session.setAttribute("message", new Message("Your contact is added!! Add more..", "success"));

		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();

			// error message
			session.setAttribute("message", new Message("Something went wrong!! try again ", "danger"));
		}
		return "normal/add_contact_form";
	}

	// show contact handler
	// per page=5[5]
	// current page=0[page]

	@GetMapping("/show_contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
		m.addAttribute("title", "show_contacts");
		String userName = principal.getName();

		User user = this.userRepository.getUserByUserName(userName);

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPage", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	// showing particular contact details

	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);

		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();

		String username = principal.getName();
		User user = this.userRepository.getUserByUserName(username);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_detail";
	}

	// delete contact handler

	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {
		Contact contact = this.contactRepo.findById(cId).get();

		// check.. assignment

		User user = this.userRepository.getUserByUserName(principal.getName());

		user.getContacts().remove(contact);

		this.userRepository.save(user);

		System.out.println("DELETED");
		session.setAttribute("message", new Message("contact delete successfully..", "success"));

		return "redirect:/user/show_contacts/0";
	}

	// open update form handler

	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		model.addAttribute("title", "Update contact");

		Contact contact = this.contactRepo.findById(cid).get();
		model.addAttribute("contact", contact);

		return "normal/update_form";
	}

	// update contact handler

	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model m, HttpSession session, Principal principal) {

		try {
			// old contact details
			Contact oldContactDetail = this.contactRepo.findById(contact.getcId()).get();
			// image
			if (!file.isEmpty()) {
				// file work
				// rewrite

				// delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldContactDetail.getImage());

				file1.delete();

				// update new photo

				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetail.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());

			contact.setUser(user);
			this.contactRepo.save(contact);

			session.setAttribute("message", new Message("your contact is update..", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("CONTACT NAME " + contact.getName());
		System.out.println("CONTACT ID " + contact.getcId());

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("title", "Profile page");
		return "normal/profile";
	}

	// open setting handler

	@GetMapping("/settings")
	public String openSetting() {

		return "normal/settings";
	}

	// change password.. handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		System.out.println("Old password " + oldPassword);
		System.out.println("new Password " + newPassword);

		String userName = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(userName);

		if (this.bCryptPasswordEncoder.matches(currentUser.getPassword(), oldPassword)) {
			// change the password
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("your password has successfully changed..", "success"));
		}

		else {
			// error

			session.setAttribute("message", new Message("please Enter correct old password!!", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/index";
	}
}
