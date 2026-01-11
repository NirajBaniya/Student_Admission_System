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
			@RequestParam(required = false) Integer applicationId,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String search) {

		User user = authentication.authenticate(request);

		if (user == null) {
			return "redirect:/login";
		}

		// Verify user has ADMIN role
		if (user.getType() != UserType.ADMIN) {
			return "redirect:/dashboard";
		}

		// Parse status string to Enum safely
		ApplicationStatus statusEnum = null;
		if (status != null && !status.trim().isEmpty()) {
			try {
				statusEnum = ApplicationStatus.valueOf(status);
			} catch (IllegalArgumentException e) {
				statusEnum = null;
			}
		}

		List<Application> applications;

		// Logic to filter/search
		if (search != null && !search.trim().isEmpty()) {
			applications = applicationRepository.searchApplications(search.trim());
		} else if (statusEnum != null) {
			applications = applicationRepository.findByStatusOrderByAppliedDateDesc(statusEnum);
		} else {
			// Get all applications ordered by date
			applications = applicationRepository.findAllOrderByAppliedDateDesc();
		}

		// Statistics (Calculate from all applications to keep cards consistent)
		List<Application> allApplications = applicationRepository.findAllOrderByAppliedDateDesc();
		long pendingCount = allApplications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.PENDING)
				.count();
		long acceptedCount = allApplications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.ACCEPTED)
				.count();
		long rejectedCount = allApplications.stream()
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

		com.admission.college.College college = null;
		try {
			college = collegeRepository.getSingleCollege();
		} catch (Exception e) {
			// Handle case where college might not exist
		}
		model.addAttribute("college", college);

		model.addAttribute("user", user);
		model.addAttribute("applications", applications);
		model.addAttribute("selectedApplication", selectedApplication);
		model.addAttribute("pendingCount", pendingCount);
		model.addAttribute("acceptedCount", acceptedCount);
		model.addAttribute("rejectedCount", rejectedCount);

		// Models for filter preservation
		model.addAttribute("currentStatus", statusEnum);
		model.addAttribute("currentSearch", search);

		return "admin/dashboard.html";
	}

	@PostMapping("/admin/application/{id}/status")
	public String updateApplicationStatus(
			@PathVariable int id,
			@RequestParam ApplicationStatus status,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String statusFilter,
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

		// Preserve filters in redirect
		StringBuilder redirectUrl = new StringBuilder("/admin/dashboard?applicationId=" + id);
		if (search != null && !search.trim().isEmpty()) {
			redirectUrl.append("&search=").append(search);
		}
		if (statusFilter != null && !statusFilter.trim().isEmpty()) {
			redirectUrl.append("&status=").append(statusFilter);
		}
		return "redirect:" + redirectUrl.toString();
	}

	@PostMapping("/admin/application/{id}/comment")
	public String updateApplicationComment(
			@PathVariable int id,
			@RequestParam(required = false) String comment,
			@RequestParam(required = false) String search,
			@RequestParam(required = false) String status,
			HttpServletRequest request) {

		User user = authentication.authenticate(request);

		if (user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}

		Application application = applicationRepository.findById(id).orElse(null);
		if (application != null) {
			application.setAdminComment(comment != null ? comment.trim() : null);
			applicationRepository.save(application);
		}

		// Preserve filters in redirect
		StringBuilder redirectUrl = new StringBuilder("/admin/dashboard?applicationId=" + id);
		if (search != null && !search.trim().isEmpty()) {
			redirectUrl.append("&search=").append(search);
		}
		if (status != null && !status.trim().isEmpty()) {
			redirectUrl.append("&status=").append(status);
		}
		return "redirect:" + redirectUrl.toString();
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