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
	
	@GetMapping("/admin/dashboard")
	public String getDashboard(
			HttpServletRequest request, 
			Model model,
			@RequestParam(required = false) Integer applicationId) {
		
		User user = authentication.authenticate(request);
		
		if(user == null) {
			return "redirect:/login";
		}
		
		// Verify user has ADMIN role
		if(user.getType() != UserType.ADMIN) {
			return "redirect:/dashboard";
		}
		
		// Get all applications ordered by date
		List<Application> allApplications = applicationRepository.findAllOrderByAppliedDateDesc();
		
		// Calculate statistics
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
		if (selectedApplication == null && !allApplications.isEmpty()) {
			selectedApplication = allApplications.get(0);
		}
		
		model.addAttribute("user", user);
		model.addAttribute("applications", allApplications);
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
		
		if(user == null || user.getType() != UserType.ADMIN) {
			return "redirect:/login";
		}
		
		Application application = applicationRepository.findById(id).orElse(null);
		if (application != null) {
			application.setStatus(status);
			applicationRepository.save(application);
		}
		
		return "redirect:/admin/dashboard?applicationId=" + id;
	}
	
}