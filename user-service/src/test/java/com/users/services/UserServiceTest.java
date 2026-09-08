package com.users.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.users.exceptions.InvalidCredentialsException;
import com.users.exceptions.UserAlreadyExistsException;
import com.users.exceptions.UserNotFoundException;
import com.users.models.User;
import com.users.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserService userService;

	@Test
	void createUser_hashesPasswordAndSaves() {
		when(userRepository.existsByUserName("alice")).thenReturn(false);
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("HASH");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		User created = userService.createUser("alice", "alice@example.com", "password123");

		assertThat(created.getUserName()).isEqualTo("alice");
		assertThat(created.getEmail()).isEqualTo("alice@example.com");
		assertThat(created.getPasswordHash()).isEqualTo("HASH");
	}

	@Test
	void createUser_whenUserNameTaken_throwsConflict() {
		when(userRepository.existsByUserName("alice")).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser("alice", "alice@example.com", "password123"))
				.isInstanceOf(UserAlreadyExistsException.class);
	}

	@Test
	void createUser_whenEmailTaken_throwsConflict() {
		when(userRepository.existsByUserName("alice")).thenReturn(false);
		when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

		assertThatThrownBy(() -> userService.createUser("alice", "alice@example.com", "password123"))
				.isInstanceOf(UserAlreadyExistsException.class);
	}

	@Test
	void login_withValidCredentials_returnsUser() {
		User stored = new User("alice", "alice@example.com", "HASH");
		when(userRepository.findByUserName("alice")).thenReturn(Optional.of(stored));
		when(passwordEncoder.matches("password123", "HASH")).thenReturn(true);

		User result = userService.login("alice", "password123");

		assertThat(result).isSameAs(stored);
	}

	@Test
	void login_withWrongPassword_throwsInvalidCredentials() {
		User stored = new User("alice", "alice@example.com", "HASH");
		when(userRepository.findByUserName("alice")).thenReturn(Optional.of(stored));
		when(passwordEncoder.matches("wrong", "HASH")).thenReturn(false);

		assertThatThrownBy(() -> userService.login("alice", "wrong"))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void login_withUnknownUser_throwsInvalidCredentials() {
		when(userRepository.findByUserName("ghost")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.login("ghost", "whatever"))
				.isInstanceOf(InvalidCredentialsException.class);
	}

	@Test
	void getById_whenMissing_throwsNotFound() {
		when(userRepository.findById(42L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getById(42L))
				.isInstanceOf(UserNotFoundException.class);
	}
}