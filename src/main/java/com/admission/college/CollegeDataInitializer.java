package com.admission.college;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CollegeDataInitializer implements CommandLineRunner {

	@Autowired
	private CollegeRepository collegeRepository;

	@Override
	public void run(String... args) throws Exception {
		// Only initialize if database is empty - create single college
		if (collegeRepository.count() == 0) {
			College college = new College();
			college.setName("Tech University of Innovation");
			college.setLocation("San Francisco, CA");
			college.setCostPerYear(45000);
			college.setFoundedYear(1920);
			college.setDescription("Founded in 1920, Tech University of Innovation has consistently ranked among the top 1% of universities worldwide. Our campus spans 150 acres of state-of-the-art facilities, including the famous Turing AI Research Lab and the Bohr Physics Center. We believe in a holistic approach to education, combining rigorous academics with industry-aligned practical training.");
			college.setCourses(Arrays.asList(
				"B.Tech Computer Science & AI",
				"B.Sc Data Science",
				"M.Sc Artificial Intelligence",
				"B.Tech Information Technology",
				"M.Sc Cybersecurity"
			));
			college.setFeatures(Arrays.asList(
				"24/7 Digital Library",
				"High-Performance Computing Center",
				"Olympic-sized Swimming Pool",
				"On-campus Residential Housing",
				"Student Innovation Hub",
				"Modern Medical Center"
			));
			collegeRepository.save(college);
		}
	}
}

