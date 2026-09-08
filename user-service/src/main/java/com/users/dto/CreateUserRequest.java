package com.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps de la requête {@code POST /users}. Le mot de passe est reçu en clair puis haché par le service. */
public record CreateUserRequest(

		@NotBlank
		@Size(min = 3, max = 50)
		String userName,

		@NotBlank
		@Email
		@Size(max = 255)
		String email,

		@NotBlank
		@Size(min = 8, max = 100)
		String password
) {
}
