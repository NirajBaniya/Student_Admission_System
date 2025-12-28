package com.admission.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;

	@Value("${ADMISSION_SYSTEM_ADMIN_PASSWORD}")
	private String password;

	@PostConstruct
	private void createAdminUserIfNotExist() {
		if (userRepository.count() == 0) {
			User admin = new User();
			admin.setFirstName("Admin");
			admin.setEmail("admin@gmail.com");
			admin.setUsername("admin@gmail.com");
			admin.setPassword(passwordEncoder.encode(password));
			admin.setGender(Gender.MALE);
			admin.setType(UserType.ADMIN);
			userRepository.save(admin);
		}
	}
}
