package tr.gov.sg.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.Data;

@Entity
@Data
public class PersonelRol {
	@EmbeddedId
	private PersonelRolId id;

	@ManyToOne()
	@MapsId("personelId")
	@JoinColumn(name = "personel_id")
	private Personel personel;

	@ManyToOne()
	@MapsId("rolId")
	@JoinColumn(name = "rol_id")
	private Rol rol;
}
