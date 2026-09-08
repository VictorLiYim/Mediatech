package com.users.dto;

import jakarta.validation.constraints.NotBlank;

/** Corps de la requête {@code POST /users/login}. */
public record LoginRequest(

		@NotBlank
		String userName,

		@NotBlank
		String password
) {
}
