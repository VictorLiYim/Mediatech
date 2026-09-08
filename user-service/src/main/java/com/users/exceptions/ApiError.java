package com.users.exceptions;

import java.time.Instant;
import java.util.Map;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Corps de réponse uniforme pour toutes les erreurs renvoyées par l'API.
 * {@code fieldErrors} n'est présent que pour les erreurs de validation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
		Instant timestamp,
		int status,
		String error,
		String message,
		Map<String, String> fieldErrors
) {

	public static ApiError of(HttpStatus status, String message) {
		return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, null);
	}

	public static ApiError of(HttpStatus status, String message, Map<String, String> fieldErrors) {
		return new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, fieldErrors);
	}
}
