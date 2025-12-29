package me.aco.marketplace_spring_ca.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;

import me.aco.marketplace_spring_ca.domain.entities.User;
import me.aco.marketplace_spring_ca.domain.intefrace.TokenService;

@Service
public class JwtTokenService implements TokenService {
	
	private static final Algorithm algorithm = Algorithm.HMAC512("a85e024c34cbc78f559627e9d36cece0bb2bf1df7be8ca7eb606405e410fda4fbcac05cb8fa79bcabf21f8a9f6a48d0cb16eb95b52f44709e6f40b5aeb604909");
	
	public String generateToken(User u) {
		try {
		    String token = JWT.create()
		        .withIssuer("MarketplaceBackendApp")
		        .withAudience("MarketplaceBackendApp")
		        .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
		        .withClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", u.getUsername())
		        .withClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier", u.getId())
		        .withClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role", u.getRole().getDisplayName())
		        .sign(algorithm);
		    return token;
		} catch (JWTCreationException e){
		    e.printStackTrace();
			return null;
		}
	}
	
	public boolean validateToken(String token) {
		try {
		    JWTVerifier verifier = JWT.require(algorithm)
		    	.withIssuer("MarketplaceBackendApp")
			    .withAudience("MarketplaceBackendApp")
		        .build();
		    verifier.verify(token); // This automatically checks expiration
		    return true;
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims or expired
			return false;
		}
	}
	
	public boolean validateTokenIgnoringExpiration(String token) {
		try {
		    JWTVerifier verifier = JWT.require(algorithm)
		    	.withIssuer("MarketplaceBackendApp")
			    .withAudience("MarketplaceBackendApp")
		        .acceptExpiresAt(Long.MAX_VALUE) // Accept any expiration time
		        .build();
		    verifier.verify(token);
		    return true;
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
			return false;
		}
	}

	public boolean isTokenExpired(String token) {
		try {
			com.auth0.jwt.interfaces.DecodedJWT jwt = JWT.decode(token);
			Instant expiresAt = jwt.getExpiresAt().toInstant();
			return expiresAt.isBefore(Instant.now());
		} catch (JWTVerificationException exception) {
			// If token is invalid or missing expiration, treat as expired
			return true;
		}
	}
    
	public String extractUsername(String token) {
		try {
			return com.auth0.jwt.JWT.decode(token).getClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name").asString();
		} catch (Exception e) {
			return null;
		}
	}

	public boolean isTokenValid(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
		String username = extractUsername(token);
		return username != null && username.equals(userDetails.getUsername()) && validateToken(token);
	}
}
