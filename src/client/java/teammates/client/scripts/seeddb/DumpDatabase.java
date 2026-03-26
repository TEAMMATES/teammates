package teammates.client.scripts.seeddb;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.sqllogic.core.LogicStarter;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Dumps the current development database state to a JSON databundle file.
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
    private static final String DEFAULT_DUMP_FILE = "build/dumps/Dumped_Databundle_%s.json";

    private DumpDatabase() {
        // Utility class
    }

    public static void main(String[] args) {
        String dumpFile = DEFAULT_DUMP_FILE;

        if (args.length == 0) {
            dumpFile = String.format(DEFAULT_DUMP_FILE, LocalDateTime.now()
                    .truncatedTo(ChronoUnit.SECONDS).toString().replace(":", "-"));
        } else {
            dumpFile = args[1];
            if (dumpFile.startsWith("~/")) {
                dumpFile = System.getProperty("user.home") + dumpFile.substring(1);
            }
        }

        String dbUrl = "jdbc:postgresql://" + Config.POSTGRES_HOST + ":" + Config.POSTGRES_PORT
                + "/" + Config.POSTGRES_DATABASENAME;
        HibernateUtil.buildSessionFactory(dbUrl, Config.POSTGRES_USERNAME, Config.POSTGRES_PASSWORD);
        LogicStarter.initializeDependencies();
        HibernateUtil.beginTransaction();
        boolean dumpOk = false;

        try {
            log.info("Querying database...");
            SqlDataBundle bundle = buildBundle();
            log.info("Writing dump to: " + dumpFile);
            teammates.test.FileHelper.saveFile(dumpFile, JsonUtils.toJson(bundle));
            HibernateUtil.commitTransaction();
            dumpOk = true;
            log.info("Dump completed.");
        } catch (IOException e) {
            log.severe("Failed to write dump file '" + dumpFile + "'", e);
        } finally {
            if (!dumpOk) {
                HibernateUtil.rollbackTransaction();
                System.exit(1);
            }
        }
    }

    private static SqlDataBundle buildBundle() {
        SqlDataBundle bundle = new SqlDataBundle();
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
