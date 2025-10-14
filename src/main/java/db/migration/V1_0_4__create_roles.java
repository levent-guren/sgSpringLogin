package db.migration;

import java.sql.PreparedStatement;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V1_0_4__create_roles extends BaseJavaMigration {
	@Override
	public void migrate(Context context) throws Exception {

		try (PreparedStatement rol = context.getConnection().prepareStatement("insert into rol values(?,?)")) {
			rol.setString(1, UUID.randomUUID().toString());
			rol.setString(2, "admin");
			rol.execute();
		}
		try (PreparedStatement rol = context.getConnection().prepareStatement("insert into rol values(?,?)")) {
			rol.setString(1, UUID.randomUUID().toString());
			rol.setString(2, "user");
			rol.execute();
		}

	}

}
