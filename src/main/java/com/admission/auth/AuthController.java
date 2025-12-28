package com.admission.auth;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.admission.Utilities;
import com.admission.ValidationError;
import com.admission.user.Gender;
import com.admission.user.User;
import com.admission.user.UserRepository;
import com.admission.user.UserType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;

	private final List<String> userTypes = List.of(UserType.STUDENT.toString(), UserType.ADMIN.toString());

	
	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("registerForm", new RegistrationForm());
		model.addAttribute("userTypes", userTypes);
		return "register.html";
	}

	@PostMapping("/register")
	public String registerUser(RegistrationForm form, Model model) {

		System.out.println("Email:" + form.getEmail());
		System.out.println("Password:" + form.getPassword());

		// DONE: Make sure first name is not empty
		if (form.getFirstName().isBlank()) {
			// send "First name cannot be empty"
			model.addAttribute("error", new ValidationError("First name cannot be empty"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// TODO: Make sure email is valid

		// DONE: Make sure password contains special character, number
		if (!Utilities.isValidPassword(form.getPassword())) {
			model.addAttribute("error", new ValidationError(
					"Password should be at least 8 characters long and should contain at least one number and at least one uppercase letter"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// TODO: Make sure password & confirm password are equal

		// DONE: Make sure email is unique
		// cannot be done in client side
		if (userRepository.existsByEmail(form.getEmail())) {
			model.addAttribute("error", new ValidationError("Email already exists"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: if any error exists; show it in the form

		// DONE: Create User entity object
		User user = new User();
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		user.setEmail(form.getEmail());

		// DONE: store hash of the password
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setType(UserType.valueOf(form.getUserType()));
		user.setUsername(form.getEmail());
		user.setGender(Gender.valueOf(form.getGender()));

		// DONE: Store the entity in db
		userRepository.save(user);

		// TODO: Send successful message and redirect to login page.

		return "redirect:/login";
	}

	@GetMapping("/login")
	public String getLoginPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("form", new LoginForm());
		return "login.html";
	}

	@PostMapping("/login")
	public String loginUser(LoginForm form, Model model, HttpServletResponse response) throws IOException {

		// DONE: find user in db by form's email
		/*
		 * SELECT * FROM _user WHERE _user.email = 'abc@example.com' LIMIT 1
		 * 
		 */
		User user = userRepository.findByEmail(form.getEmail());

		// DONE: match the form's password with db password using encoder
		// DONE: if match not found, send "Invalid credentials" error message
		if (user == null || !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
			model.addAttribute("error", new ValidationError("Invalid credentials!"));
			model.addAttribute("form", form);
			return "login.html";
		}
		
		// DONE: if match found, set a new cookie (session) for the user
		String sessionID = Utilities.getRandomString(20);
		Cookie cookie = new Cookie("SESSIONID", sessionID);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		// DONE: store created cookie in db
		user.setSession(sessionID);
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
		
		
		
		// DONE: redirect to dashboard
		
		System.out.println("Logged in as:" + user.getType());
	
		switch (user.getType()) {
		case ADMIN:
			return "redirect:/admin/dashboard";
		default:
			return "redirect:/dashboard";
		}
	
	}
	
	
	@GetMapping("/about")
	public String getAbout() {
	    return "about";
	}
	
	
	
	
	


}