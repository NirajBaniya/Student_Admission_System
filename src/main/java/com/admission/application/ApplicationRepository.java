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

	boolean existsByUserAndCollegeAndCourse(User user, com.admission.college.College college, String course);

	void deleteByUser(User user);

	@Query("SELECT a FROM Application a ORDER BY a.appliedDate DESC")
	List<Application> findAllOrderByAppliedDateDesc();

	List<Application> findByStatusOrderByAppliedDateDesc(ApplicationStatus status);

	@Query("SELECT a FROM Application a JOIN a.user u WHERE " +
			"LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(COALESCE(u.lastName, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
			"LOWER(a.course) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"ORDER BY a.appliedDate DESC")
	List<Application> searchApplications(@org.springframework.data.repository.query.Param("keyword") String keyword);

}
