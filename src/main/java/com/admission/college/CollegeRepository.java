package com.admission.college;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeRepository extends JpaRepository<College, Integer> {
	
	// Get the single college (assuming only one college exists)
	default College getSingleCollege() {
		return findAll().stream().findFirst().orElse(null);
	}
	
}

