package me.aco.marketplace_spring_ca.infrastructure.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import me.aco.marketplace_spring_ca.domain.entities.User;

public class JWTUtil {
	
	private static final Algorithm algorithm = Algorithm.HMAC512("a85e024c34cbc78f559627e9d36cece0bb2bf1df7be8ca7eb606405e410fda4fbcac05cb8fa79bcabf21f8a9f6a48d0cb16eb95b52f44709e6f40b5aeb604909");
	
	public static String createToken(User u) {
		try {
		    String token = JWT.create()
		        .withIssuer("MarketplaceBackendApp")
		        .withAudience("MarketplaceBackendApp")
		        .withExpiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
		        .withClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", u.getUsername())
		        .withClaim("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier", u.getId())
		        .withClaim("http://schemas.microsoft.com/ws/2008/06/identity/claims/role", u.getRole().toString())
		        .sign(algorithm);
		    return token;
		} catch (JWTCreationException e){
		    e.printStackTrace();
			return null;
		}
	}
	
	public static boolean validateToken(String token) {
		DecodedJWT decodedJWT;
		try {
		    JWTVerifier verifier = JWT.require(algorithm)
		    	.withIssuer("MarketplaceBackendApp")
			    .withAudience("MarketplaceBackendApp")
		        .build();
		    decodedJWT = verifier.verify(token);
		} catch (JWTVerificationException exception){
		    // Invalid signature/claims
			return false;
		}
		if (decodedJWT.getExpiresAtAsInstant().isBefore(Instant.now()))
			return false;
		else
			return true;
	}
    
}
