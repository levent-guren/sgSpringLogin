package tr.gov.sg.entity;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Personel {
	@Id
	private String id;
	private String adi;
	private String sifre;
	@OneToMany(mappedBy = "personel")
	private List<PersonelRol> personelRols;
}
