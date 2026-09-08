package com.users.exceptions;

/** Un compte existe déjà avec ce nom d'utilisateur ou cet email. Traduit en HTTP 409. */
public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String field, String value) {
		super("A user already exists with " + field + " '" + value + "'");
	}
}
