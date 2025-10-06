package com.Shiva.SCM.controller;

import java.util.Random;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Shiva.SCM.Service.EmailService;
import com.Shiva.SCM.repository.UserRepository;
import com.Shiva.SCM.entity.User;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ForgotController {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    // Email ID form handler
    @RequestMapping("/forgot")
    public String openEmailForm() {
        return "forgot_email_form";
    }

    @RequestMapping("/send-otp")
    public String sendOTP(@RequestParam("email") String email, HttpSession session) {
        int otp = new Random().nextInt(999999);
        System.out.println("EMAIL: " + email + ", OTP: " + otp);

        String subject = "OTP From SCM";
        String message = """
                <div style='border:1px solid #e2e2e2; padding:20px'>
                    <h1>OTP is <b>%d</b></h1>
                </div>
                """.formatted(otp);  // ✅ Text Block with Java 17

        boolean flag = emailService.sendEmail(subject, message, email);

        if (flag) {
            session.setAttribute("myotp", otp);
            session.setAttribute("email", email);
            return "verify_otp";
        } else {
            session.setAttribute("message", "Check your email id!!");
            return "forgot_email_form";
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
        int myOtp = (int) session.getAttribute("myotp");
        String email = (String) session.getAttribute("email");

        if (myOtp == otp) {
            User user = userRepository.getUserByUserName(email);
            if (user == null) {
                session.setAttribute("message", "User does not exist with this email!!");
                return "forgot_email_form";
            }
            return "password_change_form";
        } else {
            session.setAttribute("message", "You have entered wrong OTP!!");
            return "verify_otp";
        }
    }

    // Change password
    @PostMapping("/change-password")
    public String changePassword(@RequestParam("newpassword") String newPassword, HttpSession session) {
        String email = (String) session.getAttribute("email");
        User user = userRepository.getUserByUserName(email);

        if (user != null) {
            user.setPassword(bcrypt.encode(newPassword));
            userRepository.save(user);
        }

        return "redirect:/signin?change=password changed successfully";
    }
}
