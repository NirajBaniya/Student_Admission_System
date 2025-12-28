package com.admission.profile;

import org.springframework.web.multipart.MultipartFile;

public class ProfileEditForm {

	private String address;
	private MultipartFile profile;

	public ProfileEditForm() {
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public MultipartFile getProfile() {
		return profile;
	}

	public void setProfile(MultipartFile profile) {
		this.profile = profile;
	}

	
}
