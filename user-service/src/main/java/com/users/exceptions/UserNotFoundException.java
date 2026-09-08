package com.users.exceptions;

/** Aucun utilisateur ne correspond à l'identifiant demandé. Traduit en HTTP 404. */
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("No user found with id " + id);
	}
}
