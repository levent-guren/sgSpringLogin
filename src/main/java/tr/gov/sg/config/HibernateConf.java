package tr.gov.sg.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import tr.gov.sg.service.SecurityService;

@RequiredArgsConstructor
@Log4j2
@Configuration
public class HibernateConf {
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Autowired
	private SecurityService securityService;

	@Bean
	HikariConfig hikariConfig() {
		return new HikariConfig();
	}

	@Bean
	DataSource getDataSource(HikariConfig config) throws Exception {
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(securityService.getDBPassword());
		System.out.println("user:" + username);
		System.out.println("pass:" + securityService.getDBPassword());
		return new HikariDataSource(config);
	}

}
