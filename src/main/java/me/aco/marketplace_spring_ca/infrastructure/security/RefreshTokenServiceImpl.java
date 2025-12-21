package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.stereotype.Service;

import me.aco.marketplace_spring_ca.domain.intefrace.RefreshTokenService;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Override
    public String generateRefreshToken() {
        return java.util.UUID.randomUUID().toString();
    }
    
}
