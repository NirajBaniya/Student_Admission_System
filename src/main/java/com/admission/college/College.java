package com.admission.college;

import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "college")
public class College {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String location;
	
	@Column(nullable = false)
	private double costPerYear;
	
	@Column(length = 2000)
	private String description;
	
	private String imagePath;
	
	@ElementCollection
	@Column(name = "course")
	private List<String> courses;
	
	@ElementCollection
	@Column(name = "feature")
	private List<String> features;
	
	private Integer foundedYear;
	
	@CreationTimestamp
	private java.time.Instant createdAt;
	
	@UpdateTimestamp
	private java.time.Instant updatedAt;
	
	public College() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public double getCostPerYear() {
		return costPerYear;
	}

	public void setCostPerYear(double costPerYear) {
		this.costPerYear = costPerYear;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public List<String> getCourses() {
		return courses;
	}

	public void setCourses(List<String> courses) {
		this.courses = courses;
	}

	public java.time.Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(java.time.Instant createdAt) {
		this.createdAt = createdAt;
	}

	public java.time.Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(java.time.Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<String> getFeatures() {
		return features;
	}

	public void setFeatures(List<String> features) {
		this.features = features;
	}

	public Integer getFoundedYear() {
		return foundedYear;
	}

	public void setFoundedYear(Integer foundedYear) {
		this.foundedYear = foundedYear;
	}
	
}

