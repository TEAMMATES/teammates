package teammates.client.scripts.testdataconversion;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonObject;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.JsonUtils;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.test.FileHelper;

/**
 * Class to create JSON test data in SQL format from a noSQL JSON file.
 * File can be run using the gradle execScript task and accepts a single argument which is the JSON path
 * ./gradlew execScript -PuserScript="testdataconversion/ConvertDatastoreJsonToSqlJson" --args="JSON_FILE_PATH_HERE"
 */
public class ConvertDatastoreJsonToSqlJson {
    private DataStoreToSqlConverter entityConverter;
    private DataBundle dataStoreBundle;
    private SqlDataBundle sqlDataBundle;

    private String[] entitiesReferencedForeignKeys = new String[] {
            "course",
            "feedbackSession",
            "section",
            "account",
            "giverSection",
            "recipientSection",
            "notification"};

    protected ConvertDatastoreJsonToSqlJson(File inputFile) throws IOException {
        this.entityConverter = new DataStoreToSqlConverter();

        this.dataStoreBundle = loadDataBundle(inputFile.getCanonicalPath());
    }

    private String removeWhitespace(String string) {
        return string.replaceAll("\\s", "");
    }

    private DataBundle loadDataBundle(String pathToJsonFile) throws IOException {
        String jsonString = FileHelper.readFile(pathToJsonFile);
        return JsonUtils.fromJson(jsonString, DataBundle.class);
    }

    private void saveFile(String filePath, String content) throws IOException {
        FileHelper.saveFile(filePath, content);
        System.out.println(filePath + " created!");
    }

    /**
     * Amends foreign key references to only have ID field.
     */
    private void removeForeignKeyData(JsonObject obj) {
        for (String entityName : entitiesReferencedForeignKeys) {
            if (obj.get(entityName) != null) {
                JsonObject entity = obj.get(entityName).getAsJsonObject();
                for (String field : entity.deepCopy().keySet()) {
                    if (!"id".equals(field)) {
                        entity.remove(field);
                    }
                }
            }
        }
    }

    /**
     * Read datstore json file and creates a SQL equivalent.
     */
    private void createSqlJson(File outputFile) throws IOException, InvalidParametersException {
        sqlDataBundle = new SqlDataBundle();

        migrateIndepedentEntities();
        migrateDependentEntities();

        // Iterates through all entities in JSON file and removes foreign entitity data except its ID
        JsonObject sqlJsonString = JsonUtils.toJsonObject(sqlDataBundle);
        for (String entityCollectionName : sqlJsonString.keySet()) {
            JsonObject entityCollection = sqlJsonString.get(entityCollectionName).getAsJsonObject();
            for (String entityName : entityCollection.getAsJsonObject().keySet()) {
                JsonObject entity = entityCollection.get(entityName).getAsJsonObject();
                removeForeignKeyData(entity);
            }
        }

        String jsonString = JsonUtils.toJson(sqlJsonString);
        saveFile(outputFile.getCanonicalPath(), jsonString + System.lineSeparator());
    }

    /**
     * Migrate entities with no foreign key reference.
     * Entities are account requests, usage statistics, courses, accouns, notifications
     */
    private void migrateIndepedentEntities() {
        assert sqlDataBundle != null;

        dataStoreBundle.accounts.forEach((k, datastoreAccount) -> {
            Account sqlAccount = entityConverter.convert(datastoreAccount);
            sqlDataBundle.accounts.put(k, sqlAccount);
        });

        dataStoreBundle.courses.forEach((k, datastoreCourse) -> {
            Course sqlCourse = entityConverter.convert(datastoreCourse);
            sqlDataBundle.courses.put(k, sqlCourse);
        });

        dataStoreBundle.accountRequests.forEach((k, accountRequest) -> {
            AccountRequest sqlAccountRequest = entityConverter.convert(accountRequest);
            sqlDataBundle.accountRequests.put(k, sqlAccountRequest);
        });

        dataStoreBundle.notifications.forEach((k, notification) -> {
            Notification sqlNotification = entityConverter.convert(notification);
            sqlDataBundle.notifications.put(k, sqlNotification);
        });
    }

    /**
     * Migrate entities which have dependence on each other or on the independent entities.
     * The order which the entities were migrated was generated using a topological sort
     * of its foreign key dependencies.
     * Dependent entities: feedback sessions, sections, teams, users, students, instructors,
     * deadline extensions, feedback questions, read notifications,
     * feedback responses and feedback response comments.
     */
    private void migrateDependentEntities() {

        dataStoreBundle.feedbackSessions.forEach((k, feedbackSession) -> {
            FeedbackSession sqlFeedbackSession = entityConverter.convert(feedbackSession);
            sqlDataBundle.feedbackSessions.put(k, sqlFeedbackSession);
        });

        dataStoreBundle.students.forEach((k, student) -> {
            String jsonKey = removeWhitespace(String.format("%s-%s",
                    student.getCourse(), student.getSection()));

            if (!sqlDataBundle.sections.containsKey(jsonKey)) {
                Section sqlSection = entityConverter.createSection(student);
                sqlDataBundle.sections.put(jsonKey, sqlSection);
            }
        });

        dataStoreBundle.students.forEach((k, student) -> {
            String jsonKey = removeWhitespace(String.format("%s-%s-%s",
                    student.getCourse(), student.getSection(), student.getTeam()));

            if (!sqlDataBundle.teams.containsKey(jsonKey)) {
                Team sqlTeam = entityConverter.createTeam(student);
                sqlDataBundle.teams.put(jsonKey, sqlTeam);
            }
        });

        dataStoreBundle.instructors.forEach((k, instructor) -> {
            Instructor sqlInstructor = entityConverter.convert(instructor);
            sqlDataBundle.instructors.put(k, sqlInstructor);
        });

        dataStoreBundle.students.forEach((k, student) -> {
            Student sqlStudent = entityConverter.convert(student);
            sqlDataBundle.students.put(k, sqlStudent);
        });

        dataStoreBundle.deadlineExtensions.forEach((k, deadlineExtension) -> {
            DeadlineExtension sqlDeadline = entityConverter.convert(deadlineExtension);
            sqlDataBundle.deadlineExtensions.put(k, sqlDeadline);
        });

        dataStoreBundle.feedbackQuestions.forEach((k, feedbackQuestion) -> {
            FeedbackQuestion sqlFeedbackQuestion = entityConverter.convert(feedbackQuestion);
            sqlDataBundle.feedbackQuestions.put(k, sqlFeedbackQuestion);
        });

        dataStoreBundle.accounts.forEach((k, account) -> {
            List<ReadNotification> sqlReadNotifications = entityConverter.createReadNotifications(account);
            sqlReadNotifications.forEach(notif -> {
                String jsonKey = removeWhitespace(String.format("%s-%s",
                            notif.getNotification().getTitle(), account.getEmail()));
                sqlDataBundle.readNotifications.put(jsonKey, notif);
            });
        });

        dataStoreBundle.feedbackResponses.forEach((k, feedbackResponse) -> {
            FeedbackResponse sqlFeedbackResponse = entityConverter.convert(feedbackResponse);
            sqlDataBundle.feedbackResponses.put(k, sqlFeedbackResponse);
        });

        dataStoreBundle.feedbackResponseComments.forEach((k, feedbackReponseComment) -> {
            FeedbackResponseComment sqlFeedbackResponseComment = entityConverter.convert(feedbackReponseComment);
            sqlDataBundle.feedbackResponseComments.put(k, sqlFeedbackResponseComment);
        });
    }

    public static void main(String[] args) throws IOException, InvalidParametersException {
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            String fileExtension = FilenameUtils.getExtension(inputFile.getName());
            if (!"json".equals(fileExtension)) {
                throw new InvalidParametersException("The file provided is not a JSON file");
            }

            ConvertDatastoreJsonToSqlJson script = new ConvertDatastoreJsonToSqlJson(inputFile);
            String outputFileName = FilenameUtils.getBaseName(inputFile.getName()) + "Sql.json";
            File outputFile = new File(inputFile.getParent(), outputFileName);
            script.createSqlJson(outputFile);
        } else {
            throw new InvalidParametersException("Required the path of the script to convert");
        }
    }
}
