package com.users.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.users.models.User;


public interface UserRepository extends CrudRepository<User, Long> {

	Optional<User> findByUserName(String userName);

	boolean existsByUserName(String userName);

	boolean existsByEmail(String email);
}
