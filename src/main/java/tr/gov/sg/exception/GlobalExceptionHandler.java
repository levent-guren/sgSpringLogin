package tr.gov.sg.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

	// 🔸 401 - Authentication hataları
	@ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> handleAuthenticationException(Exception ex) {
		return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	// 🔸 403 - Yetki (authorization) hataları
	@ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
		return buildResponse(HttpStatus.FORBIDDEN, "Erişim yetkiniz bulunmamaktadır");
	}

	// 🔸 400 - Genel doğrulama hataları
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleBadRequest(Exception ex) {
		return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	// 🔸 500 - Diğer tüm beklenmeyen hatalar
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleGeneralError(Exception ex) {
		return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu: " + ex.getMessage());
	}

	// 🔸 JSON cevabı oluşturan yardımcı metot
	private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("timestamp", LocalDateTime.now());
		body.put("status", status.value());
		body.put("error", status.getReasonPhrase());
		// body.put("message", message);

		return new ResponseEntity<>(body, status);
	}
}
