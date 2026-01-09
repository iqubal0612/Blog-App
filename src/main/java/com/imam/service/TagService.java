package com.imam.service;

import com.imam.model.Tag;
import com.imam.repository.TagRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TagService {

	@Autowired
	private TagRepository tagRepository;

	public List<Tag> findAllTags() {
		return tagRepository.findAll();
	}

	public Tag findTagByName(String name) {
		return tagRepository.findByName(name);
	}

	@Transactional
	public void saveTag(Tag tag) {
		tag.setCreatedAt(LocalDate.now());
		tag.setUpdatedAt(LocalDate.now());
		tagRepository.save(tag);
	}

	@Transactional
	public void deleteTag(int id) {
		Tag tag = tagRepository.findById(id).orElse(null);
		if (tag != null && tag.getPosts().isEmpty()) {
			tagRepository.delete(tag);
		} else {
			throw new RuntimeException("Cannot delete tag with active posts.");
		}
	}

}
