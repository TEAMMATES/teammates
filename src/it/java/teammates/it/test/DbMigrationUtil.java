package teammates.it.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;

/**
 * Utility class with methods to apply sql migrations in tests.
 */
public final class DbMigrationUtil {

    private DbMigrationUtil() {
        // prevent instantiation
    }

    /**
     * Drop all tables and re-apply migrations.
     */
    public static void resetDb(String dbUrl, String username, String password) throws Exception {
        Map<String, Object> config = new HashMap<>();
        File file = new File(System.getProperty("user.dir"));

        Scope.child(config, () -> {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            try (Liquibase liquibase = new Liquibase("src/main/resources/db/changelog/db.changelog-root.xml",
                    new DirectoryResourceAccessor(file), database)) {
                liquibase.update();
            }
            conn.close();
        });
    }
}
