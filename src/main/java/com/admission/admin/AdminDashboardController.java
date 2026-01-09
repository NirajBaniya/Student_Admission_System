package com.admission.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admission.application.Application;
import com.admission.application.ApplicationRepository;
import com.admission.application.ApplicationStatus;
import com.admission.auth.Authentication;
import com.admission.user.User;
import com.admission.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminDashboardController {

	@Autowired
	private Authentication authentication;

	@Autowired
	private ApplicationRepository applicationRepository;

	@Autowired
	private com.admission.college.CollegeRepository collegeRepository;

	@GetMapping("/admin/dashboard")
	public String getDashboard(
			HttpServletRequest request,
			Model model,
			@RequestParam(required = false) Integer applicationId) {

		User user = authentication.authenticate(request);

		if (user == null) {
			return "redirect:/login";
		}

		// Verify user has ADMIN role
		if (user.getType() != UserType.ADMIN) {
			return "redirect:/dashboard";
		}

		// Get all applications ordered by date
		List<Application> applications = applicationRepository.findAllOrderByAppliedDateDesc();

		// Calculate statistics
		long pendingCount = applications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.PENDING)
				.count();
		long acceptedCount = applications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.ACCEPTED)
				.count();
		long rejectedCount = applications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.REJECTED)
				.count();

		// Get selected application or first one
		Application selectedApplication = null;
		if (applicationId != null) {
			selectedApplication = applicationRepository.findById(applicationId).orElse(null);
		}
		if (selectedApplication == null && !applications.isEmpty()) {
			selectedApplication = applications.get(0);
		}

		com.admission.college.College college = collegeRepository.getSingleCollege();
		model.addAttribute("college", college);

		model.addAttribute("user", user);
		model.addAttribute("applications", applications);
		model.addAttribute("selectedApplication", selectedApplication);
		model.addAttribute("pendingCount", pendingCount);
		model.addAttribute("acceptedCount", acceptedCount);
		model.addAttribute("rejectedCount", rejectedCount);

		return "admin/dashboard.html";
	}

	@PostMapping("/admin/application/{id}/status")
	public String updateApplicationStatus(
			@PathVariable int id,
			@RequestParam ApplicationStatus status,
			HttpServletRequest request) {

		User user = authentication.authenticate(request);

		if (user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}

		Application application = applicationRepository.findById(id).orElse(null);
		if (application != null) {
			application.setStatus(status);
			applicationRepository.save(application);
		}

		return "redirect:/admin/dashboard?applicationId=" + id;
	}

	@PostMapping("/admin/application/{id}/comment")
	public String updateApplicationComment(
			@PathVariable int id,
			@RequestParam(required = false) String comment,
			HttpServletRequest request) {

		User user = authentication.authenticate(request);

		if (user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}

		Application application = applicationRepository.findById(id).orElse(null);
		if (application != null) {
			application.setAdminComment(comment);
			applicationRepository.save(application);
		}

		return "redirect:/admin/dashboard?applicationId=" + id;
	}

	// --- Course Management ---

	@PostMapping("/admin/college/course/add")
	public String addCourse(@RequestParam String courseName, HttpServletRequest request) {
		User user = authentication.authenticate(request);
		if (user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}

		if (courseName != null && !courseName.trim().isEmpty()) {
			com.admission.college.College college = collegeRepository.getSingleCollege();
			if (college != null) {
				// Don't add duplicate
				if (college.getCourses() == null) {
					college.setCourses(new java.util.ArrayList<>());
				}
				if (!college.getCourses().contains(courseName.trim())) {
					college.getCourses().add(courseName.trim());
					collegeRepository.save(college);
				}
			}
		}
		return "redirect:/admin/dashboard";
	}

	@PostMapping("/admin/college/course/remove")
	public String removeCourse(@RequestParam String courseName, HttpServletRequest request) {
		User user = authentication.authenticate(request);
		if (user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}

		if (courseName != null) {
			com.admission.college.College college = collegeRepository.getSingleCollege();
			if (college != null && college.getCourses() != null) {
				college.getCourses().remove(courseName);
				collegeRepository.save(college);
			}
		}
		return "redirect:/admin/dashboard";
	}

}