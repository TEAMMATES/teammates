package teammates.client.scripts.seeddb;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonSyntaxException;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.common.util.Templates;
import teammates.sqllogic.api.Logic;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.sqllogic.core.LogicStarter;
import teammates.storage.sqlentity.Instructor;

/**
 * Seeds the development database with mock data.
 *
 * <p>Invoked via: {@code ./gradlew seedDatabase}
 *
 * <p>Options:
 * <ul>
 *   <li>{@code --reset} — truncate all tables before seeding</li>
 *   <li>{@code --noSeed} — truncate only, skip seeding (requires {@code --reset})</li>
 *   <li>{@code --seedFile &lt;path&gt;} — seed from a custom JSON file; skips demo-course
 *      seeding (only applies when using the default file)</li>
 * </ul>
 */
public final class SeedDatabase {

    private static final Logger log = Logger.getLogger();
    private static final String DEFAULT_SEED_FILE = "src/client/resources/SeedingDatabundle.json";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private static final Pattern SEED_DATE_TOKEN =
            Pattern.compile("seed\\.d\\(([+-]?\\d+)\\)");
    private static final String TRUNCATE_SQL =
            "TRUNCATE TABLE accounts, account_requests, courses, notifications, usage_statistics"
            + " RESTART IDENTITY CASCADE";

    private record DemoInstructor(String email, String name, String institute) {}

    private static final List<DemoInstructor> DEMO_INSTRUCTORS = List.of(
            new DemoInstructor("alice@teammates.tmt",     "Alice",     "Ficuni School of Computing"),
            new DemoInstructor("tanaka@teammates.tmt", "Hiroshi Tanaka", "Ficuni School of Computing"),
            new DemoInstructor("elena@teammates.tmt",  "Elena Vasquez",  "Ficuni School of Computing"),
            new DemoInstructor("marcus.okonkwo@teammates.tmt", "Marcus Okonkwo", "Ficuni School of Computing"),
            new DemoInstructor("priya.sharma@teammates.tmt",   "Priya Sharma",   "Ficuni School of Design and Environment"),
            new DemoInstructor("lars.eriksson@teammates.tmt",  "Lars Eriksson",  "Ficuni School of Computing")
    );

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

                SqlDataBundle bundle = DataBundleLogic.deserializeDataBundle(applyDateTokens(jsonString));
                Logic.inst().persistDataBundle(bundle);
                log.info("Seeding complete.");

                if (seedFile == null) {
                    log.info("Seeding demo courses for instructors...");
                    seedDemoCourses(Logic.inst());
                    log.info("Demo course seeding complete.");
                }
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
            log.severe("Entity already exists — re-run with --reset to truncate first");
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

    private static void seedDemoCourses(Logic logic)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Instant now = Instant.now();
        String d1 = DATE_FMT.format(now.minus(7, ChronoUnit.DAYS));
        String d2 = DATE_FMT.format(now.minus(3, ChronoUnit.DAYS));
        String d3 = DATE_FMT.format(now.minus(2, ChronoUnit.DAYS));
        String d4 = DATE_FMT.format(now.plus(3,  ChronoUnit.DAYS));
        String d5 = DATE_FMT.format(now);

        for (DemoInstructor inst : DEMO_INSTRUCTORS) {
            String courseId = demoCourseId(inst.email());
            String json = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                    "teammates.demo.instructor.student@demo.course", inst.email().replace("@", "+student@"),
                    "teammates.demo.instructor@demo.course",         inst.email(),
                    "Demo_Instructor",                               inst.name(),
                    "demo.course",                                   courseId,
                    "demo.institute",                                inst.institute(),
                    "demo.timezone",                                 "UTC",
                    "demo.date1", d1, "demo.date2", d2, "demo.date3", d3,
                    "demo.date4", d4, "demo.date5", d5);

            // deserializeDataBundle regenerates all placeholder UUIDs — no collision across calls
            SqlDataBundle bundle = DataBundleLogic.deserializeDataBundle(json);
            logic.persistDataBundle(bundle);

            // Link the demo-course instructor entity to the already-persisted account.
            // Mirrors CreateAccountAction.execute() calling joinCourseForInstructor().
            // googleId == email for all accounts in SeedingDatabundle.json.
            List<Instructor> instructors = logic.getInstructorsByCourse(courseId);
            if (!instructors.isEmpty()) {
                logic.joinCourseForInstructor(instructors.get(0).getRegKey(), inst.email());
            }
            log.info("Seeded demo course: " + courseId);
        }
    }

    private static String demoCourseId(String email) {
        int at = email.indexOf('@');
        String user = email.substring(0, at);
        String host = email.substring(at + 1);
        return user + "." + host.substring(0, Math.min(host.length(), 3)) + "-demo";
    }

    private static String applyDateTokens(String jsonString) {
        Instant now = Instant.now();
        Matcher m = SEED_DATE_TOKEN.matcher(jsonString);
        StringBuilder sb = new StringBuilder();
        while (m.find()) {
            int days = Integer.parseInt(m.group(1));
            m.appendReplacement(sb, DATE_FMT.format(now.plus(days, ChronoUnit.DAYS)));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static void printUsage() {
        log.info("Usage: ./gradlew seedDatabase [--reset] [--noSeed] [--seedFile <path>]");
    }

}
