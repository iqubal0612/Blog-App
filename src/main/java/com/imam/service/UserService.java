package com.imam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.imam.model.User;
import com.imam.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public void saveUser(User user) {
		// TODO Auto-generated method stub
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	 public User fetchByEmail(String email) {
	        User userByEmail = userRepository.findByEmail(email);
	        return userByEmail;
	    }
	 
	 public User fetchByName(String authorName) {
	        User name = userRepository.findByName(authorName);
	        return name;
	    }

}


