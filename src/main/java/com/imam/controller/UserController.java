package com.imam.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.imam.model.User;
import com.imam.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String registerUser() {
		return "redirect:/posts";
	}

	@GetMapping("/signUp")
	public String showRegisterForm() {
		return "register";
	}

	@GetMapping("/login")
	public String showLoginForm() {
		return "login";
	}

	@PostMapping("/register")
	public String registerUser(@ModelAttribute User user, Model model) {
		user.setRole("ROLE_AUTHOR");
		userService.saveUser(user);
		return "redirect:/login";
	}
}
