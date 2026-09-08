package com.users.exceptions;

/** Identifiants de connexion invalides (nom d'utilisateur inconnu ou mauvais mot de passe). Traduit en HTTP 401. */
public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		super("Invalid userName or password");
	}
}
