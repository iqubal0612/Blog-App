package com.imam.service;

import com.imam.model.Post;
import com.imam.model.Tag;
import com.imam.model.User;
import com.imam.repository.PostRepository;
import com.imam.repository.TagRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private TagService tagService;

	@Autowired
	private UserService userService;

	private final int PAGE_SIZE = 10;

	public Page<Post> getPaginatedPosts(int page, String sortDirection) {
		Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by("publishedAt").ascending()
				: Sort.by("publishedAt").descending();
		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);
		return postRepository.findAll(pageable);
	}

	public int getTotalPages() {
		return (int) Math.ceil((double) postRepository.findAll().size() / PAGE_SIZE);
	}

	public List<String> getAllAuthors() {
		return postRepository.findAll().stream().map(Post::getAuthor).distinct().collect(Collectors.toList());
	}

	public List<Post> filterPosts(List<String> authors, String publishedDate, List<String> tagNames, int page) {
		List<Post> posts = findAllPosts();

		if (authors != null && !authors.isEmpty()) {
			posts = posts.stream().filter(post -> authors.contains(post.getAuthor())).collect(Collectors.toList());
		}

		if (publishedDate != null && !publishedDate.isEmpty()) {
			LocalDate date = LocalDate.parse(publishedDate);
			posts = posts.stream().filter(post -> post.getPublishedAt().equals(date)).collect(Collectors.toList());
		}

		if (tagNames != null && !tagNames.isEmpty()) {
			posts = posts.stream()
					.filter(post -> post.getTags().stream().anyMatch(tag -> tagNames.contains(tag.getName())))
					.collect(Collectors.toList());
		}

		int pageSize = 6;
		int start = page * pageSize;
		int end = Math.min(start + pageSize, posts.size());
		return posts.subList(start, end);
	}

	public int getTotalFilteredPages(List<String> authors, String publishedDate, List<String> tagNames) {
		List<Post> posts = findAllPosts();

		if (authors != null && !authors.isEmpty()) {
			posts = posts.stream().filter(post -> authors.contains(post.getAuthor())).collect(Collectors.toList());
		}

		if (publishedDate != null && !publishedDate.isEmpty()) {
			LocalDate date = LocalDate.parse(publishedDate);
			posts = posts.stream().filter(post -> post.getPublishedAt().equals(date)).collect(Collectors.toList());
		}

		if (tagNames != null && !tagNames.isEmpty()) {
			posts = posts.stream()
					.filter(post -> post.getTags().stream().anyMatch(tag -> tagNames.contains(tag.getName())))
					.collect(Collectors.toList());
		}

		int pageSize = 6;
		int totalPosts = posts.size();
		int totalPages = (int) Math.ceil((double) totalPosts / pageSize);

		return totalPages;
	}

	public List<Post> searchPosts(String query, int page) {
		Set<Post> results = new HashSet<>();
		results.addAll(postRepository.findByAuthorContainingIgnoreCase(query));
		results.addAll(postRepository.findByTagsNameContainingIgnoreCase(query));
		results.addAll(postRepository.findByTitleContainingIgnoreCase(query));

		return getPage(new ArrayList<>(results), page);
	}

	public int getTotalSearchPages(String query) {
		return (int) Math.ceil((double) searchPosts(query, 0).size() / PAGE_SIZE);
	}

	public Optional<Post> findPostById(int id) {
		return postRepository.findById(id);
	}

	@Transactional
	public void createPost(Post post, String tagsInput) {
		post.setCreatedAt(LocalDate.now());
		post.setPublishedAt(LocalDate.now());
		post.setUpdatedAt(LocalDate.now());
		post.setExcerpt(post.getContent().length() > 5 ? post.getContent().substring(0, 5) : post.getContent());
		post.setPublished(true);

		post.setTags(parseTags(tagsInput));
		postRepository.save(post);
	}

	@Transactional
	public boolean updatePost(int id, String title, String content, String tagInput, String author) {
		Optional<Post> postOptional = findPostById(id);
		if (!postOptional.isPresent()) {
			return false;
		}
		Post post = postOptional.get();
		post.setUpdatedAt(LocalDate.now());
		post.setAuthor(author);
		post.setContent(content);
		post.setTitle(title);
		post.setTags(parseTags(tagInput));

		postRepository.save(post);
		return true;
	}

	@Transactional
	public void deletePost(int id) {
		Optional<Post> postOptional = postRepository.findById(id);
		if (postOptional.isPresent()) {
			Post post = postOptional.get();
			Set<Tag> tags = post.getTags();

			postRepository.delete(post);

			for (Tag tag : tags) {
				if (tag.getPosts().isEmpty()) {
					tagRepository.delete(tag);
				}
			}
		}
	}

	public List<Post> findAllPosts() {
		return postRepository.findAll();
	}

	public String getPostTagsAsString(Post post) {
		return post.getTags().stream().map(Tag::getName).collect(Collectors.joining(","));
	}

	private List<Post> getPage(List<Post> posts, int page) {
		int start = page * PAGE_SIZE;
		int end = Math.min(start + PAGE_SIZE, posts.size());
		return posts.subList(start, end);
	}

	private Set<Tag> parseTags(String tagsInput) {
		Set<String> tagNames = Stream.of(tagsInput.split(",")).map(String::trim).collect(Collectors.toSet());

		Set<Tag> tags = new HashSet<>();
		for (String tagName : tagNames) {
			Tag tag = tagRepository.findByName(tagName);
			if (tag == null) {
				tag = new Tag();
				tag.setName(tagName);
				tag.setCreatedAt(LocalDate.now());
				tag.setUpdatedAt(LocalDate.now());
				tagRepository.save(tag);
			}
			tags.add(tag);
		}
		return tags;
	}

	public void showPost(int page, String sortDirection, Model model) {
		// TODO Auto-generated method stub

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userService.fetchByEmail(username);
			model.addAttribute("user", user);
		}

		model.addAttribute("posts", getPaginatedPosts(page, sortDirection));
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", getTotalPages());
		model.addAttribute("authors", getAllAuthors());
		model.addAttribute("tags", tagService.findAllTags());
		model.addAttribute("sortField", "publishedAt");
		model.addAttribute("sortDirection", sortDirection);
	}

	public void filterPost(List<String> author, String publishedDate, List<String> tagNames, int page, Model model) {
		// TODO Auto-generated method stub

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userService.fetchByEmail(username);
			model.addAttribute("user", user);
		}

		model.addAttribute("posts", filterPosts(author, publishedDate, tagNames, page));
		model.addAttribute("authors", getAllAuthors());
		model.addAttribute("tags", tagService.findAllTags());
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedAuthors", author);
		model.addAttribute("publishedDate", publishedDate);
		model.addAttribute("selectedTags", tagNames);
		model.addAttribute("totalPages", getTotalFilteredPages(author, publishedDate, tagNames));
	}

	public void searchPost(String query, Model model, int page) {
		// TODO Auto-generated method stub

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.isAuthenticated()) {
			String username = authentication.getName();
			User user = userService.fetchByEmail(username);
			model.addAttribute("user", user);
		}

		model.addAttribute("posts", searchPosts(query, page));
		model.addAttribute("currentPage", page);
		model.addAttribute("query", query);
		model.addAttribute("totalPages", getTotalSearchPages(query));

	}
}
