package me.aco.marketplace_spring_ca.domain.intefrace;

import me.aco.marketplace_spring_ca.domain.entities.User;

public interface TokenService {

    public String generateToken(User user);

    public boolean validateToken(String token);
    
}
