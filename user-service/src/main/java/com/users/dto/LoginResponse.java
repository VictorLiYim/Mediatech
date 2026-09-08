package com.users.dto;

import com.users.models.User;

/** Réponse de {@code POST /users/login} : identifiant + nom, à conserver côté front. */
public record LoginResponse(Long id, String userName) {

	public static LoginResponse from(User user) {
		return new LoginResponse(user.getId(), user.getUserName());
	}
}
