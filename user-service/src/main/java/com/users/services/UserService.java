package com.users.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.users.exceptions.InvalidCredentialsException;
import com.users.exceptions.UserAlreadyExistsException;
import com.users.exceptions.UserNotFoundException;
import com.users.models.User;
import com.users.repositories.UserRepository;

/**
 * Logique métier utilisateurs.
 * Connaît pas la couche web
 */
@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Crée un compte après avoir vérifié l'unicité du nom d'utilisateur et de l'email.
	 */
	@Transactional
	public User createUser(String userName, String email, String rawPassword) {
		log.debug("Creating user with userName={}", userName);
		if (userRepository.existsByUserName(userName)) {
			throw new UserAlreadyExistsException("userName", userName);
		}
		if (userRepository.existsByEmail(email)) {
			throw new UserAlreadyExistsException("email", email);
		}
		User user = new User(userName, email, passwordEncoder.encode(rawPassword));
		User saved = userRepository.save(user);
		log.info("Created user id={} userName={}", saved.getId(), saved.getUserName());
		return saved;
	}

	/**
	 * Vérifie les identifiants et renvoie l'utilisateur correspondant.
	 *
	 * @throws InvalidCredentialsException si le nom d'utilisateur est inconnu ou le mot de passe faux
	 */
	@Transactional(readOnly = true)
	public User login(String userName, String rawPassword) {
		log.debug("Login attempt for userName={}", userName);
		User user = userRepository.findByUserName(userName)
				.orElseThrow(InvalidCredentialsException::new);
		if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
			log.warn("Failed login for userName={}", userName);
			throw new InvalidCredentialsException();
		}
		log.info("Successful login for user id={} userName={}", user.getId(), user.getUserName());
		return user;
	}

	/**
	 * Renvoie un utilisateur par son identifiant.
	 *
	 * @throws UserNotFoundException si aucun utilisateur ne porte cet identifiant
	 */
	@Transactional(readOnly = true)
	public User getById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
	}
}
