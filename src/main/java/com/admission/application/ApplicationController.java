package com.admission.application;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.admission.auth.Authentication;
import com.admission.college.College;
import com.admission.college.CollegeRepository;
import com.admission.storage.StorageService;
import com.admission.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ApplicationController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

	@Autowired
	private Authentication authentication;
	
	@Autowired
	private CollegeRepository collegeRepository;
	
	@Autowired
	private ApplicationRepository applicationRepository;
	
	@Autowired
	private StorageService storageService;

	@GetMapping("/applications")
	public String getMyApplications(HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		List<Application> applications = applicationRepository.findByUserOrderByAppliedDateDesc(user);
		model.addAttribute("user", user);
		model.addAttribute("applications", applications);
		
		return "applications.html";
	}
	
	@PostMapping("/apply")
	public String submitApplication(ApplicationForm form, HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		try {
			// Get the single college
			College college = collegeRepository.getSingleCollege();
			if (college == null) {
				model.addAttribute("error", "College information not available. Please contact administrator.");
				model.addAttribute("user", user);
				model.addAttribute("college", null);
				return "dashboard.html";
			}
			
			// Check if user already applied
			if (applicationRepository.existsByUserAndCollege(user, college)) {
				model.addAttribute("error", "You have already submitted an application");
				model.addAttribute("user", user);
				model.addAttribute("college", college);
				return "dashboard.html";
			}
			
			Application application = new Application();
			application.setUser(user);
			application.setCollege(college);
			application.setCourse(form.getCourse());
			application.setGpa(form.getGpa());
			application.setStatus(ApplicationStatus.PENDING);
			
			// Store documents if provided
			if (form.getDocuments() != null && !form.getDocuments().isEmpty()) {
				try {
					String documentsPath = storageService.storeDocument(form.getDocuments());
					application.setDocumentsPath(documentsPath);
				} catch (IllegalArgumentException e) {
					model.addAttribute("error", e.getMessage());
					model.addAttribute("user", user);
					model.addAttribute("college", college);
					return "dashboard.html";
				}
			}
			
			applicationRepository.save(application);
			logger.info("Application submitted by user {} for college {}", user.getId(), college.getId());
			
			return "redirect:/applications?success=true";
			
		} catch (Exception e) {
			logger.error("Error submitting application", e);
			model.addAttribute("error", "Failed to submit application. Please try again.");
			model.addAttribute("user", user);
			College college = collegeRepository.getSingleCollege();
			model.addAttribute("college", college);
			return "dashboard.html";
		}
	}
	
}

