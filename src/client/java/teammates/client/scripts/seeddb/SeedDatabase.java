package teammates.client.scripts.seeddb;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.sqllogic.api.Logic;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.sqllogic.core.LogicStarter;

/**
 * Seeds the development database with mock data.
 *
 * <p>Invoked via: {@code ./gradlew seedDatabase}
 *
 * <p>Options (passed as Gradle project properties):
 * <ul>
 *   <li>{@code -Preset} — truncate all tables before seeding</li>
 *   <li>{@code -PnoSeed} — truncate only, skip seeding (requires {@code -Preset})</li>
 *   <li>{@code -PseedFile=&lt;path&gt;} — seed from a custom JSON file instead of the default</li>
 * </ul>
 */
public final class SeedDatabase {

    private static final Logger log = Logger.getLogger();

    private static final String DEFAULT_SEED_FILE = "src/it/resources/data/typicalDataBundle.json";

    private static final String TRUNCATE_SQL =
            "TRUNCATE TABLE accounts, account_requests, courses, notifications, usage_statistics"
            + " RESTART IDENTITY CASCADE";

    private SeedDatabase() {
        // utility class
    }

    /**
     * Entry point.
     */
    public static void main(String[] args) {
        boolean reset = false;
        boolean noSeed = false;
        String seedFile = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
            case "--reset":
                reset = true;
                break;
            case "--noSeed":
                noSeed = true;
                break;
            case "--seedFile":
                if (i + 1 < args.length) {
                    seedFile = args[++i];
                }
                break;
            default:
                log.severe("Unknown argument: " + args[i]);
                printUsage();
                System.exit(1);
                break;
            }
        }

        if (noSeed && !reset) {
            log.severe("--noSeed requires --reset");
            printUsage();
            System.exit(1);
        }
        if (noSeed && seedFile != null) {
            log.severe("--noSeed and --seedFile are mutually exclusive");
            printUsage();
            System.exit(1);
        }

        String dbUrl = "jdbc:postgresql://" + Config.POSTGRES_HOST + ":" + Config.POSTGRES_PORT
                + "/" + Config.POSTGRES_DATABASENAME;
        HibernateUtil.buildSessionFactory(dbUrl, Config.POSTGRES_USERNAME, Config.POSTGRES_PASSWORD);
        LogicStarter.initializeDependencies();

        boolean committed = false;
        HibernateUtil.beginTransaction();
        try {
            if (reset) {
                log.info("Truncating all tables...");
                HibernateUtil.createNativeMutationQuery(TRUNCATE_SQL).executeUpdate();
                log.info("Truncate complete.");
            }

            if (!noSeed) {
                String jsonString;
                if (seedFile != null) {
                    if (seedFile.startsWith("~/")) {
                        seedFile = System.getProperty("user.home") + seedFile.substring(1);
                    }
                    log.info("Seeding from specified databundle file: " + seedFile);
                    jsonString = teammates.test.FileHelper.readFile(seedFile);
                } else {
                    log.info("Seeding from default databundle file: " + DEFAULT_SEED_FILE);
                    jsonString = teammates.test.FileHelper.readFile(DEFAULT_SEED_FILE);
                }

                SqlDataBundle bundle = DataBundleLogic.deserializeDataBundle(jsonString);
                Logic.inst().persistDataBundle(bundle);
                log.info("Seeding complete.");
            }

            HibernateUtil.commitTransaction();
            committed = true;
        } catch (IOException e) {
            log.severe("Cannot read seed file '" + seedFile + "': " + e.getMessage());
            System.exit(1);
        } catch (JsonSyntaxException e) {
            log.severe("Invalid JSON in seed file: " + e.getMessage());
            System.exit(1);
        } catch (InvalidParametersException e) {
            log.severe("Invalid entity data: " + e.getMessage());
            System.exit(1);
        } catch (EntityAlreadyExistsException e) {
            log.severe("Entity already exists — re-run with -Preset to truncate first");
            System.exit(1);
        } catch (EntityDoesNotExistException e) {
            log.severe("Seed file references an entity that does not exist: " + e.getMessage());
            System.exit(1);
        } finally {
            if (!committed) {
                HibernateUtil.rollbackTransaction();
            }
        }
    }

    private static void printUsage() {
        log.info("Usage: ./gradlew seedDatabase [-Preset] [-PnoSeed] [-PseedFile=<path>]");
    }

}
