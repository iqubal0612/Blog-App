package com.imam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imam.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

	User findByName(String author);

    boolean existsByEmail(String email);

    User findByEmail(String email);

    User findUserByName(String user);
}
