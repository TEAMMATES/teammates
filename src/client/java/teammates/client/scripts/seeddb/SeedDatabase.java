package teammates.client.scripts.seeddb;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonSyntaxException;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.Logger;
import teammates.common.util.Templates;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
import teammates.sqllogic.api.Logic;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.sqllogic.core.LogicStarter;
import teammates.test.FileHelper;

/**
 * Seeds the development database with mock data.
 *
 * <p>If {@code --seedFile} is not specified, seeds from the default
 * seeding databundle file at {@code src/client/resources/SeedingDatabundle.json}
 *
 * <p>Usage: {@code ./gradlew seedDatabase [--reset] [--noSeed] [--seedFile <path>]}
 *
 * <p>Options:
 * <ul>
 *   <li>{@code --reset} — truncate all tables before seeding</li>
 *   <li>{@code --noSeed} — truncate only, skip seeding</li>
 *   <li>{@code --seedFile <path>} — seed from a custom JSON databundle file</li>
 * </ul>
 */
public final class SeedDatabase {

    private static final Logger log = Logger.getLogger();
    private static final String DEFAULT_SEED_FILE = "src/client/resources/SeedingDatabundle.json";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    private static final Pattern SEED_DATE_TOKEN = Pattern.compile("seed\\.d\\(([+-]?\\d+)\\)");
    private static final String TRUNCATE_SQL =
            "TRUNCATE TABLE account_requests, accounts, courses, deadline_extensions, "
                    + "feedback_questions, feedback_response_comments, feedback_responses, "
                    + "feedback_session_logs, feedback_sessions, instructors, notifications, "
                    + "read_notifications, sections, students, teams, usage_statistics, users "
                    + "RESTART IDENTITY CASCADE";

    private SeedDatabase() {
        // Utility class
    }

    public static void main(String[] args) {
        boolean reset = false;
        boolean noSeed = false;
        String seedFile = DEFAULT_SEED_FILE;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
            case "--reset" -> reset = true;
            case "--noSeed" -> noSeed = true;
            case "--seedFile" -> seedFile = args[i + 1];
            default -> {
                // Unrecognised arguments handled by gradle
            }
            }
        }

        boolean committed = true;
        boolean isUsingDefaultSeedFile = DEFAULT_SEED_FILE.equals(seedFile);

        try {
            HibernateUtil.buildSessionFactory(
                    Config.getDbConnectionUrl(), Config.POSTGRES_USERNAME, Config.POSTGRES_PASSWORD);
            LogicStarter.initializeDependencies();
            HibernateUtil.beginTransaction();
            committed = false;

            if (reset) {
                log.info("Truncating all tables...");
                HibernateUtil.createNativeMutationQuery(TRUNCATE_SQL).executeUpdate();
                log.info("Truncate completed.");
            }

            if (noSeed) {
                HibernateUtil.commitTransaction();
                committed = true;
                return;
            }

            log.info("Seeding from databundle file: " + seedFile);
            String jsonString = FileHelper.readFile(seedFile);
            if (isUsingDefaultSeedFile) {
                jsonString = applyDateTokens(jsonString);
            }
            DataBundle bundle = DataBundleLogic.deserializeDataBundle(jsonString);

            // All read notifications have been put inside the account and will be persisted when account
            // is persisted. We need to empty the read noitfications list to prevent duplicate insertions.
            bundle.readNotifications.clear();
            Logic.inst().persistDataBundle(bundle);

            if (isUsingDefaultSeedFile) {
                log.info("Seeding additional demo courses for instructors...");
                seedDemoCourses(Logic.inst(), bundle);
            }
            log.info("Seeding completed.");

            HibernateUtil.commitTransaction();
            committed = true;
        } catch (IOException e) {
            log.severe("Failed to read seed file '" + seedFile + "'", e);
        } catch (JsonSyntaxException e) {
            log.severe("Invalid JSON in seed file", e);
        } catch (InvalidParametersException e) {
            log.severe("Invalid entity data", e);
        } catch (EntityAlreadyExistsException e) {
            log.severe("Entity already exists", e);
        } catch (EntityDoesNotExistException e) {
            log.severe("Seed file references an entity that does not exist", e);
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        } finally {
            if (!committed) {
                HibernateUtil.rollbackTransaction();
                System.exit(1);
            }
        }
    }

    private static void seedDemoCourses(Logic logic, DataBundle seedBundle)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Instant now = Instant.now();
        String d1 = DATE_FMT.format(now.minus(7, ChronoUnit.DAYS));
        String d2 = DATE_FMT.format(now.minus(3, ChronoUnit.DAYS));
        String d3 = DATE_FMT.format(now.minus(2, ChronoUnit.DAYS));
        String d4 = DATE_FMT.format(now.plus(3, ChronoUnit.DAYS));
        String d5 = DATE_FMT.format(now);

        // Index courses by their ID so we can look up institute from an instructor's course
        Map<String, Course> courseById = new LinkedHashMap<>();
        for (Course course : seedBundle.courses.values()) {
            courseById.put(course.getId(), course);
        }

        // Deduplicate instructors by email
        Map<String, Instructor> uniqueByEmailInstructors = new LinkedHashMap<>();
        for (Instructor inst : seedBundle.instructors.values()) {
            uniqueByEmailInstructors.putIfAbsent(inst.getEmail(), inst);
        }

        for (Instructor inst : uniqueByEmailInstructors.values()) {
            Course instCourse = courseById.get(inst.getCourseId());
            String institute = instCourse != null ? instCourse.getInstitute() : "";
            String courseId = demoCourseId(inst.getEmail());
            String json = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                    "teammates.demo.instructor.student@demo.course", inst.getEmail().replace("@", "+student@"),
                    "teammates.demo.instructor@demo.course", inst.getEmail(),
                    "Demo_Instructor", inst.getName(),
                    "demo.course", courseId,
                    "demo.institute", institute,
                    "demo.timezone", "UTC",
                    "demo.date1", d1, "demo.date2", d2, "demo.date3", d3,
                    "demo.date4", d4, "demo.date5", d5);

            DataBundle demoBundle = DataBundleLogic.deserializeDataBundle(json);
            logic.persistDataBundle(demoBundle);

            List<Instructor> instructors = logic.getInstructorsByCourse(courseId);
            if (!instructors.isEmpty()) {
                logic.joinCourseForInstructor(instructors.get(0).getRegKey(), inst.getEmail());
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

}
