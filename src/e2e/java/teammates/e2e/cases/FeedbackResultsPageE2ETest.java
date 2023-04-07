package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackResultsPage;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_RESULTS_PAGE}.
 */
public class FeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private FeedbackResultsPage resultsPage;
    private CourseAttributes course;
    private FeedbackSessionAttributes openSession;
    private List<FeedbackQuestionAttributes> questions = new ArrayList<>();

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

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
        StudentAttributes unregistered = testData.students.get("Unregistered");
        AppUrl url = createFrontendUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(unregistered.getCourse())
                .withStudentEmail(unregistered.getEmail())
                .withSessionName(openSession.getFeedbackSessionName())
                .withRegistrationKey(getKeyForStudent(unregistered));
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("unregistered student: questions with responses loaded");
        verifyLoadedQuestions(unregistered, false);

        ______TS("registered student: can access results");
        StudentAttributes student = testData.students.get("Alice");
        url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName());
        resultsPage = loginToPage(url, FeedbackResultsPage.class, student.getGoogleId());

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("registered student: questions with responses loaded");
        verifyLoadedQuestions(student, false);

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
        verifyCommentDetails(4, testData.feedbackResponseComments.get("qn4Comment1"), student);

        ______TS("registered instructor: can access results");
        logout();
        InstructorAttributes instructor = testData.instructors.get("FRes.instr");
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName());
        resultsPage = loginToPage(url, FeedbackResultsPage.class, instructor.getGoogleId());

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("registered instructor: questions with responses loaded");
        verifyLoadedQuestions(instructor, false);

        ______TS("verify responses");
        questions.forEach(question -> verifyResponseDetails(instructor, question));

        ______TS("preview results as student: can access results");
        url = createFrontendUrl(Const.WebPageURIs.SESSION_RESULTS_PAGE)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName())
                .withParam("previewas", student.getEmail());
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("preview results as student: questions with responses loaded and invisible responses excluded");
        verifyLoadedQuestions(student, true);

        ______TS("preview results as student: visible responses shown");
        questions.stream().filter(this::canInstructorSeeQuestion)
                .forEach(question -> verifyResponseDetails(student, question));

        ______TS("preview results as student: invisible comments excluded");
        List<String> commentsNotVisibleForPreview = List.of(
                testData.feedbackResponseComments.get("qn3Comment1").getCommentText());
        resultsPage.verifyQuestionHasCommentsNotVisibleForPreview(3, commentsNotVisibleForPreview);

        ______TS("preview results as student: visible comments shown");
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment1"), student);
        verifyCommentDetails(2, testData.feedbackResponseComments.get("qn2Comment2"), student);
        verifyCommentDetails(3, testData.feedbackResponseComments.get("qn3Comment2"), student);
        verifyCommentDetails(4, testData.feedbackResponseComments.get("qn4Comment1"), student);

        ______TS("preview results as instructor: can access results");
        url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName())
                .withParam("previewas", instructor.getEmail());
        resultsPage = getNewPageInstance(url, FeedbackResultsPage.class);

        resultsPage.verifyFeedbackSessionDetails(openSession, course);

        ______TS("preview results as instructor: questions with responses loaded and invisible responses excluded");
        verifyLoadedQuestions(instructor, true);

        ______TS("preview results as instructor: visible responses shown");
        questions.stream().filter(this::canInstructorSeeQuestion)
                .forEach(question -> verifyResponseDetails(instructor, question));

    }

    private void verifyLoadedQuestions(StudentAttributes currentStudent, boolean isPreview) {
        Set<FeedbackQuestionAttributes> qnsWithResponse = getQnsWithResponses(currentStudent);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            } else {
                resultsPage.verifyQuestionNotPresent(qn.getQuestionNumber());
            }
        });

        if (isPreview) {
            Set<FeedbackQuestionAttributes> qnsWithResponseNotVisibleForPreview = qnsWithResponse.stream()
                    .filter(qn -> !canInstructorSeeQuestion(qn))
                    .collect(Collectors.toSet());
            qnsWithResponseNotVisibleForPreview.forEach(qn ->
                    resultsPage.verifyQuestionHasResponsesNotVisibleForPreview(qn.getQuestionNumber()));
        }
    }

    private void verifyLoadedQuestions(InstructorAttributes currentInstructor, boolean isPreview) {
        Set<FeedbackQuestionAttributes> qnsWithResponse = getQnsWithResponses(currentInstructor);
        questions.forEach(qn -> {
            if (qnsWithResponse.contains(qn)) {
                resultsPage.verifyQuestionDetails(qn.getQuestionNumber(), qn);
            } else {
                resultsPage.verifyQuestionNotPresent(qn.getQuestionNumber());
            }
        });

        if (isPreview) {
            Set<FeedbackQuestionAttributes> qnsWithResponseNotVisibleForPreview = qnsWithResponse.stream()
                    .filter(qn -> !canInstructorSeeQuestion(qn))
                    .collect(Collectors.toSet());
            qnsWithResponseNotVisibleForPreview.forEach(qn ->
                    resultsPage.verifyQuestionHasResponsesNotVisibleForPreview(qn.getQuestionNumber()));
        }
    }

    private void verifyResponseDetails(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = getGivenResponses(currentStudent, question);
        List<FeedbackResponseAttributes> otherResponses = getOtherResponses(currentStudent, question);
        Set<String> visibleGivers = getVisibleGivers(currentStudent, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentStudent, question);
        resultsPage.verifyResponseDetails(question, givenResponses, otherResponses, visibleGivers, visibleRecipients);
    }

    private void verifyResponseDetails(InstructorAttributes currentInstructor, FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = getGivenResponses(currentInstructor, question);
        List<FeedbackResponseAttributes> otherResponses = getOtherResponses(currentInstructor, question);
        Set<String> visibleGivers = getVisibleGivers(currentInstructor, question);
        Set<String> visibleRecipients = getVisibleRecipients(currentInstructor, question);
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

    private boolean canInstructorSeeQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        boolean isGiverVisibleToInstructor =
                feedbackQuestion.getShowGiverNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isRecipientVisibleToInstructor =
                feedbackQuestion.getShowRecipientNameTo().contains(FeedbackParticipantType.INSTRUCTORS);
        boolean isResponseVisibleToInstructor =
                feedbackQuestion.getShowResponsesTo().contains(FeedbackParticipantType.INSTRUCTORS);
        return isResponseVisibleToInstructor && isGiverVisibleToInstructor && isRecipientVisibleToInstructor;
    }

    private Set<FeedbackQuestionAttributes> getQnsWithResponses(StudentAttributes currentStudent) {
        return questions.stream()
                .filter(qn -> !getGivenResponses(currentStudent, qn).isEmpty()
                        || !getOtherResponses(currentStudent, qn).isEmpty())
                .collect(Collectors.toSet());
    }

    private Set<FeedbackQuestionAttributes> getQnsWithResponses(InstructorAttributes currentInstructor) {
        return questions.stream()
                .filter(qn -> !getGivenResponses(currentInstructor, qn).isEmpty()
                        || !getOtherResponses(currentInstructor, qn).isEmpty())
                .collect(Collectors.toSet());
    }

    private List<FeedbackResponseAttributes> getGivenResponses(StudentAttributes currentStudent,
                                                               FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getGiver().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentStudent, givenResponses);
    }

    private List<FeedbackResponseAttributes> getGivenResponses(InstructorAttributes currentInstructor,
                                                               FeedbackQuestionAttributes question) {
        List<FeedbackResponseAttributes> givenResponses = testData.feedbackResponses.values().stream()
                .filter(f -> f.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber()))
                        && f.getGiver().equals(currentInstructor.getEmail()))
                .collect(Collectors.toList());
        return editIdentifiers(currentInstructor, givenResponses);
    }

    private List<FeedbackResponseAttributes> getOtherResponses(StudentAttributes currentStudent,
                                                               FeedbackQuestionAttributes question) {
        Set<String> visibleResponseGivers = getRelevantUsers(currentStudent, question.getShowResponsesTo());
        visibleResponseGivers.add(currentStudent.getEmail());

        List<FeedbackResponseAttributes> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> fr.getGiver().equals(currentStudent.getEmail())
                        && fr.getRecipient().equals(currentStudent.getEmail()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesByOthers = questionResponses.stream()
                .filter(fr -> !fr.getGiver().equals(currentStudent.getEmail())
                        && visibleResponseGivers.contains(fr.getGiver()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !fr.getGiver().equals(currentStudent.getEmail())
                            && fr.getRecipient().equals(currentStudent.getEmail()))
                    .collect(Collectors.toList());
        }

        List<FeedbackResponseAttributes> otherResponses = new ArrayList<>();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return editIdentifiers(currentStudent, otherResponses);
    }

    private List<FeedbackResponseAttributes> getOtherResponses(InstructorAttributes currentInstructor,
                                                               FeedbackQuestionAttributes question) {
        Set<String> visibleResponseGivers = getRelevantUsersForInstructors(question.getShowResponsesTo());
        visibleResponseGivers.add(currentInstructor.getEmail());

        List<FeedbackResponseAttributes> questionResponses = testData.feedbackResponses.values().stream()
                .filter(fr -> fr.getFeedbackQuestionId().equals(Integer.toString(question.getQuestionNumber())))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> selfEvaluationResponses = questionResponses.stream()
                .filter(fr -> fr.getGiver().equals(currentInstructor.getEmail())
                        && fr.getRecipient().equals(currentInstructor.getEmail()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesByOthers = questionResponses.stream()
                .filter(fr -> !fr.getGiver().equals(currentInstructor.getEmail())
                        && visibleResponseGivers.contains(fr.getGiver()))
                .collect(Collectors.toList());

        List<FeedbackResponseAttributes> responsesToSelf = new ArrayList<>();
        if (visibleResponseGivers.contains("RECEIVER") || visibleResponseGivers.contains("INSTRUCTORS")) {
            responsesToSelf = questionResponses.stream()
                    .filter(fr -> !fr.getGiver().equals(currentInstructor.getEmail())
                            && fr.getRecipient().equals(currentInstructor.getEmail()))
                    .collect(Collectors.toList());
        }

        List<FeedbackResponseAttributes> otherResponses = new ArrayList<>();
        otherResponses.addAll(selfEvaluationResponses);
        otherResponses.addAll(responsesByOthers);
        otherResponses.addAll(responsesToSelf);

        return editIdentifiers(currentInstructor, otherResponses);
    }

    private Set<String> getVisibleGivers(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        return getRelevantUsers(currentStudent, question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleGivers(InstructorAttributes currentInstructor, FeedbackQuestionAttributes question) {
        return getRelevantUsersForInstructors(question.getShowGiverNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(StudentAttributes currentStudent, FeedbackQuestionAttributes question) {
        return getRelevantUsers(currentStudent, question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentStudent, user))
                .collect(Collectors.toSet());
    }

    private Set<String> getVisibleRecipients(InstructorAttributes currentInstructor, FeedbackQuestionAttributes question) {
        return getRelevantUsersForInstructors(question.getShowRecipientNameTo()).stream()
                .map(user -> getIdentifier(currentInstructor, user))
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
        students.forEach(s -> relevantUsers.add(s.getEmail()));
        students.forEach(s -> relevantUsers.add(s.getTeam()));

        if (relevantParticipants.contains(FeedbackParticipantType.RECEIVER)) {
            relevantUsers.add("RECEIVER");
        }

        return relevantUsers;
    }

    private Set<String> getRelevantUsersForInstructors(List<FeedbackParticipantType> relevantParticipants) {
        Set<String> relevantUsers = new HashSet<>();
        if (relevantParticipants.contains(FeedbackParticipantType.RECEIVER)) {
            relevantUsers.add("RECEIVER");
        }
        if (relevantParticipants.contains(FeedbackParticipantType.INSTRUCTORS)) {
            relevantUsers.add("INSTRUCTORS");
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
            fr.setGiver(getIdentifier(currentStudent, fr.getGiver()));
            fr.setRecipient(getIdentifier(currentStudent, fr.getRecipient()));
        });
        return editedResponses;
    }

    private List<FeedbackResponseAttributes> editIdentifiers(InstructorAttributes currentInstructor,
                                                             List<FeedbackResponseAttributes> responses) {
        List<FeedbackResponseAttributes> editedResponses = deepCopyResponses(responses);
        editedResponses.forEach(fr -> {
            fr.setGiver(getIdentifier(currentInstructor, fr.getGiver()));
            fr.setRecipient(getIdentifier(currentInstructor, fr.getRecipient()));
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

    private String getIdentifier(InstructorAttributes currentInstructor, String user) {
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
                (FeedbackRubricQuestionDetails) testData.feedbackQuestions.get("qn10").getQuestionDetailsCopy();
        List<String> subQns = rubricsQnDetails.getRubricSubQuestions();
        String[] formattedSubQns = { "a) " + subQns.get(0), "b) " + subQns.get(1), "c) " + subQns.get(2) };

        String[][] expectedRubricStats = {
                {
                        formattedSubQns[0],
                        "33.33% (1)",
                        "33.33% (1)",
                        "0% (0)",
                        "0% (0)",
                        "33.33% (1)",
                },
                {
                        formattedSubQns[1],
                        "0% (0)",
                        "0% (0)",
                        "33.33% (1)",
                        "0% (0)",
                        "66.67% (2)",
                },
                {
                        formattedSubQns[2],
                        "0% (0)",
                        "0% (0)",
                        "0% (0)",
                        "66.67% (2)",
                        "33.33% (1)",
                },
        };

        String[][] expectedRubricStatsExcludingSelf = {
                {
                        formattedSubQns[0],
                        "50% (1)",
                        "0% (0)",
                        "0% (0)",
                        "0% (0)",
                        "50% (1)",
                },
                {
                        formattedSubQns[1],
                        "0% (0)",
                        "0% (0)",
                        "0% (0)",
                        "0% (0)",
                        "100% (2)",
                },
                {
                        formattedSubQns[2],
                        "0% (0)",
                        "0% (0)",
                        "0% (0)",
                        "50% (1)",
                        "50% (1)",
                },
        };

        resultsPage.verifyRubricStatistics(10, expectedRubricStats, expectedRubricStatsExcludingSelf);
    }
}
