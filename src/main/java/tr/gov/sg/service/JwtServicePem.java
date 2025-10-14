package tr.gov.sg.service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import tr.gov.sg.util.PemUtils;

@Service
public class JwtServicePem {
	private Duration ttl;

	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public JwtServicePem(@Value("${security.jwt.private-key-pem}") Resource privateKeyPem,
			@Value("${security.jwt.public-key-pem}") Resource publicKeyPem,
			@Value("${security.jwt.ttl}") int ttlDuration) throws Exception {
		privateKey = PemUtils.readPrivateKey(privateKeyPem.getInputStream());
		publicKey = PemUtils.readPublicKey(publicKeyPem.getInputStream());
		ttl = Duration.ofMinutes(ttlDuration);
	}

	public String generateAccessToken(UserDetails user) {
		Instant now = Instant.now();
		return Jwts.builder().id(UUID.randomUUID().toString()) // jti (setId -> id)
				.issuer("login") // setIssuer -> issuer
				.subject(user.getUsername()) // setSubject -> subject
				.claim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.issuedAt(Date.from(now)) // setIssuedAt -> issuedAt
				.expiration(Date.from(now.plus(ttl))) // setExpiration -> expiration7
				.signWith(privateKey, Jwts.SIG.RS256) // SignatureAlgorithm.RS256 yerine
				.compact();
	}

	public Jws<Claims> parse(String token) {
		return Jwts.parser().verifyWith(publicKey).build().parseSignedClaims(token);
	}

}
