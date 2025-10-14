package tr.gov.sg.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

// PemUtils.java
public final class PemUtils {
	public static PrivateKey readPrivateKey(InputStream in) throws Exception {
		String key = new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
		byte[] pkcs8 = Base64.getDecoder().decode(key);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(new PKCS8EncodedKeySpec(pkcs8));
	}

	public static PublicKey readPublicKey(InputStream in) throws Exception {
		String key = new String(in.readAllBytes(), StandardCharsets.UTF_8).replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");
		byte[] x509 = Base64.getDecoder().decode(key);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(new X509EncodedKeySpec(x509));
	}
}
