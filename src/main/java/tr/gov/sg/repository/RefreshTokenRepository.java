package tr.gov.sg.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tr.gov.sg.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	@Query("select rt from RefreshToken rt where rt.revoked = false and rt.expiresAt > :now")
	List<RefreshToken> findValidByNotExpired(@Param("now") Instant now);

	@Modifying
	@Query("update RefreshToken rt set rt.revoked = true where rt.familyId = :familyId and rt.revoked = false")
	void revokeFamily(@Param("familyId") String familyId);
}
