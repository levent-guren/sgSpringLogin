package tr.gov.sg.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {
	@Autowired
	private SecurityService securityService;
	private Duration ttl;

	public JwtService(@Value("${security.jwt.ttl}") int ttlDuration) throws Exception {
		ttl = Duration.ofMinutes(ttlDuration);
		ttl = Duration.ofSeconds(5);
	}

	public String generateAccessToken(UserDetails user) throws Exception {
		Instant now = Instant.now();
		return Jwts.builder().id(UUID.randomUUID().toString()) // jti (setId -> id)
				.issuer("login") // setIssuer -> issuer
				.subject(user.getUsername()) // setSubject -> subject
				.claim("roller", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.issuedAt(Date.from(now)) // setIssuedAt -> issuedAt
				.expiration(Date.from(now.plus(ttl))) // setExpiration -> expiration
				// .expiration(Date.from(now)) // setExpiration -> expiration
				.signWith(securityService.getPrivateKey(), Jwts.SIG.RS256) // SignatureAlgorithm.RS256 yerine
				.compact();
	}

	public Jws<Claims> parse(String token) throws Exception {
		return Jwts.parser().verifyWith(securityService.getPublicKey()).build().parseSignedClaims(token);
	}

}
