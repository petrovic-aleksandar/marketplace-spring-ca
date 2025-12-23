package me.aco.marketplace_spring_ca.domain.intefrace;

public interface PasswordHasher {

    String hash(String rawPassword);

    boolean verify(String rawPassword, String hashedPassword);
    
}
    