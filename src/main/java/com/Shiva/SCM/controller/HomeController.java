package com.Shiva.SCM.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.Shiva.SCM.dao.UserRepository;
import com.Shiva.SCM.entity.User;
import com.Shiva.SCM.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepository userRepo;
	
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String home(Model m)
	{  
		m.addAttribute("title", "smart-ContactManager");
		return "home";
	}
	
	
	@RequestMapping(value="/about", method=RequestMethod.GET)
	public String about(Model m)
	{  
		m.addAttribute("title", "smart-ContactManager");
		return "about";
	}
	
	

	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signup(Model m)
	{  
		m.addAttribute("title", "smart-ContactManager");
		m.addAttribute("user",new User());
		return "signup";
	}
	
	//handler for registering
	
	@RequestMapping(value="/do_register", method=RequestMethod.POST)
	public String registerUser(@Valid  @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session)
	
	{  
		
		try {
			if(!agreement)
			{
				System.out.println("You have not agreed the term and condition ");
				throw new Exception("You have not agreed the term and condition ");
			}
			
			if(result1.hasErrors())
			{
				System.out.println("ERROR"+result1.toString());
				model.addAttribute("user" ,user);
				return "signup";
			}
			
			
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		System.out.println("Agreement:"+agreement);
		System.out.println("User:"+user);
		    
		  User result=  this.userRepo.save(user);
		    
		    model.addAttribute("user", new User());
		    
		    session.setAttribute("message",new Message("Successfully Registered !!","alert-success"));
			return "signup";
		    
		}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message",new Message("something went wrong !!"+e.getMessage(),"alert-danger"));
			return "signup";
		}
		
		
	}

	
	// handler for custom login
	
	@GetMapping("/signin")
	public String customlogin(Model model)
	{   
		
		model.addAttribute("title", "Login Page");
		return "login";
	}
}
