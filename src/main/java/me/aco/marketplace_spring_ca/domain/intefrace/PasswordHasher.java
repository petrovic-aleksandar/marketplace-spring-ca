package me.aco.marketplace_spring_ca.domain.intefrace;

public interface PasswordHasher {

    String hashPassword(String rawPassword);

    boolean verifyPassword(String rawPassword, String hashedPassword);
    
}
    