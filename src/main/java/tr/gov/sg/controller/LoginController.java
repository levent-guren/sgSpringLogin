package tr.gov.sg.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tr.gov.sg.dto.LoginRequestDTO;
import tr.gov.sg.dto.LoginResponseDTO;
import tr.gov.sg.service.LoginService;

@RestController
@RequestMapping("/api/v1/auth")
public class LoginController {
	@Autowired
	private LoginService loginService;

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO login, HttpServletRequest httpReq,
			HttpServletResponse res) throws Exception {
		var r = loginService.login(login.getAdi(), login.getSifre(), res, httpReq.getRemoteAddr(),
				httpReq.getHeader("User-Agent"));
		return ResponseEntity.ok(new LoginResponseDTO(r.accessToken()));
	}

	@PostMapping("/refresh")
	public LoginResponseDTO refresh(@CookieValue(name = "refresh_token", required = false) String refreshToken,
			HttpServletRequest httpReq, HttpServletResponse res) throws Exception {
		if (refreshToken == null)
			throw new BadCredentialsException("No refresh cookie");
		var r = loginService.refresh(refreshToken, httpReq, res);
		return new LoginResponseDTO(r.accessToken());
	}

	@PostMapping("/logout")
	public void logout(@CookieValue(name = "refresh_token", required = false) String refreshToken,
			HttpServletResponse res) {
		if (refreshToken != null)
			loginService.logout(refreshToken, res);
	}
}
