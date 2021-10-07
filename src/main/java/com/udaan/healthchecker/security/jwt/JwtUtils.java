package com.udaan.healthchecker.security.jwt;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.udaan.healthchecker.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${healthchecker.app.jwtSecret}")
	private String jwtSecret;

	@Value("${healthchecker.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		Map<String, Object> claimsMap = new HashMap<>();
		claimsMap.put("serviceName", userPrincipal.getEmail());
		claimsMap.put("hostname", userPrincipal.getUsername());
		claimsMap.put("sub", userPrincipal.getUsername());
		claimsMap.put("iat", new Date());
		claimsMap.put("exp", new Date((new Date()).getTime() + jwtExpirationMs));

		logger.info(userPrincipal.getUsername());
		return Jwts.builder()
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.setClaims(claimsMap)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
