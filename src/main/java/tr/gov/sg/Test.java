package tr.gov.sg;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Test implements CommandLineRunner {
	// @Autowired
	// private JwtService jwtService;

	@Override
	public void run(String... args) throws Exception {
		// System.out.println("Program çalıştı. Test kodları çalıştırılıyor:");
		// List<GrantedAuthority> roller = new ArrayList<>();
		// roller.add(new SimpleGrantedAuthority("admin"));
		// User user = new User("murteza", "123", roller);
		// String token = jwtService.generateAccessToken(user);
		// System.out.println(token);

		// var claims = jwtService.parse(token);
		// System.out.println(claims.getPayload().getSubject());
	}
}
