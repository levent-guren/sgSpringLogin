package db.migration;

import java.sql.PreparedStatement;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class V1_0_3__create_personels extends BaseJavaMigration {
	@Override
	public void migrate(Context context) throws Exception {
		PasswordEncoder encoder = new BCryptPasswordEncoder();

		try (PreparedStatement rol = context.getConnection().prepareStatement("insert into personel values(?,?,?)")) {
			rol.setString(1, UUID.randomUUID().toString());
			rol.setString(2, "ali");
			rol.setString(3, encoder.encode("123"));
			rol.execute();
		}
		try (PreparedStatement rol = context.getConnection().prepareStatement("insert into personel values(?,?,?)")) {
			rol.setString(1, UUID.randomUUID().toString());
			rol.setString(2, "veli");
			rol.setString(3, encoder.encode("123"));
			rol.execute();
		}

	}

}
