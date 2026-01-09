package com.imam.controller;

import com.imam.model.Post;
import com.imam.model.User;
import com.imam.service.PostService;
import com.imam.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private UserService userService;

	@GetMapping
	public String listPosts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "desc") String sortDirection, Model model) {
		postService.showPost(page, sortDirection, model);
		return "list";
	}

	@GetMapping("/filter")
	public String filterPosts(@RequestParam(required = false) List<String> author,
			@RequestParam(required = false) String publishedDate,
			@RequestParam(value = "tag", required = false) List<String> tagNames,
			@RequestParam(defaultValue = "0") int page, Model model) {
		postService.filterPost(author, publishedDate, tagNames, page, model);
		return "list";
	}

	@GetMapping("/search")
	public String searchPosts(@RequestParam String query, Model model, @RequestParam(defaultValue = "0") int page) {
		postService.searchPost(query, model, page);
		return "list";
	}

	@GetMapping("/{id}")
	public String viewPost(@PathVariable int id, Model model) {
		Optional<Post> post = postService.findPostById(id);
		if (!post.isPresent()) {
			return "redirect:/posts";
		}
		model.addAttribute("post", post.get());

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()
				&& authentication.getPrincipal() instanceof UserDetails) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String username = userDetails.getUsername();
			User user = userService.fetchByEmail(username);
			model.addAttribute("name", user.getName());
		} else {
			model.addAttribute("name", "");
		}

		return "view";
	}

	@GetMapping("/create")
	public String showCreateForm(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String userEmail = userDetails.getUsername();
		User user = userService.fetchByEmail(userEmail);

		model.addAttribute("post", new Post());
		model.addAttribute("authorName", user.getName());
		model.addAttribute("role", user.getRole());
		return "create";
	}

	@PostMapping("/create")
	public String createPost(@ModelAttribute Post post, @RequestParam String tagsInput) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User loggedInUser = userService.fetchByEmail(userDetails.getUsername());

		post.setAuthor(loggedInUser.getName());
		postService.createPost(post, tagsInput);
		return "redirect:/posts";
	}

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable int id, Model model) {
		Optional<Post> postOpt = postService.findPostById(id);
		if (!postOpt.isPresent()) {
			return "redirect:/posts";
		}
		Post post = postOpt.get();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User loggedInUser = userService.fetchByEmail(userDetails.getUsername());
		System.out.println(loggedInUser.getName() + " " + post.getAuthor() + " "
				+ post.getAuthor().equals(loggedInUser.getName()));

		if (!post.getAuthor().equals(loggedInUser.getName()) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		model.addAttribute("post", post);
		model.addAttribute("tagSet", postService.getPostTagsAsString(post));
		model.addAttribute("loggedInUser", loggedInUser);
		model.addAttribute("role", loggedInUser.getRole());
		return "editPost";
	}

	@PostMapping("/update/{id}")
	public String updatePost(@PathVariable Integer id, @RequestParam String title, @RequestParam String content,
			@RequestParam String tag, @RequestParam String author) {
		Optional<Post> postOpt = postService.findPostById(id);
		if (!postOpt.isPresent()) {
			return "redirect:/posts";
		}
		Post post = postOpt.get();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User loggedInUser = userService.fetchByEmail(userDetails.getUsername());

		System.out
				.println(loggedInUser.getRole() + " " + post.getAuthor() + " " + author + " " + loggedInUser.getName());

		if (!post.getAuthor().equals(loggedInUser.getName()) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		if (!postService.updatePost(id, title, content, tag, author)) {
			return "redirect:/posts";
		}

		return "redirect:/posts/" + id;
	}

	@GetMapping("/delete/{id}")
	public String deletePost(@PathVariable int id) {
		Optional<Post> postOpt = postService.findPostById(id);
		if (!postOpt.isPresent()) {
			return "redirect:/posts";
		}

		Post post = postOpt.get();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		User loggedInUser = userService.fetchByEmail(userDetails.getUsername());

		if (!post.getAuthor().equals(loggedInUser.getName()) && !loggedInUser.getRole().equals("ROLE_ADMIN")) {
			return "error";
		}

		postService.deletePost(id);
		return "redirect:/posts";
	}

}
