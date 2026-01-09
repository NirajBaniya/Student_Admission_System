package com.admission.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.admission.auth.Authentication;
import com.admission.college.College;
import com.admission.college.CollegeRepository;
import com.admission.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {

	@Autowired
	private Authentication authentication;

	@Autowired
	private CollegeRepository collegeRepository;

	@GetMapping("/dashboard")
	public String getDashboard(HttpServletRequest request, Model model) {

		User user = authentication.authenticate(request);

		if (user == null) {
			return "redirect:/login";
		}

		// Get the single college
		College college = collegeRepository.getSingleCollege();

		model.addAttribute("user", user);
		model.addAttribute("college", college);

		return "dashboard.html";
	}

}