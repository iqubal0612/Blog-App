package com.imam.service;

import com.imam.model.Comment;
import com.imam.model.Post;
import com.imam.repository.CommentRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CommentService {

	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private PostService postService;

	public List<Comment> findAllComments() {
		return commentRepository.findAll();
	}

	public Comment findCommentById(int id) {
		return commentRepository.findById(id).orElse(null);
	}


	@Transactional
	public void deleteComment(int id) {
		commentRepository.deleteById(id);
	}

	@Transactional
	public void addComment(int postId, String name, String email, String commentContent) {
		Post post = postService.findPostById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));

		Comment comment = new Comment();
		comment.setName(name);
		comment.setEmail(email);
		comment.setComment(commentContent);
		comment.setCreatedAt(LocalDate.now());
		comment.setUpdatedAt(LocalDate.now());
		comment.setPost(post);
		commentRepository.save(comment);
	}

	public void updateComment(int id, Comment comment) {
		// TODO Auto-generated method stub
		Comment existingComment = findCommentById(id);
		comment.setUpdatedAt(LocalDate.now());
		comment.setId(id);
		comment.setPost(existingComment.getPost());
		commentRepository.save(comment);
	}
}
