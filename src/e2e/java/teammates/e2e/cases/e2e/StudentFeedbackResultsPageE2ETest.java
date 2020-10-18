package teammates.e2e.cases.e2e;

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
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.AppPage;
import teammates.e2e.pageobjects.StudentFeedbackResultsPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_SESSION_RESULTS_PAGE}.
 */
public class StudentFeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private StudentFeedbackResultsPage resultsPage;
    private FeedbackSessionAttributes openSession;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/StudentFeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        openSession = testData.feedbackSessions.get("Open Session");
    }

    @Test
    public void testAll() {

        ______TS("unregistered student can access results");
        StudentAttributes unregistered = testData.students.get("Unregistered");
        AppUrl url = createUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(unregistered.course)
                .withStudentEmail(unregistered.email)
                .withSessionName(openSession.getFeedbackSessionName())
                .withRegistrationKey(getKeyForStudent(unregistered));
        logout();
        resultsPage = AppPage.getNewPageInstance(browser, url, StudentFeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(testData.feedbackSessions.get("Open Session"));

        ______TS("registered student can access results");
        StudentAttributes student = testData.students.get("Alice");
        url = createUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withUserId(student.googleId)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName());
        resultsPage = loginAdminToPage(url, StudentFeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(testData.feedbackSessions.get("Open Session"));

        ______TS("questions loaded");
        for (int i = 1; i <= 11; i++) {
            resultsPage.verifyQuestionDetails(i, testData.feedbackQuestions.get("qn" + i));
        }

        ______TS("questions with no responses not shown");
        resultsPage.verifyQuestionNotPresent(12);

        ______TS("verify responses");
        // qn11 is a contribution question so we only need to check the statistics for that question
        for (int i = 1; i <= 10; i++) {
            verifyResponseDetails(student, testData.feedbackQuestions.get("qn" + i));
        }

        ______TS("verify statistics - numscale");
        resultsPage.verifyNumScaleStatistics(5, student, getReceivedResponses(student,
                testData.feedbackQuestions.get("qn5")));

        ______TS("verify statistics - rubric");
        FeedbackQuestionAttributes rubricsQn = testData.feedbackQuestions.get("qn10");
        resultsPage.verifyRubricStatistics(10, rubricsQn, getReceivedResponses(student, rubricsQn),
                getAllResponses(student, rubricsQn), getVisibleRecipients(student, rubricsQn), student,
                testData.students.values());

        ______TS("verify statistics - contribution");
        int[] expectedOwnStatistics = { 20, 50, -50 };
        int[] expectedTeamStatistics = { 71, -20, -31 };
        resultsPage.verifyContributionStatistics(11, expectedOwnStatistics, expectedTeamStatistics);

        ______TS("verify comments");
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment1"), student);
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment2"), student);
        verifyCommentDetails(3, testData.feedbackResponseComments.get("qn3Comment1"), student);
        verifyCommentDetails(3, testData.feedbackResponseComments.get("qn3Comment2"), student);
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

    private List<FeedbackResponseAttributes> getReceivedResponses(StudentAttributes currentStudent,
                                                                  FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> receivedResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getRecipient().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentStudent, receivedResponses);
    }

    private List<FeedbackResponseAttributes> getAllResponses(StudentAttributes currentStudent,
                                                             FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> allResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());
        return editIdentifiers(currentStudent, allResponses);
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

    private String getStudentName(String student) {
        return testData.students.values().stream()
               .filter(s -> s.getEmail().equals(student))
               .map(StudentAttributes::getName)
               .findFirst()
               .orElse(null);
    }

    private String getInstructorName(String instructor) {
        return testData.instructors.values().stream()
                .filter(s -> s.getEmail()
                        .equals(instructor))
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
}
