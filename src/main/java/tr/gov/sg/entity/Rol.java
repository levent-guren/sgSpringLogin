package tr.gov.sg.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Rol {
	@Id
	private String id;
	private String adi;
	@OneToMany(mappedBy = "rol")
	private List<PersonelRol> personelRol;
}
