package com.admission.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.admission.user.User;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
	
	List<Application> findByUserOrderByAppliedDateDesc(User user);
	
	boolean existsByUserAndCollege(User user, com.admission.college.College college);
	
	@Query("SELECT a FROM Application a ORDER BY a.appliedDate DESC")
	List<Application> findAllOrderByAppliedDateDesc();
	
}

