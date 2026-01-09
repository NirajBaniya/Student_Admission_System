package com.admission.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.admission.application.ApplicationRepository;
import com.admission.auth.Authentication;
import com.admission.storage.StorageService;
import com.admission.user.User;
import com.admission.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private Authentication authentication;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StorageService storageService;

    @GetMapping("/profile")
    public String getProfilePage(HttpServletRequest request, Model model) {
        User user = authentication.authenticate(request);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "profile.html";
    }

    @GetMapping("/profile/edit")
    public String getProfileEditPage(HttpServletRequest request, Model model) {
        User user = authentication.authenticate(request);
        if (user == null) {
            return "redirect:/login";
        }

        // Create and prefill ProfileEditForm so Thymeleaf bindings exist
        ProfileEditForm form = new ProfileEditForm();
        form.setAddress(user.getAddress());

        model.addAttribute("user", user);
        model.addAttribute("profileEditForm", form);
        return "profile-edit.html";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(ProfileEditForm form, HttpServletRequest request) {
        User user = authentication.authenticate(request);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            logger.debug("Received profile edit request for user id: {}", user.getId());

            // Update address (safe even if null)
            user.setAddress(form.getAddress());

            // Save file only if provided and non-empty
            if (form.getProfile() != null && !form.getProfile().isEmpty()) {

                // Log original filename safely
                String orig = form.getProfile().getOriginalFilename();
                logger.info("Uploading profile file: {}", orig);

                // Ensure upload directory exists
                Path uploadDir = Paths.get(StorageService.DIRECTORY);
                if (!Files.exists(uploadDir)) {
                    try {
                        Files.createDirectories(uploadDir);
                        logger.info("Created upload directory: {}", uploadDir.toAbsolutePath());
                    } catch (IOException e) {
                        logger.error("Unable to create upload directory: {}", e.getMessage());
                        return "redirect:/profile?error=upload_dir";
                    }
                }

                try {
                    // Store new file using StorageService (it already generates unique name)
                    String storedFileName = storageService.store(form.getProfile());
                    if (storedFileName == null) {
                        // storage service decided not to store (empty file etc.)
                        logger.warn("StorageService returned null (no file stored)");
                    } else {
                        // Save new filename to user
                        String oldImage = user.getProfilePicture();
                        user.setProfilePicture(storedFileName);
                        userRepository.save(user); // save first to ensure DB persisted

                        // Delete old image only after new file stored and DB updated
                        if (oldImage != null && !oldImage.trim().isEmpty()) {
                            try {
                                Path oldPath = Paths.get(StorageService.DIRECTORY, oldImage);
                                Files.deleteIfExists(oldPath);
                                logger.debug("Deleted old profile image: {}", oldPath);
                            } catch (Exception e) {
                                logger.warn("Failed to delete old image: {}", e.getMessage());
                                // non-fatal — continue
                            }
                        }
                        return "redirect:/profile?success=true";
                    }
                } catch (IllegalArgumentException e) {
                    // Handle file validation errors (size, type)
                    logger.warn("File upload validation failed: {}", e.getMessage());
                    return "redirect:/profile?error=validation&message=" +
                            java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
                }
            } // end if file uploaded

            // If no file uploaded, just update other fields
            userRepository.save(user);
            return "redirect:/profile?success=true";

        } catch (Exception ex) {
            logger.error("Error updating profile for user id: {}", user.getId(), ex);
            return "redirect:/profile?error=server";
        }
    }

    @Autowired
    private com.admission.application.ApplicationRepository applicationRepository;

    @PostMapping("/profile/delete")
    @Transactional
    public String deleteProfile(HttpServletRequest request) {
        User user = authentication.authenticate(request);
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // Delete user's profile image if it exists
            if (user.getProfilePicture() != null) {
                try {
                    Path path = Paths.get(StorageService.DIRECTORY, user.getProfilePicture());
                    Files.deleteIfExists(path);
                } catch (Exception e) {
                    logger.warn("Failed to delete profile image for user {}: {}", user.getId(), e.getMessage());
                }
            }

            // Delete associated applications first
            applicationRepository.deleteByUser(user);

            // Delete user
            userRepository.delete(user);

            // Logout
            return "redirect:/logout";

        } catch (Exception e) {
            logger.error("Failed to delete profile for user {}", user.getId(), e);
            return "redirect:/profile?error=delete_failed";
        }
    }
}
