package tr.gov.sg.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tr.gov.sg.service.JwtService;

// JwtAuthenticationFilter.java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	@SuppressWarnings("unchecked")
	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {
		String header = req.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith("Bearer ")) {
			String token = header.substring(7);
			try {
				var claims = jwtService.parse(token).getPayload();
				String username = claims.getSubject();
				if (username != null) {
					List<String> roller = (List<String>) claims.get("roller");
					var auth = new UsernamePasswordAuthenticationToken(username, null,
							roller.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList());
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			} catch (Exception ex) {
				// invalid/expired -> leave unauthenticated
				throw new BadCredentialsException("Invalid or missing token");
			}
		}
		chain.doFilter(req, res);
	}
}
