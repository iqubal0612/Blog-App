package com.imam.repository;

import com.imam.model.Tag;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
	Tag findByName(String name);

	List<Tag> findAll();
}
