package com.users.controllers;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.users.dto.CreateUserRequest;
import com.users.dto.LoginRequest;
import com.users.dto.LoginResponse;
import com.users.dto.UserResponse;
import com.users.models.User;
import com.users.services.UserService;

import jakarta.validation.Valid;

/** Points d'entrée REST des comptes utilisateurs. Link UserService. */
@RestController
@RequestMapping("/users")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
		log.info("POST /users userName={}", request.userName());
		User user = userService.createUser(request.userName(), request.email(), request.password());
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(user.getId())
				.toUri();
		return ResponseEntity.created(location).body(UserResponse.from(user));
	}

	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		log.info("POST /users/login userName={}", request.userName());
		User user = userService.login(request.userName(), request.password());
		return LoginResponse.from(user);
	}

	@GetMapping("/{id}")
	public UserResponse getUser(@PathVariable Long id) {
		log.info("GET /users/{}", id);
		return UserResponse.from(userService.getById(id));
	}
}
