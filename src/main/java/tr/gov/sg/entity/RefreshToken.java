package tr.gov.sg.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Hangi kullanıcıya ait olduğunu bilmek için
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "personel_id", nullable = false)
	private Personel personel;

	// DB'de asla raw token saklamıyoruz, sadece hash'i saklanıyor
	@Column(name = "token_hash", nullable = false, length = 255)
	private String tokenHash;

	// Token'ın ne zamana kadar geçerli olduğu
	@Column(name = "expires_at", nullable = false)
	private Instant expiresAt;

	// Token kullanımdan kaldırıldı mı?
	@Column(nullable = false)
	private boolean revoked = false;

	// Refresh token rotation için family id
	@Column(name = "family_id", nullable = false, length = 100)
	private String familyId;

	// Bu token yenilendiğinde hangi yeni token ile değiştirildi
	@Column(name = "replaced_by_token_id")
	private Long replacedByTokenId;

	// İsteğe bağlı: güvenlik/analiz amaçlı metadata
	@Column(name = "created_by_ip")
	private String createdByIp;
	@Column(name = "user_agent")
	private String userAgent;

}
