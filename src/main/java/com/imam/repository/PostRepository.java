package com.imam.repository;

import com.imam.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
	List<Post> findByTitleContainingIgnoreCase(String title);

	List<Post> findByTagsNameContainingIgnoreCase(String tags);

	List<Post> findByAuthorContainingIgnoreCase(String author);

	List<Post> findByTagsName(String tagName);

}
