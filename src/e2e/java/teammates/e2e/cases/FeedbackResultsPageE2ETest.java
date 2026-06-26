package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.SessionKeyType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.KeyUtil;
import teammates.e2e.pageobjects.FeedbackResultsPage;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Student;
import teammates.test.ResponseEntityHelper;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_RESULTS_PAGE}.
 */
public class FeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private FeedbackResultsPage resultsPage;
    private Course course;
    private FeedbackSession openSession;
    private List<FeedbackQuestion> questions = new ArrayList<>();

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(loadDataBundle("/FeedbackResultsPageE2ETest.json"));

        course = testData.courses.get("FRes.CS2104");
        openSession = testData.feedbackSessions.get("Open Session");
        for (int i = 1; i <= testData.feedbackQuestions.size(); i++) {
            questions.add(testData.feedbackQuestions.get("qn" + i));
        }
    }

    @Test
    @Override
    public void testAll() {

        ______TS("unregistered student: can access results");
        Student unregistered = testData.students.get("Unregistered");
        AppUrl url = getStudentResultsPageUrl(openSession, unregistered);
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("unregistered student: questions with responses loaded");
        verifyLoadedQuestions(unregistered, false);

        ______TS("registered student: can access results");
        Student student = testData.students.get("Alice");
        url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(openSession.getId());
        resultsPage = loginToPage(url, FeedbackResultsPage.class, student.getEmail());

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("registered student: questions with responses loaded");
        verifyLoadedQuestions(student, false);

        ______TS("verify responses");
        questions.forEach(question -> verifyResponseDetails(student, question));

        ______TS("verify statistics - numscale");
        String[] expectedNumScaleStats = { student.getTeamName(), "You", "3.83", "4.5", "3", "3.5" };

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
        verifyCommentDetails(2, testData.responseInstructorComments.get("qn2Comment1"));
        verifyCommentDetails(2, testData.responseInstructorComments.get("qn2Comment2"));
        verifyParticipantCommentDetails(3, testData.feedbackResponses.get("qn3response1").getGiverComment());
        verifyParticipantCommentDetails(4, testData.feedbackResponses.get("qn4response1").getGiverComment());

        ______TS("registered instructor: can access results");
        logout();
        Instructor instructor = testData.instructors.get("FRes.instr");
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(openSession.getId());
        resultsPage = loginToPage(url, FeedbackResultsPage.class, instructor.getEmail());

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("registered instructor: questions with responses loaded");
        verifyLoadedQuestions(instructor, false);

        ______TS("verify responses");
        questions.forEach(question -> verifyResponseDetails(instructor, question));

        ______TS("preview results as student: can access results");
        url = createFrontendUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(openSession.getId())
                .withPreviewAs(student.getId().toString());
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("preview results as student: questions with responses loaded and invisible responses excluded");
        verifyLoadedQuestions(student, true);

        ______TS("preview results as student: visible responses shown");
        questions.stream().filter(this::canInstructorSeeQuestion)
                .forEach(question -> verifyResponseDetails(student, question));

        ______TS("preview results as student: visible comments shown");
        verifyCommentDetails(2, testData.responseInstructorComments.get("qn2Comment1"));
        verifyCommentDetails(2, testData.responseInstructorComments.get("qn2Comment2"));
        verifyParticipantCommentDetails(3, testData.feedbackResponses.get("qn3response1").getGiverComment());
        verifyParticipantCommentDetails(4, testData.feedbackResponses.get("qn4response1").getGiverComment());

        ______TS("preview results as instructor: can access results");
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(openSession.getId())
                .withPreviewAs(instructor.getId().toString());
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);
    }

    private AppUrl getStudentResultsPageUrl(FeedbackSession session, Student student) {
        return createFrontendUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withFeedbackSessionId(session.getId())
                .withKey(KeyUtil.encryptSessionKey(student.getId(), SessionKeyType.RESULTS,
                        student.getRegKey(), session.getId()));
    }

    private void verifyLoadedQuestions(Student currentStudent, boolean isPreview) {
        Set<FeedbackQuestion> qnsWithResponse = getQnsWithResponses(currentStudent);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            }
        });

        if (isPreview) {
            Set<FeedbackQuestion> qnsWithResponseNotVisibleForPreview = qnsWithResponse.stream()
                    .filter(qn -> !canInstructorSeeQuestion(qn))
                    .collect(Collectors.toSet());
            qnsWithResponseNotVisibleForPreview
                    .forEach(qn -> resultsPage.verifyQuestionHasResponsesNotVisibleForPreview(
                            qn.getQuestionNumber()));
        }
    }

    private void verifyLoadedQuestions(Instructor currentInstructor, boolean isPreview) {
        Set<FeedbackQuestion> qnsWithResponse = getQnsWithResponses(currentInstructor);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            }
        });

        if (isPreview) {
            Set<FeedbackQuestion> qnsWithResponseNotVisibleForPreview = qnsWithResponse.stream()
                    .filter(qn -> !canInstructorSeeQuestion(qn))
                    .collect(Collectors.toSet());
            qnsWithResponseNotVisibleForPreview
                    .forEach(qn -> resultsPage.verifyQuestionHasResponsesNotVisibleForPreview(
                            qn.getQuestionNumber()));
        }
    }

    private void verifyResponseDetails(Student currentStudent, FeedbackQuestion question) {
        List<FeedbackResultsPage.ExpectedFeedbackResponse> givenResponses =
                getGivenResponses(currentStudent, question);
        List<FeedbackResultsPage.ExpectedFeedbackResponse> otherResponses =
                getOtherResponses(currentStudent, question);
        Set<String> visibleGivers = getVisibleGivers(currentStudent, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentStudent, question);
        resultsPage.verifyResponseDetails(question, givenResponses, otherResponses, visibleGivers,
                visibleRecipients);
    }

    private void verifyResponseDetails(Instructor currentInstructor, FeedbackQuestion question) {
        List<FeedbackResultsPage.ExpectedFeedbackResponse> givenResponses =
                getGivenResponses(currentInstructor, question);
        List<FeedbackResultsPage.ExpectedFeedbackResponse> otherResponses =
                getOtherResponses(currentInstructor, question);
        Set<String> visibleGivers = getVisibleGivers(currentInstructor, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentInstructor, question);
        resultsPage.verifyResponseDetails(question, givenResponses, otherResponses, visibleGivers,
                visibleRecipients);
    }

    private void verifyCommentDetails(int questionNum, ResponseInstructorComment comment) {
        String giver = comment.getGiver().getDisplayName();
        resultsPage.verifyCommentDetails(questionNum, giver, comment.getCommentText());
    }

    private void verifyParticipantCommentDetails(int questionNum, String commentText) {
        resultsPage.verifyCommentDetails(questionNum, "", commentText);
    }

    private boolean canInstructorSeeQuestion(FeedbackQuestion feedbackQuestion) {
        boolean isGiverVisibleToInstructor = feedbackQuestion.getShowGiverNameTo()
                .contains(FeedbackVisibilityType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor = feedbackQuestion.getShowRecipientNameTo()
                .contains(FeedbackVisibilityType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor = feedbackQuestion.getShowResponsesTo()
                .contains(FeedbackVisibilityType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    private Set<FeedbackQuestion> getQnsWithResponses(Student currentStudent) {
        return questions.stream()
                .filter(qn -> !getGivenResponses(currentStudent, qn).isEmpty()
                        || !getOtherResponses(currentStudent, qn).isEmpty())
                .collect(Collectors.toSet());
    }

    private Set<FeedbackQuestion> getQnsWithResponses(Instructor currentInstructor) {
        return questions.stream()
                .filter(qn -> !getGivenResponses(currentInstructor, qn).isEmpty()
                        || !getOtherResponses(currentInstructor, qn).isEmpty())
                .collect(Collectors.toSet());
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> getGivenResponses(Student currentStudent,
            FeedbackQuestion question) {
        List<FeedbackResponse> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestion().equals(question)
                        && f.getGiver().isGiverUser()
                        && currentStudent.getEmail().equals(f.getGiver().getGiverUser().getEmail()))
                .toList();
        return toExpectedResponses(currentStudent, givenResponses);
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> getGivenResponses(Instructor currentInstructor,
            FeedbackQuestion question) {
        List<FeedbackResponse> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestion().equals(question)
                        && f.getGiver().isGiverUser()
                        && currentInstructor.getEmail().equals(f.getGiver().getGiverUser().getEmail()))
                .toList();
        return toExpectedResponses(currentInstructor, givenResponses);
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> getOtherResponses(Student currentStudent,
            FeedbackQuestion question) {
        Set<String> visibleResponseGivers = getRelevantUsers(currentStudent, question.getShowResponsesTo());
        visibleResponseGivers.add(currentStudent.getEmail());

        List<FeedbackResponse> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestion().equals(question))
                .toList();

        List<FeedbackResponse> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> currentStudent.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                        && currentStudent.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getRecipient())))
                .toList();

        List<FeedbackResponse> responsesByOthers = questionResponses.stream()
                .filter(fr -> !currentStudent.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                        && visibleResponseGivers.contains(ResponseEntityHelper.getIdentifier(fr.getGiver())))
                .toList();

        List<FeedbackResponse> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIPIENT")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !currentStudent.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                            && currentStudent.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getRecipient())))
                    .toList();
        }

        List<FeedbackResponse> otherResponses = new ArrayList<>();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return toExpectedResponses(currentStudent, otherResponses);
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> getOtherResponses(Instructor currentInstructor,
            FeedbackQuestion question) {
        Set<String> visibleResponseGivers = getRelevantUsersForInstructors(question.getShowResponsesTo());
        visibleResponseGivers.add(currentInstructor.getEmail());

        List<FeedbackResponse> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestion().equals(question))
                .toList();

        List<FeedbackResponse> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> currentInstructor.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                        && currentInstructor.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getRecipient())))
                .toList();

        List<FeedbackResponse> responsesByOthers = questionResponses.stream()
                .filter(fr -> !currentInstructor.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                        && visibleResponseGivers.contains(ResponseEntityHelper.getIdentifier(fr.getGiver())))
                .toList();

        List<FeedbackResponse> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER") || visibleResponseGivers.contains("INSTRUCTORS")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !currentInstructor.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getGiver()))
                            && currentInstructor.getEmail().equals(ResponseEntityHelper.getIdentifier(fr.getRecipient())))
                    .toList();
        }

        List<FeedbackResponse> otherResponses = new ArrayList<>();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return toExpectedResponses(currentInstructor, otherResponses);
    }

    private Set<String> getVisibleGivers(Student currentStudent, FeedbackQuestion question) {
        return getRelevantUsers(currentStudent, question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleGivers(Instructor currentInstructor, FeedbackQuestion question) {
        return getRelevantUsersForInstructors(question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(Student currentStudent, FeedbackQuestion question) {
        return getRelevantUsers(currentStudent, question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(Instructor currentInstructor,
            FeedbackQuestion question) {
        return getRelevantUsersForInstructors(question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getRelevantUsers(Student giver, List<FeedbackVisibilityType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        List<Student> students = new ArrayList<>();
        if (relevantParticipants.contains(FeedbackVisibilityType.STUDENTS)) {
            students.addAll(getOtherStudents(giver));
        } else if (relevantParticipants.contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
            students.addAll(getOtherTeammates(giver));
        }
        students.forEach(s -> relevantUsers.add(s.getEmail()));
        students.forEach(s -> relevantUsers.add(s.getTeamName()));

        if (relevantParticipants.contains(FeedbackVisibilityType.RECIPIENT)) {
            relevantUsers.add("RECEIVER");
        }

        return relevantUsers;
    }

    private Set<String> getRelevantUsersForInstructors(List<FeedbackVisibilityType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        if (relevantParticipants.contains(FeedbackVisibilityType.RECIPIENT)) {
            relevantUsers.add("RECEIVER");
        }
        if (relevantParticipants.contains(FeedbackVisibilityType.INSTRUCTORS)) {
            relevantUsers.add("INSTRUCTORS");
        }
        return relevantUsers;
    }

    private Set<Student> getOtherTeammates(Student currentStudent) {
        return testData.students.values().stream()
                .filter(s -> s.getTeam().equals(currentStudent.getTeam())
                        && !s.equals(currentStudent))
                .collect(Collectors.toSet());
    }

    private Set<Student> getOtherStudents(Student currentStudent) {
        return testData.students.values().stream()
                .filter(s -> s.getCourse().equals(currentStudent.getCourse())
                        && !s.equals(currentStudent))
                .collect(Collectors.toSet());
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> toExpectedResponses(Student currentStudent,
                    List<FeedbackResponse> responses) {
        return responses.stream()
                        .map(response -> new FeedbackResultsPage.ExpectedFeedbackResponse(response,
                                        getIdentifier(currentStudent, response.getGiver()),
                                        getIdentifier(currentStudent, response.getRecipient())))
                        .toList();
    }

    private List<FeedbackResultsPage.ExpectedFeedbackResponse> toExpectedResponses(Instructor currentInstructor,
                    List<FeedbackResponse> responses) {
        return responses.stream()
                        .map(response -> new FeedbackResultsPage.ExpectedFeedbackResponse(response,
                                        getIdentifier(currentInstructor, response.getGiver()),
                                        getIdentifier(currentInstructor, response.getRecipient())))
                        .toList();
    }

    private String getIdentifier(Student currentStudent, ResponseGiver giver) {
        if (giver == null) {
            return "";
        }

        return getIdentifier(currentStudent, ResponseEntityHelper.getIdentifier(giver));
    }

    private String getIdentifier(Student currentStudent, ResponseRecipient recipient) {
        if (recipient == null) {
            return "";
        }

        return getIdentifier(currentStudent, ResponseEntityHelper.getIdentifier(recipient));
    }

    private String getIdentifier(Student currentStudent, String user) {
        if (currentStudent.getEmail().equals(user)) {
            return "You";
        }
        if (Const.GENERAL_QUESTION.equals(user)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (user.equals(currentStudent.getTeamName())) {
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

    private String getIdentifier(Instructor currentInstructor, String user) {
        if (currentInstructor.getEmail().equals(user)) {
            return "You";
        }
        if (Const.GENERAL_QUESTION.equals(user)) {
            return Const.USER_NOBODY_TEXT;
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

    private String getIdentifier(Instructor currentInstructor, ResponseGiver giver) {
        if (giver == null) {
            return "";
        }

        return getIdentifier(currentInstructor, ResponseEntityHelper.getIdentifier(giver));
    }

    private String getIdentifier(Instructor currentInstructor, ResponseRecipient recipient) {
        if (recipient == null) {
            return "";
        }

        return getIdentifier(currentInstructor, ResponseEntityHelper.getIdentifier(recipient));
    }

    private String getStudentName(String studentEmail) {
        return testData.students.values().stream()
                .filter(s -> s.getEmail().equals(studentEmail))
                .map(Student::getName)
                .findFirst()
                .orElse(null);
    }

    private String getInstructorName(String instructorEmail) {
        return testData.instructors.values().stream()
                .filter(s -> s.getEmail()
                        .equals(instructorEmail))
                .map(Instructor::getName)
                .findFirst()
                .orElse(null);
    }

    private void verifyExpectedRubricStats() {
        FeedbackRubricQuestionDetails rubricsQnDetails = (FeedbackRubricQuestionDetails) testData.feedbackQuestions
                .get("qn10").getQuestionDetailsCopy();
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
                },
                {
                        formattedSubQns[1],
                        "0% (0) [0.01]",
                        "0% (0) [0.02]",
                        "33.33% (1) [0.03]",
                        "0% (0) [0.04]",
                        "66.67% (2) [0.05]",
                },
                {
                        formattedSubQns[2],
                        "0% (0) [2]",
                        "0% (0) [1]",
                        "0% (0) [0]",
                        "66.67% (2) [-1]",
                        "33.33% (1) [-2]",
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
                },
                {
                        formattedSubQns[1],
                        "0% (0) [0.01]",
                        "0% (0) [0.02]",
                        "0% (0) [0.03]",
                        "0% (0) [0.04]",
                        "100% (2) [0.05]",
                },
                {
                        formattedSubQns[2],
                        "0% (0) [2]",
                        "0% (0) [1]",
                        "0% (0) [0]",
                        "50% (1) [-1]",
                        "50% (1) [-2]",
                },
        };

        resultsPage.verifyRubricStatistics(10, expectedRubricStats, expectedRubricStatsExcludingSelf);
    }
}
