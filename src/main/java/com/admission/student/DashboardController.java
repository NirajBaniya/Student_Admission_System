package com.admission.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.admission.auth.Authentication;
import com.admission.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {

	@Autowired
	private Authentication authentication;

	@GetMapping("/dashboard")
	public String getDashboard(HttpServletRequest request, Model model) {
		// DONE: get user from cookie
		// user verification
		// user identification
		User user = authentication.authenticate(request);

		if (user == null) {
			return "redirect:/login";
		}
		
		model.addAttribute("user", user);
		
		return "dashboard.html";
	}

}