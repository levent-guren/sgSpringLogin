package tr.gov.sg.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import tr.gov.sg.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {
	@Autowired
	private JwtAuthenticationFilter jwtFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.headers(h -> h.frameOptions(f -> f.sameOrigin()))
				.exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					res.setContentType("application/json;charset=UTF-8");
					res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
				}).accessDeniedHandler((req, res, e) -> {
					res.setStatus(HttpServletResponse.SC_FORBIDDEN);
					res.setContentType("application/json;charset=UTF-8");
					res.getWriter().write("{\"error\":\"forbidden\",\"message\":\"" + e.getMessage() + "\"}");
				}))
				// @formatter:off
		.authorizeHttpRequests(
				auth -> auth
				.requestMatchers("/api/v1/auth/**").permitAll()
				.requestMatchers("/api/v1/personel/list").hasRole("admin")
				.requestMatchers("/api/v1/**").authenticated()
				.anyRequest().denyAll()
			)
		// @formatter:on
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		var c = new CorsConfiguration();
		c.addAllowedOriginPattern("http://localhost:4200");
		c.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		c.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
		c.setAllowCredentials(true);
		c.setExposedHeaders(List.of("Set-Cookie"));
		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", c);
		return source;
	}

	@Bean
	PasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder(12);
	}
}
