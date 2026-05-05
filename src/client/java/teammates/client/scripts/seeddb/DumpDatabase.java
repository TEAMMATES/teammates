package teammates.client.scripts.seeddb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.test.FileHelper;

/**
 * Dumps the current development database state to a JSON databundle file.
 *
 * <p>If {@code --dumpFile} is not specified, dumps to the default path at
 * {@code db-dumps/databundle/Dumped_Databundle_<timestamp>.json}
 *
 * <p>Usage: {@code ./gradlew dumpDatabase [--dumpFile <path>]}
 *
 * <p>Options:
 * <ul>
 *   <li>{@code --dumpFile <path>} — output path</li>
 * </ul>
 */
public final class DumpDatabase {

    private static final Logger log = Logger.getLogger();
    private static final String DEFAULT_DUMP_FILE = "db-dumps/databundle/Dumped_Databundle_%s.json";

    private DumpDatabase() {
        // Utility class
    }

    public static void main(String[] args) {
        String dumpFile = String.format(DEFAULT_DUMP_FILE, LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "-"));
        if (args.length == 2) {
            dumpFile = args[1];
        }

        boolean dumpOk = true;

        try {
            HibernateUtil.buildSessionFactory(
                    Config.getDbConnectionUrl(), Config.POSTGRES_USERNAME, Config.POSTGRES_PASSWORD);
            HibernateUtil.beginTransaction();
            dumpOk = false;

            log.info("Querying database...");
            DataBundle bundle = buildBundle();

            log.info("Writing dump to: " + dumpFile);
            Path outputPath = Paths.get(dumpFile);
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            FileHelper.saveFile(dumpFile, JsonUtils.toJson(bundle));
            HibernateUtil.commitTransaction();

            dumpOk = true;
            log.info("Dump completed.");
        } catch (IOException e) {
            log.severe("Failed to write dump file '" + dumpFile + "'", e);
        } catch (Exception e) {
            log.severe("Unexpected error", e);
        } finally {
            if (!dumpOk) {
                HibernateUtil.rollbackTransaction();
                System.exit(1);
            }
        }
    }

    private static DataBundle buildBundle() {
        DataBundle bundle = new DataBundle();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();

        for (Account e : queryAll(cb, Account.class)) {
            bundle.accounts.put(e.getId().toString(), e);
        }
        for (AccountRequest e : queryAll(cb, AccountRequest.class)) {
            bundle.accountRequests.put(e.getId().toString(), e);
        }
        for (Course e : queryAll(cb, Course.class)) {
            bundle.courses.put(e.getId(), e);
        }
        for (Section e : queryAll(cb, Section.class)) {
            bundle.sections.put(e.getId().toString(), e);
        }
        for (Team e : queryAll(cb, Team.class)) {
            bundle.teams.put(e.getId().toString(), e);
        }
        for (DeadlineExtension e : queryAll(cb, DeadlineExtension.class)) {
            bundle.deadlineExtensions.put(e.getId().toString(), e);
        }
        for (Instructor e : queryAll(cb, Instructor.class)) {
            bundle.instructors.put(e.getId().toString(), e);
        }
        for (Student e : queryAll(cb, Student.class)) {
            bundle.students.put(e.getId().toString(), e);
        }
        for (FeedbackSession e : queryAll(cb, FeedbackSession.class)) {
            bundle.feedbackSessions.put(e.getId().toString(), e);
        }
        for (FeedbackQuestion e : queryAll(cb, FeedbackQuestion.class)) {
            bundle.feedbackQuestions.put(e.getId().toString(), e);
        }
        for (FeedbackResponse e : queryAll(cb, FeedbackResponse.class)) {
            bundle.feedbackResponses.put(e.getId().toString(), e);
        }
        for (FeedbackResponseComment e : queryAll(cb, FeedbackResponseComment.class)) {
            bundle.feedbackResponseComments.put(e.getId().toString(), e);
        }
        for (FeedbackSessionLog e : queryAll(cb, FeedbackSessionLog.class)) {
            bundle.feedbackSessionLogs.put(e.getId().toString(), e);
        }
        for (Notification e : queryAll(cb, Notification.class)) {
            bundle.notifications.put(e.getId().toString(), e);
        }
        for (ReadNotification e : queryAll(cb, ReadNotification.class)) {
            bundle.readNotifications.put(e.getId().toString(), e);
        }

        return bundle;
    }

    private static <T> List<T> queryAll(CriteriaBuilder cb, Class<T> entityClass) {
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return HibernateUtil.createQuery(cq).getResultList();
    }

}
