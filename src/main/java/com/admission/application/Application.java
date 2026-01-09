package com.admission.application;

import org.hibernate.annotations.CreationTimestamp;

import com.admission.college.College;
import com.admission.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "application")
public class Application {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "college_id", nullable = false)
	private College college;
	
	@Column(nullable = false)
	private String course;
	
	@Column(nullable = false)
	private String gpa;
	
	private String documentsPath;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ApplicationStatus status;
	
	private String adminComment;
	
	@CreationTimestamp
	private java.time.Instant appliedDate;
	
	public Application() {
		this.status = ApplicationStatus.PENDING;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public College getCollege() {
		return college;
	}

	public void setCollege(College college) {
		this.college = college;
	}

	public String getCourse() {
		return course;
	}

	public void setCourse(String course) {
		this.course = course;
	}

	public String getGpa() {
		return gpa;
	}

	public void setGpa(String gpa) {
		this.gpa = gpa;
	}

	public String getDocumentsPath() {
		return documentsPath;
	}

	public void setDocumentsPath(String documentsPath) {
		this.documentsPath = documentsPath;
	}

	public ApplicationStatus getStatus() {
		return status;
	}

	public void setStatus(ApplicationStatus status) {
		this.status = status;
	}
	
	public String getAdminComment() {
		return adminComment;
	}

	public void setAdminComment(String adminComment) {
		this.adminComment = adminComment;
	}

	public java.time.Instant getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(java.time.Instant appliedDate) {
		this.appliedDate = appliedDate;
	}
	
}

