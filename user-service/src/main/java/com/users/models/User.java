package com.users.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Compte utilisateur persisté dans user_db.
 * La table est nommée {@code users} (au pluriel) pour éviter le mot réservé SQL {@code user}.
 */
@Entity
@Table(
		name = "users",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_users_user_name", columnNames = "user_name"),
				@UniqueConstraint(name = "uk_users_email", columnNames = "email")
		}
)
@Getter
@Setter
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(nullable = false)
	private String email;

	/** Hash BCrypt du mot de passe, jamais le mot de passe en clair. */
	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	public User(String userName, String email, String passwordHash) {
		this.userName = userName;
		this.email = email;
		this.passwordHash = passwordHash;
	}
}
