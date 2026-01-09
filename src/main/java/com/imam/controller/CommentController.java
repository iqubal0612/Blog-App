package com.imam.controller;

import com.imam.model.Comment;
import com.imam.model.User;
import com.imam.service.CommentService;
import com.imam.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	@Autowired
	private UserService userService;

	@PostMapping("/add/{postId}")
	public String addComment(@PathVariable int postId, @RequestParam String name, @RequestParam String email,
			@RequestParam("comment") String commentContent) {
		commentService.addComment(postId, name, email, commentContent);
		return "redirect:/posts/" + postId;
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
	public String showEditForm(@PathVariable int id, Model model) {
		Comment comment = commentService.findCommentById(id);
		if (comment == null) {
			return "redirect:/posts";
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		User loggedInUser = userService.fetchByEmail(username);

		if (!loggedInUser.getEmail().equals(comment.getEmail()) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		model.addAttribute("commentData", comment);
		return "editComment";
	}

	@PostMapping("/update/{id}")
	@PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
	public String updateComment(@PathVariable int id, @ModelAttribute("commentData") Comment updatedComment) {
		Comment existingComment = commentService.findCommentById(id);
		if (existingComment == null) {
			return "redirect:/posts";
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		User loggedInUser = userService.fetchByEmail(username);

		System.out.println(loggedInUser.getEmail() + " " + existingComment.getEmail());

		if (!loggedInUser.getEmail().equals(existingComment.getEmail())
				&& !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		existingComment.setComment(updatedComment.getComment());
		commentService.updateComment(id, existingComment);
		return "redirect:/posts/" + existingComment.getPost().getId();
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("hasRole('AUTHOR') or hasRole('ADMIN')")
	public String deleteComment(@PathVariable int id) {
		Comment comment = commentService.findCommentById(id);
		if (comment == null) {
			return "redirect:/posts";
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String username = userDetails.getUsername();
		User loggedInUser = userService.fetchByEmail(username);

		if (!loggedInUser.getEmail().equals(comment.getEmail()) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		commentService.deleteComment(id);
		return "redirect:/posts/" + comment.getPost().getId();
	}
}
