package com.users.dto;

import com.users.models.Role;
import com.users.models.User;

/** Réponse de {@code POST /users/login} : identifiant, nom et rôle, à conserver côté front. */
public record LoginResponse(Long id, String userName, Role role) {

	public static LoginResponse from(User user) {
		return new LoginResponse(user.getId(), user.getUserName(), user.getRole());
	}
}
