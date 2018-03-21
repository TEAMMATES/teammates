package teammates.client.scripts.scalabilitytests;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.kohsuke.randname.RandomNameGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Generates test data for InstructorFeedbackResultsPageScaleTest.
 */
// Inner classes and their fields are only used for JSON object generation.
@SuppressWarnings({"PMD.UnusedPrivateField", "unused"})
public class InstructorFeedbackResultsPageDataGenerator {
    private Map<String, Question> feedbackQuestions = new HashMap<>();
    private Map<String, Student> students = new HashMap<>();
    private Map<String, Instructor> instructors = new HashMap<>();
    private Map<String, Course> courses = new HashMap<>();
    private Map<String, Session> feedbackSessions = new HashMap<>();
    private Map<String, Response> feedbackResponses = new HashMap<>();
    private Map<String, Account> accounts = new HashMap<>();
    private EmptyObject comments = new EmptyObject();
    private EmptyObject feedbackResponseComments = new EmptyObject();
    private EmptyObject profiles = new EmptyObject();

    class EmptyObject {}

    class Account {
        private String email = "CFResultsScT.instr@gmail.tmt";
        private String googleId = "CFResultsScT.instr";
        private String institute = "TEAMMATES Test Institute 1";
        private String isInstructor = "true";
        private String name = "Teammates Test";
    }

    class Course {
        private String id = "CFResultsScT.CS2104";
        private String name = "Programming Language Concepts";
        private String timeZone = "UTC";
    }

    class Metadata {
        private String value;

        Metadata(String value) {
            this.value = value;
        }
    }

    class Question {
        private String courseId = "CFResultsScT.CS2104";
        private String creatorEmail = "CFResultsScT.instr@gmail.tmt";
        private String feedbackSessionName = "Open Session";
        private String giverType = "STUDENTS";
        private int numberOfEntitiesToGiveFeedbackTo = 4;
        private Metadata questionMetaData = new Metadata("Rate other students");
        private int questionNumber;
        private String questionType = "TEXT";
        private String recipientType = "STUDENTS";
        private String[] showGiverNameTo = new String[] {"INSTRUCTORS", "OWN_TEAM_MEMBERS"};
        private String[] showRecipientNameTo = new String[] {"INSTRUCTORS", "RECEIVER"};
        private String[] showResponsesTo = new String[] {"INSTRUCTORS", "RECEIVER", "STUDENTS", "OWN_TEAM_MEMBERS"};

        Question(int num) {
            this.questionNumber = num;
        }
    }

    class Session {
        private String courseId = "CFResultsScT.CS2104";
        private String createdTime = "2012-04-01 11:59 PM UTC";
        private String creatorEmail = "CFResultsScT.instr@gmail.tmt";
        private String endTime = "2026-04-30 11:59 PM UTC";
        private String feedbackSessionName = "Open Session";
        private int gracePeriod = 10;
        private Metadata instructions = new Metadata("Instructions for Open session");
        private String isClosingEmailEnabled = "true";
        private String isOpeningEmailEnabled = "true";
        private String isPublishedEmailEnabled = "true";
        private String resultsVisibleFromTime = "2012-05-01 11:59 PM UTC";
        private String sentClosedEmail = "false";
        private String sentClosingEmail = "false";
        private String sentOpenEmail = "true";
        private String sentPublishedEmail = "true";
        private String sessionVisibleFromTime = "2012-04-01 11:59 PM UTC";
        private String startTime = "2012-04-01 11:59 PM UTC";
        private double timeZone = 8.0;
    }

    class Instructor {
        private String googleId = "CFResultsScT.instr";
        private String courseId = "CFResultsScT.CS2104";
        private String name = "Teammates Test";
        private String email = "CFResultsScT.instr@gmail.tmt";
        private String role = "Co-owner";
        private String isDisplayedToStudents = "false";
        private String displayedName = "Instructor";

        private Privileges privileges = new Privileges();
        private EmptyObject sectionLevel = new EmptyObject();
        private EmptyObject sessionLevel = new EmptyObject();

        class Privileges {
            private CourseLevel courseLevel = new CourseLevel();
        }

        class CourseLevel {
            private String canviewstudentinsection = "true";
            private String cansubmitsessioninsection = "true";
            private String canmodifysessioncommentinsection = "true";
            private String canmodifycourse = "true";
            private String canviewsessioninsection = "true";
            private String canmodifysession = "true";
            private String canmodifystudent = "true";
            private String canmodifyinstructor = "true";
        }
    }

    class Student {
        private String googleId = "CFResultsScT.";
        private String comments = "This is a student.";
        private String course = "CFResultsScT.CS2104";
        private String email = "CFResultsScT.";
        private String name;
        private String section = "Section A";
        private String team = "Team 1";

        Student(String name) {
            this.name = name;
            this.googleId += name;
            this.email += name + "@gmail.tmt";
        }

        public String getEmail() {
            return email;
        }
    }

    class Response {
        private String giver;
        private String recipient;
        private String feedbackQuestionId;
        private String courseId = "CFResultsScT.CS2104";
        private String feedbackQuestionType = "TEXT";
        private String feedbackSessionName = "Open Session";
        private String giverSection = "Section A";
        private String recipientSection = "Section A";
        private Metadata responseMetaData = new Metadata("Response.");

        Response(String giver, String recipient, String feedbackQuestionId) {
            this.giver = giver;
            this.recipient = recipient;
            this.feedbackQuestionId = feedbackQuestionId;
        }
    }

    private InstructorFeedbackResultsPageDataGenerator(int numQuestions, int numStudents) {
        courses.put("CFResultsScT.CS2104", new Course());
        accounts.put("CFResultsScT.instr", new Account());
        feedbackSessions.put("Open Session", new Session());
        instructors.put("CFResultsScT.instr", new Instructor());

        RandomNameGenerator nameGenerator = new RandomNameGenerator();
        for (int i = 0; i < numStudents; i++) {
            String name = nameGenerator.next();
            students.put(name.replace("_", " "), new Student(name.replace("_", ".")));
        }

        int count = 0;
        for (int i = 0; i < numQuestions; i++) {
            feedbackQuestions.put("question" + i, new Question(i));
            for (Student giver : students.values()) {
                for (Student recipient : students.values()) {
                    feedbackResponses.put(
                            "response" + count,
                            new Response(giver.getEmail(), recipient.getEmail(), String.valueOf(i)));
                    count++;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        //Number of students and questions for each data set.
        int[] studentNums = {10, 20};
        int[] questionNums = {1, 5, 10};
        String folderPath = "src/client/java/teammates/client/scripts/scalabilitytests/data/";
        new File(folderPath).mkdir();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (int studentNum : studentNums) {
            for (int questionNum : questionNums) {
                try (Writer writer = new FileWriter(
                        folderPath
                        + "InstructorFeedbackResultsPageScaleTest-" + studentNum
                        + "Students" + questionNum + "Questions.json")) {
                    gson.toJson(new InstructorFeedbackResultsPageDataGenerator(questionNum, studentNum), writer);
                }
            }
        }
    }
}
