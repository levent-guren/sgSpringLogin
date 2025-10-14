package tr.gov.sg.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import tr.gov.sg.entity.RefreshToken;
import tr.gov.sg.repository.PersonelRepository;
import tr.gov.sg.repository.RefreshTokenRepository;
import tr.gov.sg.util.TokenHasher;

@Service
public class LoginService {
	public record LoginResponse(String accessToken, String username, List<String> roles) {
	}

	@Autowired
	private PersonelRepository personelRepository;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private JwtService jwtService;
	@Autowired
	private TokenHasher tokenHasher;

	@Transactional
	public LoginResponse login(String adi, String rawPassword, HttpServletResponse res, String ip, String ua)
			throws Exception {
		var personel = personelRepository.findByAdi(adi)
				.orElseThrow(() -> new BadCredentialsException("Bad credentials"));
		if (!encoder.matches(rawPassword, personel.getSifre()))
			throw new BadCredentialsException("Bad credentials");

		var access = jwtService.generateAccessToken(new User(personel.getAdi(), personel.getSifre(), personel
				.getPersonelRols().stream().map(r -> new SimpleGrantedAuthority(r.getRol().getAdi())).toList()));

		// create opaque refresh token and store hashed
		var rawRefresh = newTokenValue();
		var rt = new RefreshToken();
		rt.setPersonel(personel);
		rt.setTokenHash(tokenHasher.hash(rawRefresh));
		rt.setFamilyId(UUID.randomUUID().toString());
		rt.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
		rt.setCreatedByIp(ip);
		rt.setUserAgent(ua);
		refreshTokenRepository.save(rt);

		setRefreshCookie(res, rawRefresh);

		return new LoginResponse(access, personel.getAdi(),
				personel.getPersonelRols().stream().map(r -> r.getRol().getAdi()).toList());
	}

	public LoginResponse refresh(String rawTokenFromCookie, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		var match = refreshTokenRepository.findValidByNotExpired(Instant.now()).stream()
				.filter(rt -> !rt.isRevoked() && tokenHasher.matches(rawTokenFromCookie, rt.getTokenHash())).findFirst()
				.orElseThrow(() -> new BadCredentialsException("Invalid refresh"));

		// reuse detection
		if (match.isRevoked()) {
			revokeFamily(match.getFamilyId()); // possible theft
			throw new BadCredentialsException("Token reuse detected");
		}
		if (match.getExpiresAt().isBefore(Instant.now()))
			throw new BadCredentialsException("Expired");

		// rotate
		match.setRevoked(true);
		refreshTokenRepository.save(match);

		var personel = match.getPersonel();
		var access = jwtService.generateAccessToken(new User(personel.getAdi(), personel.getSifre(), personel
				.getPersonelRols().stream().map(r -> new SimpleGrantedAuthority(r.getRol().getAdi())).toList()));

		var rawNew = newTokenValue();
		var newRt = new RefreshToken();
		newRt.setPersonel(personel);
		newRt.setTokenHash(tokenHasher.hash(rawNew));
		newRt.setFamilyId(match.getFamilyId());
		newRt.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
		newRt.setCreatedByIp(req.getRemoteAddr());
		newRt.setUserAgent(req.getHeader("User-Agent"));
		refreshTokenRepository.save(newRt);

		match.setReplacedByTokenId(newRt.getId());
		refreshTokenRepository.save(match);

		setRefreshCookie(res, rawNew);

		return new LoginResponse(access, personel.getAdi(),
				personel.getPersonelRols().stream().map(r -> r.getRol().getAdi()).toList());
	}

	public void logout(String rawTokenFromCookie, HttpServletResponse res) {
		// revoke just this family (or all families for user, your choice)
		refreshTokenRepository.findAll().forEach(rt -> {
			if (encoder.matches(rawTokenFromCookie, rt.getTokenHash())) {
				revokeFamily(rt.getFamilyId());
			}
		});
		clearRefreshCookie(res);
	}

	private void revokeFamily(String familyId) {
		refreshTokenRepository.revokeFamily(familyId); // implement update query to mark revoked=true
	}

	private static String newTokenValue() {
		byte[] b = new byte[64];
		new SecureRandom().nextBytes(b);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
	}

	private void setRefreshCookie(HttpServletResponse res, String rawValue) {
		ResponseCookie cookie = ResponseCookie.from("refresh_token", rawValue).httpOnly(true).secure(false)
				.sameSite("Strict").path("/api/v1/auth/refresh") // only refresh endpoint reads it
				.maxAge(Duration.ofDays(30)).build();
		res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private void clearRefreshCookie(HttpServletResponse res) {
		ResponseCookie cookie = ResponseCookie.from("refresh_token", "").httpOnly(true)
				/* .secure(true) */.sameSite("Strict").path("/api/auth/refresh").maxAge(0).build();
		res.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
