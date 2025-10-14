package tr.gov.sg.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// TokenHasher.java
@Component
public class TokenHasher {
	private static final String HMAC_ALG = "HmacSHA256";
	// opsiyonel "pepper" (env’den oku):
	private final byte[] pepper;

	public TokenHasher(@Value("${security.refresh.pepper:}") String pepperStr) {
		this.pepper = pepperStr == null ? new byte[0] : pepperStr.getBytes(StandardCharsets.UTF_8);
	}

	public String hash(String raw) {
		try {
			if (pepper.length == 0) {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				return toHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
			} else {
				Mac mac = Mac.getInstance(HMAC_ALG);
				mac.init(new SecretKeySpec(pepper, HMAC_ALG));
				return toHex(mac.doFinal(raw.getBytes(StandardCharsets.UTF_8)));
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public boolean matches(String raw, String storedHex) {
		String calc = hash(raw);
		return constantTimeEquals(calc, storedHex);
	}

	private static boolean constantTimeEquals(String a, String b) {
		if (a.length() != b.length())
			return false;
		int res = 0;
		for (int i = 0; i < a.length(); i++)
			res |= a.charAt(i) ^ b.charAt(i);
		return res == 0;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte x : bytes)
			sb.append(String.format("%02x", x));
		return sb.toString();
	}
}
