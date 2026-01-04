package com.admission.auth;

import java.io.IOException;
import java.time.Instant;

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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private Authentication authentication;

	
	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("registerForm", new RegistrationForm());
		return "register.html";
	}

	@PostMapping("/register")
	public String registerUser(RegistrationForm form, Model model) {

		// DONE: Make sure first name is not empty
		if (form.getFirstName() == null || form.getFirstName().isBlank()) {
			model.addAttribute("error", new ValidationError("First name cannot be empty"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Make sure email is valid
		if (form.getEmail() == null || form.getEmail().isBlank() || !Utilities.isValidEmail(form.getEmail())) {
			model.addAttribute("error", new ValidationError("Please enter a valid email address"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Make sure password contains special character, number
		if (form.getPassword() == null || !Utilities.isValidPassword(form.getPassword())) {
			model.addAttribute("error", new ValidationError(
					"Password should be at least 8 characters long and should contain at least one number and at least one uppercase letter"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Make sure password & confirm password are equal
		if (form.getConfirmPassword() == null || !form.getPassword().equals(form.getConfirmPassword())) {
			model.addAttribute("error", new ValidationError("Password and confirm password do not match"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

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
		
		// Set default user type to STUDENT (admin is hardcoded in the system)
		user.setType(UserType.STUDENT);
		
		user.setUsername(form.getEmail());
		
		// Validate and set gender with exception handling
		if (form.getGender() == null || form.getGender().isBlank()) {
			model.addAttribute("error", new ValidationError("Please select a gender"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}
		try {
			user.setGender(Gender.valueOf(form.getGender()));
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", new ValidationError("Invalid gender selected"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Store the entity in db
		try {
			userRepository.save(user);
		} catch (Exception e) {
			model.addAttribute("error", new ValidationError("An error occurred while registering. Please try again."));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Send successful message and redirect to login page.
		return "redirect:/login?registered=true";
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
		cookie.setHttpOnly(true); // Prevent XSS attacks
		cookie.setSecure(true); // Only send over HTTPS
		cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days expiration
		response.addCookie(cookie);
		
		// DONE: store created cookie in db
		user.setSession(sessionID);
		user.setLastLoginAt(Instant.now());
		userRepository.save(user);
		
		
		
		// DONE: redirect to dashboard
		switch (user.getType()) {
		case ADMIN:
			return "redirect:/admin/dashboard";
		default:
			return "redirect:/dashboard";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		User user = authentication.authenticate(request);
		if (user != null) {
			// Clear session from database
			user.setSession(null);
			userRepository.save(user);
		}
		
		// Clear cookie by setting it to expire immediately
		Cookie cookie = new Cookie("SESSIONID", "");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setMaxAge(0); // Expire immediately
		response.addCookie(cookie);
		
		return "redirect:/login?loggedout=true";
	}
	
	@GetMapping("/about")
	public String getAbout() {
	    return "about";
	}

}