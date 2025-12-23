package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.domain.intefrace.PasswordHasher;

@Service
public class PasswordHasherImpl implements PasswordHasher {

	public String hash(String rawPassword) {
		return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
	}

	public boolean verify(String rawPassword, String hashedPassword) {
		if (rawPassword == null || hashedPassword == null) return false;
		return BCrypt.checkpw(rawPassword, hashedPassword);
	}
}
