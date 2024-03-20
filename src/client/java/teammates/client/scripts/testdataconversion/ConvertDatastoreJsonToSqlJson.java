package teammates.client.scripts.testdataconversion;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

public class ConvertDatastoreJsonToSqlJson {
    // Get all entities in JSON file
    // Convert entities into SQL entities
    // Create corresponding links between entities
    // Collate entities and write back to json

    DataStoreToSqlConverter entityConverter;
    DataBundle dataStoreBundle;

    protected ConvertDatastoreJsonToSqlJson() throws IOException {
        this.entityConverter = new DataStoreToSqlConverter();
        File file = new File("./src/client/java/teammates/client/scripts/typicalDataBundle.json");
        this.dataStoreBundle = loadDataBundle(file.getCanonicalPath());
    }

    private void migrate() throws IOException, InvalidParametersException{
        String outputFileName = "output.json";
        File outputFile = new File("./src/client/java/teammates/client/scripts/" + outputFileName);
        createSqlJson(outputFile);
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

    private void createSqlJson(File outputFile) throws IOException, InvalidParametersException {
        SqlDataBundle sqlDataBundle = new SqlDataBundle();

        migrateIndepedentEntities(sqlDataBundle);

        // TODO how to remove course information except ID
        // Feedback ses (course)
        // section (course)
        // team (section)
        migrateDependentEntities(sqlDataBundle);
    

        String sqlJsonString = JsonUtils.toJson(sqlDataBundle);
        saveFile(outputFile.getCanonicalPath(), sqlJsonString + System.lineSeparator());
    }

    private void migrateIndepedentEntities(SqlDataBundle sqlDataBundle) {
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

    private void migrateDependentEntities(SqlDataBundle sqlDataBundle) {
        // ['feedback_response_comment', 
        // 'feedback_response', 'readNotification', , 
        // 'feednackxxxresponse', 'feedbackxxxquestion', 'feedback_question', 
        // 'deadline_extension', 
        
        // 'instructor', 'student', 
        // 'team', 'section', 'feedback_session ]

        dataStoreBundle.feedbackSessions.forEach((k, feedbackSession) -> {
            FeedbackSession sqlFeedbackSession = entityConverter.convert(feedbackSession);
            sqlDataBundle.feedbackSessions.put(k, sqlFeedbackSession);
        });

        dataStoreBundle.students.forEach((k, student) -> {
            // Assumes that section name is unique in JSON file
            String jsonKey = removeWhitespace(student.getSection());

            if (!sqlDataBundle.sections.containsKey(jsonKey)) {
                Section sqlSection = entityConverter.createSection(student);
                sqlDataBundle.sections.put(jsonKey, sqlSection);
            }
        });

        dataStoreBundle.students.forEach((k, student) -> {
            // Assumes that team name is unique in JSON file
            String jsonKey = removeWhitespace(student.getTeam());

            if (!sqlDataBundle.teams.containsKey(jsonKey)) {
                Team sqlTeam = entityConverter.createTeam(student);
                sqlDataBundle.teams.put(jsonKey, sqlTeam);
            }
        });

        dataStoreBundle.instructors.forEach((k, instructor) -> {
            // Sets instructor key as "courseid-instructorName"
            String jsonKey = removeWhitespace(instructor.getCourseId() + "-" + instructor.getName());
            
            if (!sqlDataBundle.instructors.containsKey(jsonKey)) {
                Instructor sqlInstructor = entityConverter.convert(instructor);
                sqlDataBundle.instructors.put(jsonKey, sqlInstructor);
            }
        });

        dataStoreBundle.students.forEach((k, student) -> {
            // Sets instructor key as "courseid-instructorName"
            String jsonKey = removeWhitespace(student.getCourse() + "-" + student.getName());
            
            if (!sqlDataBundle.students.containsKey(jsonKey)) {
                Student sqlStudent = entityConverter.convert(student);
                sqlDataBundle.students.put(jsonKey, sqlStudent);
            }
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
            sqlReadNotifications.forEach((notif) -> {
                String jsonKey = removeWhitespace(notif.getNotification().getTitle() + "-" + account.getName());
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

        // dataStoreBundle.accounts.forEach((k, account) -> {
        //     List<ReadNotification> sqlReadNotifications = entityConverter.createReadNotifications(account);
        // });

        // dataStoreBundle.feedbackSessions.forEach((k, feedbackSession) -> {
        //     FeedbackSession sqlFeedbackSession = entityConverter.convert(feedbackSession);
        //     sqlDataBundle.feedbackSessions.put(k, sqlFeedbackSession);
        // });
    }



    public static void main(String[] args) throws IOException, InvalidParametersException {
        // Topo sort, last element created first
        // ['feedback_response_comment', 
        // 'feedback_response', 'readNotification', , 
        // 'feednackxxxresponse', 'feedbackxxxquestion', 'feedback_question', 
        // 'deadline_extension', 'instructor', 'student', 
        // user', 'team', 'section', 'feedback_session ]


        // Independent entities
        // acc req
        // usage stats
        // course
        // account
        // notification

        // Dependencies
        // deadline extensions -> user
        // deadline extension -> feedback-session
        // feedback question -> feedback-session
        // feedback response comments -> feedback responses
        // feedback response comments -> sections responses
        // feedback response -> sections
        // feedback response -> feedback_questions
        // feedback session -> courses
        // instructors -> users
        // sections -> courses
        // students -> users
        // teams -> sections
        // users -> courses
        // users -> teams
        // users -> accounts
        // readnotification -> account
        // readNotification -> notification
        
        // Entities
        // Course
        // feedback session
        // User
        // Section
        // Team
        // Student
        // Instructor
        // Deadlineextension
        // Feedback question / Feedbackxxxquestion
        // Account
        // ReadNotification
        // Notification
        // Feedbackresponse / Feednack xxx response
        // feedbackResponseComment
        // Account requests
        // Usage statistics        
        ConvertDatastoreJsonToSqlJson script = new ConvertDatastoreJsonToSqlJson();
        script.migrate();
    }
}
