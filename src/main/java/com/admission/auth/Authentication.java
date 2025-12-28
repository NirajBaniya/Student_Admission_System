package com.admission.auth;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.admission.Utilities;
import com.admission.user.User;
import com.admission.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class Authentication {
	
	@Autowired
	private UserRepository userRepository;

	public User authenticate(HttpServletRequest request) {
		
		//DONE: get the value of cookie SESSIONID
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			return null;
		}
		
		Cookie userCookie = null;
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals(Utilities.COOKIE_NAME)) {
				userCookie = cookie;
				break;
			}
		}
		
		//cookie SESSIONID not found
		if(userCookie == null) {
			return null;
		}
		
//		userCookie.getValue() -> "ZHyFmZ37Lf7CeeOmcXiw"
		
		//DONE: get user from database with given session ID
		User user = userRepository.findBySession(userCookie.getValue());
		
		
		
		//TODO: if user found: return that user
		//		if not found: redirect to /login
		
		return user;
	}
	
}