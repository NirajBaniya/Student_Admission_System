package com.admission.application;

import org.springframework.web.multipart.MultipartFile;

public class ApplicationForm {

	private int collegeId;
	private String course;
	private String gpa;
	private MultipartFile documents;

	public ApplicationForm() {
	}

	public int getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(int collegeId) {
		this.collegeId = collegeId;
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

	public MultipartFile getDocuments() {
		return documents;
	}

	public void setDocuments(MultipartFile documents) {
		this.documents = documents;
	}

}
