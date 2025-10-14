package tr.gov.sg.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tr.gov.sg.entity.Personel;

public interface PersonelRepository extends JpaRepository<Personel, String> {
	public Optional<Personel> findByAdi(String adi);
}
