package tr.gov.sg.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
	@NotBlank
	private String adi;
	@NotBlank
	private String sifre;
}
