package com.users.dto;

import com.users.models.User;

/** Vue publique d'un utilisateur : jamais le hash du mot de passe. */
public record UserResponse(Long id, String userName, String email) {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getUserName(), user.getEmail());
	}
}
