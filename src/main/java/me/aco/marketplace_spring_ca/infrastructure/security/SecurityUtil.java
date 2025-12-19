package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class SecurityUtil {

	public static String hashPassword(String rawPassword) {
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}

	public static boolean verifyPassword(String rawPassword, String hashed) {
		if (rawPassword == null || hashed == null) return false;
		return BCrypt.checkpw(rawPassword, hashed);
	}
}
