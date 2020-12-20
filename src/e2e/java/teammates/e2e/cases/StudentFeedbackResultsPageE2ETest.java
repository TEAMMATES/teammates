package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.StudentFeedbackResultsPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_SESSION_RESULTS_PAGE}.
 */
public class StudentFeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private StudentFeedbackResultsPage resultsPage;
    private FeedbackSessionAttributes openSession;
    private List<FeedbackQuestionAttributes> questions = new ArrayList<>();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentFeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        openSession = testData.feedbackSessions.get("Open Session");
        for (int i = 1; i <= testData.feedbackQuestions.size(); i++) {
            questions.add(testData.feedbackQuestions.get("qn" + i));
        }
    }

    @Test
    @Override
    public void testAll() {

        ______TS("unregistered student: can access results");
        StudentAttributes unregistered = testData.students.get("Unregistered");
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(unregistered.course)
                .withStudentEmail(unregistered.email)
                .withSessionName(openSession.getFeedbackSessionName())
                .withRegistrationKey(getKeyForStudent(unregistered));
        resultsPage = getNewPageInstance(url, StudentFeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession);

        ______TS("unregistered student: questions with responses loaded");
        verifyLoadedQuestions(unregistered);

        ______TS("registered student: can access results");
        StudentAttributes student = testData.students.get("Alice");
        url = createUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withUserId(student.googleId)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName());
        resultsPage = loginAdminToPage(url, StudentFeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession);

        ______TS("registered student: questions with responses loaded");
        verifyLoadedQuestions(student);

        ______TS("verify responses");
        questions.forEach(question -> verifyResponseDetails(student, question));

        ______TS("verify statistics - numscale");
        String[] expectedNumScaleStats = { student.getTeam(), "You", "3.83", "4.5", "3", "3.5" };

        resultsPage.verifyNumScaleStatistics(5, expectedNumScaleStats);

        ______TS("verify statistics - rubric");
        verifyExpectedRubricStats();

        ______TS("verify statistics - contribution");
        String[] expectedContribStats = {
                "of me: E +20%",
                "of others:  E +50%, E -50%",
                "of me: E +71%",
                "of others:  E -20%, E -31%",
        };

        resultsPage.verifyContributionStatistics(11, expectedContribStats);

        ______TS("verify comments");
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment1"), student);
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment2"), student);
        verifyCommentDetails(3, testData.feedbackResponseComments.get("qn3Comment1"), student);
        verifyCommentDetails(3, testData.feedbackResponseComments.get("qn3Comment2"), student);
    }

    private void verifyLoadedQuestions(StudentAttributes currentStudent) {
        Set<FeedbackQuestionAttributes> qnsWithResponse = getQnsWithResponses(currentStudent);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.questionNumber, qn);
            } else {
                resultsPage.verifyQuestionNotPresent(qn.questionNumber);
            }
        });
    }

    private void verifyResponseDetails(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = getGivenResponses(currentStudent, question);
        List<FeedbackResponseAttributes> otherResponses = getOtherResponses(currentStudent, question);
        Set<String> visibleGivers = getVisibleGivers(currentStudent, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentStudent, question);
        resultsPage.verifyResponseDetails(question, givenResponses, otherResponses, visibleGivers, visibleRecipients);
    }

    private void verifyCommentDetails(int questionNum, FeedbackResponseCommentAttributes comment,
                                      StudentAttributes currentStudent) {
        String editor = "";
        String giver = "";
        if (comment.getLastEditorEmail() != null) {
            editor = getIdentifier(currentStudent, comment.getLastEditorEmail());
        }
        if (!comment.getCommentGiverType().equals(FeedbackParticipantType.STUDENTS)) {
            giver = getIdentifier(currentStudent, comment.getCommentGiver());
        }
        resultsPage.verifyCommentDetails(questionNum, giver, editor, comment.getCommentText());
    }

    private Set<FeedbackQuestionAttributes> getQnsWithResponses(StudentAttributes currentStudent) {
        return questions.stream()
                .filter(qn -> getGivenResponses(currentStudent, qn).size() > 0
                        || getOtherResponses(currentStudent, qn).size() > 0)
                .collect(Collectors.toSet());
    }

    private List<FeedbackResponseAttributes> getGivenResponses(StudentAttributes currentStudent,
                                                               FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getGiver().equals(currentStudent.getEmail())
                        && !f.getRecipient().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentStudent, givenResponses);
    }

    private List<FeedbackResponseAttributes> getOtherResponses(StudentAttributes currentStudent,
                                                               FeedbackQuestionAttributes question) {
        Set<String> visibleResponseGivers = getRelevantUsers(currentStudent, question.getShowResponsesTo());
        visibleResponseGivers.add(currentStudent.getEmail());

        List<FeedbackResponseAttributes> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> fr.getGiver().equals(currentStudent.email) && fr.getRecipient().equals(currentStudent.email))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesByOthers = questionResponses.stream()
                .filter(fr -> !fr.getGiver().equals(currentStudent.email) && visibleResponseGivers.contains(fr.getGiver()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !fr.getGiver().equals(currentStudent.email)
                            && fr.getRecipient().equals(currentStudent.email))
                    .collect(Collectors.toList());
        }

        List<FeedbackResponseAttributes> otherResponses = new ArrayList<>();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return editIdentifiers(currentStudent, otherResponses);
    }

    private Set<String> getVisibleGivers(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        return getRelevantUsers(currentStudent, question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        return getRelevantUsers(currentStudent, question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getRelevantUsers(StudentAttributes giver, List<FeedbackParticipantType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        List<StudentAttributes> students = new ArrayList<>();
        if (relevantParticipants.contains(FeedbackParticipantType.STUDENTS)) {
            students.addAll(getOtherStudents(giver));
        } else if (relevantParticipants.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            students.addAll(getOtherTeammates(giver));
        }
        students.forEach(s -> relevantUsers.add(s.email));
        students.forEach(s -> relevantUsers.add(s.team));

        if (relevantParticipants.contains(FeedbackParticipantType.RECEIVER)) {
            relevantUsers.add("RECEIVER");
        }

        return relevantUsers;
    }

    private Set<StudentAttributes> getOtherTeammates(StudentAttributes currentStudent) {
        return testData.students.values().stream()
                .filter(s -> s.getTeam().equals(currentStudent.getTeam())
                && !s.equals(currentStudent))
                .collect(Collectors.toSet());
    }

    private Set<StudentAttributes> getOtherStudents(StudentAttributes currentStudent) {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(currentStudent.getCourse())
                && !s.equals(currentStudent))
                .collect(Collectors.toSet());
    }

    private List<FeedbackResponseAttributes> editIdentifiers(StudentAttributes currentStudent,
                                                             List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> editedResponses = deepCopyResponses(responses);
        editedResponses.forEach(fr -> {
            fr.giver = getIdentifier(currentStudent, fr.getGiver());
            fr.recipient = getIdentifier(currentStudent, fr.getRecipient());
        });
        return editedResponses;
    }

    private String getIdentifier(StudentAttributes currentStudent, String user) {
        if (currentStudent.getEmail().equals(user)) {
            return "You";
        }
        if (Const.GENERAL_QUESTION.equals(user)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (user.equals(currentStudent.getTeam())) {
            return "Your Team (" + user + ")";
        }
        String identifier = getInstructorName(user);
        if (identifier == null) {
            identifier = getStudentName(user);
        }
        if (identifier == null) {
            identifier = user;
        }
        return identifier;
    }

    private String getStudentName(String studentEmail) {
        return testData.students.values().stream()
               .filter(s -> s.getEmail().equals(studentEmail))
               .map(StudentAttributes::getName)
               .findFirst()
               .orElse(null);
    }

    private String getInstructorName(String instructorEmail) {
        return testData.instructors.values().stream()
                .filter(s -> s.getEmail()
                        .equals(instructorEmail))
                .map(InstructorAttributes::getName)
                .findFirst()
                .orElse(null);
    }

    private List<FeedbackResponseAttributes> deepCopyResponses(List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> copiedResponses = new ArrayList<>();
        for (FeedbackResponseAttributes response : responses) {
            copiedResponses.add(new FeedbackResponseAttributes(response));
        }
        return copiedResponses;
    }

    private void verifyExpectedRubricStats() {
        FeedbackRubricQuestionDetails rubricsQnDetails =
                (FeedbackRubricQuestionDetails) testData.feedbackQuestions.get("qn10").getQuestionDetails();
        List<String> subQns = rubricsQnDetails.getRubricSubQuestions();
        String[] formattedSubQns = { "a) " + subQns.get(0), "b) " + subQns.get(1), "c) " + subQns.get(2) };

        String[][] expectedRubricStats = {
                {
                        formattedSubQns[0],
                        "33.33% (1) [1]",
                        "33.33% (1) [2]",
                        "0% (0) [3]",
                        "0% (0) [4]",
                        "33.33% (1) [5]",
                        "2.67",
                },
                {
                        formattedSubQns[1],
                        "0% (0) [0.01]",
                        "0% (0) [0.02]",
                        "33.33% (1) [0.03]",
                        "0% (0) [0.04]",
                        "66.67% (2) [0.05]",
                        "0.04",
                },
                {
                        formattedSubQns[2],
                        "0% (0) [2]",
                        "0% (0) [1]",
                        "0% (0) [0]",
                        "66.67% (2) [-1]",
                        "33.33% (1) [-2]",
                        "-1.33",
                },
        };

        String[][] expectedRubricStatsExcludingSelf = {
                {
                        formattedSubQns[0],
                        "50% (1) [1]",
                        "0% (0) [2]",
                        "0% (0) [3]",
                        "0% (0) [4]",
                        "50% (1) [5]",
                        "3",
                },
                {
                        formattedSubQns[1],
                        "0% (0) [0.01]",
                        "0% (0) [0.02]",
                        "0% (0) [0.03]",
                        "0% (0) [0.04]",
                        "100% (2) [0.05]",
                        "0.05",
                },
                {
                        formattedSubQns[2],
                        "0% (0) [2]",
                        "0% (0) [1]",
                        "0% (0) [0]",
                        "50% (1) [-1]",
                        "50% (1) [-2]",
                        "-1.5",
                },
        };

        String[] studentNames = { "Anonymous student", "Benny Charles", "Charlie Davis", "You" };
        String[] studentTeams = { "", "Team 1", "Team 1", "Team 1" };

        String[][] expectedRubricStatsPerRecipient = new String[studentNames.length * formattedSubQns.length][3];
        // The actual calculated stats are not verified for this table
        // Checking the recipient presence in the table is sufficient for E2E purposes
        for (int i = 0; i < studentNames.length; i++) {
            for (int j = 0; j < formattedSubQns.length; j++) {
                int index = i * formattedSubQns.length + j;
                expectedRubricStatsPerRecipient[index][0] = studentTeams[i];
                expectedRubricStatsPerRecipient[index][1] = studentNames[i];
                expectedRubricStatsPerRecipient[index][2] = formattedSubQns[j];
            }
        }

        resultsPage.verifyRubricStatistics(10, expectedRubricStats, expectedRubricStatsExcludingSelf,
                expectedRubricStatsPerRecipient);
    }
}
