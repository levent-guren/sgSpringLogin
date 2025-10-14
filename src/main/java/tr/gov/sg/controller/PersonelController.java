package tr.gov.sg.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tr.gov.sg.dto.PersonelResponseDTO;
import tr.gov.sg.entity.Personel;
import tr.gov.sg.repository.PersonelRepository;
import tr.gov.sg.service.PersonelService;

@RestController
@RequestMapping("/api/v1/personel")
public class PersonelController {

	private final PersonelRepository personelRepository;
	@Autowired
	private PersonelService personelService;

	PersonelController(PersonelRepository personelRepository) {
		this.personelRepository = personelRepository;
	}

	@PostMapping("/list")
	public List<PersonelResponseDTO> getPersoneller() {
		List<Personel> personeller = personelService.getPersoneller();
		return personeller.stream().map(p -> {
			PersonelResponseDTO dto = new PersonelResponseDTO();
			dto.setAdi(p.getAdi());
			return dto;
		}).toList();
	}

	public Optional<Personel> getPersonel(String adi) {
		return personelRepository.findByAdi(adi);
	}
}
