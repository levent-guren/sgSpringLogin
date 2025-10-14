package tr.gov.sg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class PersonelRolId {
	@Column(name = "personel_id")
	private String personelId;
	@Column(name = "rol_id")
	private String rolId;
}
