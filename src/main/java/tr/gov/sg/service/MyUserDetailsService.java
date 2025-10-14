package tr.gov.sg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tr.gov.sg.repository.PersonelRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
	@Autowired
	private PersonelRepository personelRepository;

	@Override
	public UserDetails loadUserByUsername(String adi) throws UsernameNotFoundException {
		var u = personelRepository.findByAdi(adi).orElseThrow(() -> new UsernameNotFoundException(adi));
		var auths = u.getPersonelRols().stream().map(r -> new SimpleGrantedAuthority(r.getRol().getAdi())).toList();
		return new User(u.getAdi(), "", true, true, true, true, auths);
	}
}
