package tr.gov.sg.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

	@Value("${security.keystore.path}")
	private Resource keystoreResource;
	@Value("${security.keystore.password}")
	private String keystorePassword;

	@Value("${security.keystore.alias}")
	private String keyAlias;

	@Value("${security.keystore.dbpass-alias}")
	private String dbPassAlias;
	private KeyStore ks;

	public String getDBPassword() throws Exception {
		// PKCS12'de anahtar parolasını store parolasıyla aynı tutmayı öneriyorum
		SecretKey sk = (SecretKey) getKeystore().getKey(dbPassAlias, keystorePassword.toCharArray());
		if (sk == null)
			throw new IllegalStateException("Keystore'da alias bulunamadı: " + dbPassAlias);
		return new String(sk.getEncoded(), StandardCharsets.UTF_8);
	}

	private KeyStore getKeystore() throws Exception {
		if (ks == null) {
			ks = KeyStore.getInstance("PKCS12");
			try (InputStream is = keystoreResource.getInputStream()) {
				ks.load(is, keystorePassword.toCharArray());
			}
		}
		return ks;
	}

	public PrivateKey getPrivateKey() throws Exception {
		return (PrivateKey) getKeystore().getKey(keyAlias, keystorePassword.toCharArray());
	}

	public PublicKey getPublicKey() throws Exception {
		Certificate cert = getKeystore().getCertificate(keyAlias);
		return cert.getPublicKey();
	}

}
