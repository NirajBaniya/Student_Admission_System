package com.admission;

import java.security.SecureRandom;

public class Utilities {
	
	/**
	 * checks whether the provided password is valid satisfying the required constraints
	 * at least 8 characters
	 * at least 1 numeric
	 * at least 1 uppercase letter
	 * @param password
	 * @return true if password is valid
	 */
	public static boolean isValidPassword(String password) {
		return password.length() >= 8
		&& password.matches(".*[0-9].*")
		&& password.matches(".*[A-Z].*");
	}
	
	public static String COOKIE_NAME = "SESSIONID";

	static String sampleSpace = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	static int SIZE = sampleSpace.length();

	public static String getRandomString(int len) {

		SecureRandom random = new SecureRandom();

		String ans = "";

		for (int i = 1; i <= len; i++) {
			int index = random.nextInt(SIZE);
			ans = ans + sampleSpace.charAt(index); // sampleSpace[index]
		}

		return ans;
	}
	
	/**
	 * Validates email format using a simple regex pattern
	 * @param email the email to validate
	 * @return true if email format is valid
	 */
	public static boolean isValidEmail(String email) {
		if (email == null || email.isBlank()) {
			return false;
		}
		// Basic email validation pattern
		String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
		return email.matches(emailPattern);
	}
	
}