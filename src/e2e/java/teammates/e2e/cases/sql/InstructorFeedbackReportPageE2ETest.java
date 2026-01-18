package teammates.e2e.cases.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPage;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.ThreadHelper;

public class InstructorFeedbackReportPageE2ETest extends BaseE2ETestCase {

    private Instructor instructor;
    private String fileName;
    private Student studentToEmail;

    private Collection<Instructor> instructors;
    private Collection<Student> students;

    private AppUrl resultsUrl;
    private InstructorFeedbackResultsPage resultsPage;

    // Maps to organise responses
    private Map<FeedbackQuestion, List<FeedbackResponse>> questionToResponses;
    private Map<FeedbackQuestion, Map<String, List<FeedbackResponse>>> questionToGiverToResponses;
    private Map<FeedbackQuestion, Map<String, List<FeedbackResponse>>> questionToRecipientToResponses;

    // We either test all questions or just use qn2
    private FeedbackQuestion qn2;
    private List<FeedbackResponse> qn2Responses;
    private Map<String, List<FeedbackResponse>> qn2GiverResponses;
    private Map<String, List<FeedbackResponse>> qn2RecipientResponses;

    // For testing section filtering
    private String section;
    private List<FeedbackResponse> filteredQn2Responses;
    private Map<String, List<FeedbackResponse>> filteredQn2GiverResponses;
    private Map<String, List<FeedbackResponse>> filteredQn2RecipientResponses;

    // For testing missing responses
    private FeedbackResponse missingResponse;
    private Map<String, List<FeedbackResponse>> qn2GiverResponsesWithMissing;
    private Map<String, List<FeedbackResponse>> qn2RecipientResponsesWithMissing;

    // For testing comment
    private FeedbackResponse responseWithComment;
    private FeedbackResponseComment comment;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorFeedbackReportPageE2ETestSql.json"));
        studentToEmail = testData.students.get("IFRep.emily@CS2103");
        studentToEmail.setEmail(TestProperties.TEST_EMAIL);

        instructor = testData.instructors.get("IFRep.instr.CS2104");
        FeedbackSession fileSession = testData.feedbackSessions.get("Open Session 2");
        fileName = "/" + fileSession.getCourse().getId() + "_" + fileSession.getName() + "_result.csv";

        instructors = testData.instructors.values();
        students = testData.students.values();
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);

        Course course = testData.courses.get("tm.e2e.IFRep.CS2104");
        FeedbackSession feedbackSession = testData.feedbackSessions.get("Open Session");

        resultsUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());

        organiseResponses(course.getId());

        qn2 = testData.feedbackQuestions.get("qn2");
        qn2Responses = questionToResponses.get(qn2);
        qn2GiverResponses = questionToGiverToResponses.get(qn2);
        qn2RecipientResponses = questionToRecipientToResponses.get(qn2);

        section = testData.students.get("IFRep.alice@CS2104").getSectionName();
        filteredQn2Responses = filterResponsesBySection(qn2Responses, section);
        filteredQn2GiverResponses = filterMapBySection(qn2GiverResponses, section);
        filteredQn2RecipientResponses = filterMapBySection(qn2RecipientResponses, section);

        Student noResponseStudent = testData.students.get("IFRep.benny@CS2104");
        Student teammate = testData.students.get("IFRep.alice@CS2104");
        missingResponse = getMissingResponse(qn2.getQuestionNumber(), noResponseStudent, teammate);
        qn2GiverResponsesWithMissing = addMissingResponseToMap(qn2GiverResponses, missingResponse, noResponseStudent.getEmail());
        qn2RecipientResponsesWithMissing = addMissingResponseToMap(qn2RecipientResponses, missingResponse, teammate.getEmail());

        responseWithComment = testData.feedbackResponses.get("qn2response1");
        FeedbackResponseComment sqlComment = testData.feedbackResponseComments.get("qn2Comment2");
        comment = sqlComment;
    }

    @Override
    public void testAll() {
        // not used; run individual test cases instead as the entire test cases take > 5 minutes to run
    }

    @Test
    public void testQuestionView() {
        logout();
        resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("Question view: no missing responses");
        resultsPage.includeMissingResponses(false);

        for (Map.Entry<FeedbackQuestion, List<FeedbackResponse>> entry : questionToResponses.entrySet()) {
            resultsPage.verifyQnViewResponses(
                    entry.getKey(),
                    entry.getValue(),
                    instructors, students);
        }
        resultsPage.verifyQnViewStats(qn2, qn2Responses, instructors, students);

        ______TS("Question view: filter by section");
        resultsPage.filterBySectionEither(section);

        resultsPage.verifyQnViewResponses(qn2, filteredQn2Responses, instructors, students);
        resultsPage.verifyQnViewStats(qn2, filteredQn2Responses, instructors, students);
        resultsPage.unfilterResponses();

        ______TS("Question view: with missing responses");
        List<FeedbackResponse> qn2WithMissing = new ArrayList<>(qn2Responses);
        qn2WithMissing.add(missingResponse);
        sortResponses(qn2WithMissing);
        resultsPage.includeMissingResponses(true);

        resultsPage.verifyQnViewResponses(qn2, qn2WithMissing, instructors, students);
        ______TS("Question view: hide statistics");
        resultsPage.includeStatistics(false);

        resultsPage.verifyQnViewStatsHidden(qn2);

        ______TS("Question view: verify comments");
        resultsPage.verifyQnViewComment(qn2, comment, responseWithComment, instructors, students);
    }

    @Test
    public void testGrqView() {
        logout();
        resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("GRQ view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestion question : questionToResponses.keySet()) {
            verifyGrqViewResponses(question, questionToGiverToResponses.get(question), isGroupedByTeam);
        }

        ______TS("GRQ view: not grouped by team");
        isGroupedByTeam = false;
        resultsPage.includeGroupingByTeam(false);

        verifyGrqViewResponses(qn2, qn2GiverResponses, isGroupedByTeam);

        ______TS("GRQ view: filter by section");
        resultsPage.filterBySectionEither(section);

        verifyGrqViewResponses(qn2, filteredQn2GiverResponses, isGroupedByTeam);
        resultsPage.unfilterResponses();

        ______TS("GRQ view: with missing responses");
        resultsPage.includeMissingResponses(true);
        verifyGrqViewResponses(qn2, qn2GiverResponsesWithMissing, isGroupedByTeam);

        ______TS("GRQ view: verify comments");
        resultsPage.verifyGrqViewComment(qn2, comment, responseWithComment, instructors, students, false);
    }

    @Test
    public void testRgqView() {
        logout();
        resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("RGQ view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestion question : questionToResponses.keySet()) {
            verifyRgqViewResponses(question, questionToRecipientToResponses.get(question), isGroupedByTeam);
        }

        ______TS("RGQ view: not grouped by team");
        isGroupedByTeam = false;
        resultsPage.includeGroupingByTeam(false);

        verifyRqgViewResponses(qn2, qn2RecipientResponses, isGroupedByTeam);

        ______TS("RGQ view: filter by section");
        resultsPage.filterBySectionEither(section);

        verifyRqgViewResponses(qn2, filteredQn2RecipientResponses, isGroupedByTeam);
        resultsPage.unfilterResponses();

        ______TS("RGQ view: with missing responses");
        resultsPage.includeMissingResponses(true);

        verifyRgqViewResponses(qn2, qn2RecipientResponsesWithMissing, isGroupedByTeam);

        ______TS("RGQ view: verify comments");
        resultsPage.verifyRgqViewComment(qn2, comment, responseWithComment, instructors, students, false);
    }

    @Test
    public void testGqrView() {
        logout();
        resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("GQR view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestion question : questionToResponses.keySet()) {
            verifyGqrViewResponses(question, questionToGiverToResponses.get(question), isGroupedByTeam);
        }
        verifyGqrViewStats(qn2, getResponsesByTeam(qn2, true), isGroupedByTeam);

        ______TS("GQR view: not grouped by team");
        isGroupedByTeam = false;
        resultsPage.includeGroupingByTeam(false);

        verifyGqrViewResponses(qn2, qn2GiverResponses, isGroupedByTeam);
        verifyGqrViewStats(qn2, qn2GiverResponses, isGroupedByTeam);

        ______TS("GQR view: filter by section");
        resultsPage.filterBySectionEither(section);

        verifyGqrViewStats(qn2, filteredQn2GiverResponses, isGroupedByTeam);
        resultsPage.unfilterResponses();

        ______TS("GQR view: with missing responses");
        resultsPage.includeMissingResponses(true);

        verifyGqrViewResponses(qn2, qn2GiverResponsesWithMissing, isGroupedByTeam);

        ______TS("GQR view: hide statistics");
        resultsPage.includeStatistics(false);
        for (String giver : qn2GiverResponses.keySet()) {
            resultsPage.verifyGqrViewStatsHidden(qn2, giver, instructorAttrs, studentAttrs, isGroupedByTeam);
        }

        ______TS("GQR view: verify comments");
        resultsPage.verifyGqrViewComment(qn2, comment, responseWithComment, instructors, students, false);
    }

    @Test
    public void testRqgView() {
        logout();
        resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("RQG view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestion question : questionToResponses.keySet()) {
            verifyRqgViewResponses(question, questionToRecipientToResponses.get(question), isGroupedByTeam);
        }
        verifyRqgViewStats(qn2, getResponsesByTeam(qn2, false), isGroupedByTeam);

        ______TS("RQG view: not grouped by team");
        isGroupedByTeam = false;
        resultsPage.includeGroupingByTeam(false);

        verifyRqgViewStats(qn2, qn2RecipientResponses, isGroupedByTeam);

        ______TS("RQG view: filter by section");
        resultsPage.filterBySectionEither(section);

        verifyRqgViewStats(qn2, filteredQn2RecipientResponses, isGroupedByTeam);
        resultsPage.unfilterResponses();

        ______TS("RQG view: with missing responses");
        resultsPage.includeMissingResponses(true);

        verifyRqgViewResponses(qn2, qn2RecipientResponsesWithMissing, isGroupedByTeam);

        ______TS("RQG view: hide statistics");
        resultsPage.includeStatistics(false);
        for (String recipient : qn2RecipientResponses.keySet()) {
            resultsPage.verifyRqgViewStatsHidden(qn2, recipient, instructors, students, isGroupedByTeam);
        }

        ______TS("RQG view: verify comments");
        resultsPage.verifyRqgViewComment(qn2, comment, responseWithComment, instructors, students, false);
    }

    @Test
    public void testActions() {
        logout();

        Course course = testData.courses.get("tm.e2e.IFRep.CS2103");
        FeedbackSession feedbackSession = testData.feedbackSessions.get("Open Session 2");

        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getName());
        resultsPage = loginToPage(url, InstructorFeedbackResultsPage.class, instructor.getGoogleId());

        ______TS("verify loaded session details");
        resultsPage.verifySessionDetails(toFsa(feedbackSession));

        ______TS("unpublish results");
        resultsPage.unpublishSessionResults();

        resultsPage.verifyStatusMessage("The feedback session has been unpublished.");
        verifySessionPublishedState(feedbackSession, false);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]");

        ______TS("publish results");
        resultsPage.publishSessionResults();

        resultsPage.verifyStatusMessage("The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.");
        verifySessionPublishedState(feedbackSession, true);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]");

        ______TS("download results");
        resultsPage.downloadResults();

        List<String> expectedContent = java.util.Arrays.asList("Course," + course.getId(),
                "Session Name," + feedbackSession.getName(),
                "Question 1,What part of the product did this teammate contribute most to?",
                "Participants who have not responded to any question",
                String.format("%s,%s,%s", studentToEmail.getTeamName(), studentToEmail.getName(), studentToEmail.getEmail()));
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("verify no response panel details");
        List<StudentAttributes> notResponded = getNotRespondedStudents(course.getId());
        notResponded.sort(Comparator.comparing(StudentAttributes::getName).reversed());
        resultsPage.sortNoResponseByName();
        resultsPage.verifyNoResponsePanelDetails(notResponded);

        ______TS("remind all who have not responded to any question");
        resultsPage.remindAllNonResponders();

        resultsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students and instructors."
                + " Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getName() + "]");
    }

    private void verifySessionPublishedState(FeedbackSession feedbackSession, boolean state) {
        int retryLimit = 5;
        var actual = getFeedbackSession(feedbackSession.getCourse().getId(), feedbackSession.getName());
        while (isFeedbackSessionPublished(actual.getPublishStatus()) != state && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getFeedbackSession(feedbackSession.getCourse().getId(), feedbackSession.getName());
        }
        assertEquals(isFeedbackSessionPublished(actual.getPublishStatus()), state);
    }

    private List<FeedbackQuestion> getQuestionsByCourse(String courseId) {
        return testData.feedbackQuestions.values().stream()
                .filter(question -> question.getFeedbackSession().getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
    }

    private List<StudentAttributes> getNotRespondedStudents(String courseId) {
        Set<String> responders = testData.feedbackResponses.values().stream()
                .filter(r -> r.getFeedbackQuestion().getFeedbackSession().getCourse().getId().equals(courseId))
                .map(FeedbackResponse::getGiver)
                .collect(Collectors.toSet());

        return testData.students.values().stream()
                .filter(s -> !responders.contains(s.getEmail()) && s.getCourse().getId().equals(courseId))
                .map(StudentAttributes::valueOf)
                .collect(Collectors.toList());
    }

    private List<FeedbackResponse> getResponsesByQuestion(String courseId, int qnNum) {
        List<FeedbackResponse> responses = testData.feedbackResponses.values().stream()
                .filter(r -> r.getFeedbackQuestion().getFeedbackSession().getCourse().getId().equals(courseId)
                        && r.getFeedbackQuestion().getQuestionNumber() == qnNum)
                .collect(Collectors.toList());
        sortResponses(responses);
        return responses;
    }

    private void sortResponses(List<FeedbackResponse> responses) {
        responses.sort((r1, r2) -> {
            if (r1.getGiver().equals(r2.getGiver())) {
                return r1.getRecipient().compareTo(r2.getRecipient());
            }
            return r1.getGiver().compareTo(r2.getGiver());
        });
    }

    private String getTeamNameSql(String participantEmail) {
        return students.stream()
                .filter(s -> s.getEmail().equals(participantEmail))
                .findFirst()
                .map(Student::getTeamName)
                .orElse(null);
    }

    private String getTeamNameDto(String participantEmail) {
        return studentAttrs.stream()
                .filter(s -> s.getEmail().equals(participantEmail))
                .findFirst()
                .map(StudentAttributes::getTeam)
                .orElse(null);
    }

    private Map<String, List<FeedbackResponse>> getResponsesByTeam(FeedbackQuestion question, boolean isGiver) {
        Map<String, List<FeedbackResponse>> userToResponses = isGiver
                ? questionToGiverToResponses.get(question)
                : questionToRecipientToResponses.get(question);

        Map<String, List<FeedbackResponse>> teamResponses = new HashMap<>();
        for (Map.Entry<String, List<FeedbackResponse>> entry : userToResponses.entrySet()) {
            String user = entry.getKey();
            String team = getTeamNameSql(user);
            List<FeedbackResponse> responses = entry.getValue();

            teamResponses.computeIfAbsent(team, k -> new ArrayList<>()).addAll(responses);
        }
        return teamResponses;
    }

    private Map<String, List<FeedbackResponse>> addMissingResponseToMap(
            Map<String, List<FeedbackResponse>> map,
            FeedbackResponse missingResponse, String key) {
        Map<String, List<FeedbackResponse>> copy = map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
        copy.computeIfAbsent(key, k -> new ArrayList<>()).add(missingResponse);
        sortResponses(copy.get(key));
        return copy;
    }

    private Map<String, List<FeedbackResponse>> filterMapBySection(
            Map<String, List<FeedbackResponse>> userToResponses,
            String sec) {
        Map<String, List<FeedbackResponse>> filtered = new HashMap<>();
        for (Map.Entry<String, List<FeedbackResponse>> entry : userToResponses.entrySet()) {
            List<FeedbackResponse> filteredResponses = filterResponsesBySection(entry.getValue(), sec);
            if (!filteredResponses.isEmpty()) {
                filtered.put(entry.getKey(), filteredResponses);
            }
        }
        return filtered;
    }

    private List<FeedbackResponse> filterResponsesBySection(List<FeedbackResponse> responses, String sec) {
        return responses.stream()
                .filter(r -> (r.getGiverSection() != null && sec.equals(r.getGiverSection().getName()))
                        || (r.getRecipientSection() != null && sec.equals(r.getRecipientSection().getName())))
                .collect(Collectors.toList());
    }

    private FeedbackResponse getMissingResponse(int qnNum, Student giver, Student recipient) {
        // Represent a missing response by only tracking giver/recipient and question number through ID
        // We will convert this to a minimal FeedbackResponseAttributes later
        return FeedbackResponse.makeResponse(
                testData.feedbackQuestions.values().stream()
                        .filter(q -> q.getQuestionNumber() == qnNum)
                        .findFirst().orElseThrow(),
                giver.getEmail(), giver.getSection(), recipient.getEmail(), recipient.getSection(),
                // Use a TEXT empty response to stand-in; page object treats missing by null session name later
                new teammates.common.datatransfer.questions.FeedbackTextResponseDetails("")
        );
    }

    private void verifyGqrViewResponses(FeedbackQuestion question,
                                        Map<String, List<FeedbackResponse>> giverToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : giverToResponses.entrySet()) {
            // TODO: migrate resultsPage to sql version.
            resultsPage.verifyGqrViewResponses(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void verifyRqgViewResponses(FeedbackQuestion question,
                                        Map<String, List<FeedbackResponse>> recipientToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : recipientToResponses.entrySet()) {
            resultsPage.verifyRqgViewResponses(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void verifyGrqViewResponses(FeedbackQuestion question,
                                        Map<String, List<FeedbackResponse>> giverToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : giverToResponses.entrySet()) {
            resultsPage.verifyGrqViewResponses(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void verifyRgqViewResponses(FeedbackQuestion question,
                                        Map<String, List<FeedbackResponse>> recipientToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : recipientToResponses.entrySet()) {
            resultsPage.verifyRgqViewResponses(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void verifyRqgViewStats(FeedbackQuestion question,
                                    Map<String, List<FeedbackResponse>> responses,
                                    boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : responses.entrySet()) {
            resultsPage.verifyRqgViewStats(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void verifyGqrViewStats(FeedbackQuestion question,
                                    Map<String, List<FeedbackResponse>> responses,
                                    boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponse>> entry : responses.entrySet()) {
            resultsPage.verifyGqrViewStats(question, entry.getValue(), isGroupedByTeam,
                    instructors, students);
        }
    }

    private void organiseResponses(String courseId) {
        List<FeedbackQuestion> questions = getQuestionsByCourse(courseId);
        questionToResponses = new HashMap<>();
        for (FeedbackQuestion question : questions) {
            List<FeedbackResponse> responses = getResponsesByQuestion(courseId, question.getQuestionNumber());
            questionToResponses.put(question, responses);
        }

        questionToGiverToResponses = new HashMap<>();
        questionToRecipientToResponses = new HashMap<>();

        for (Map.Entry<FeedbackQuestion, List<FeedbackResponse>> entry : questionToResponses.entrySet()) {
            FeedbackQuestion question = entry.getKey();
            List<FeedbackResponse> responses = entry.getValue();

            Map<String, List<FeedbackResponse>> recipientToResponse = new HashMap<>();
            Map<String, List<FeedbackResponse>> giverToResponse = new HashMap<>();
            for (FeedbackResponse response : responses) {
                String recipient = response.getRecipient();
                String giver = response.getGiver();
                recipientToResponse.computeIfAbsent(recipient, k -> new ArrayList<>()).add(response);
                giverToResponse.computeIfAbsent(giver, k -> new ArrayList<>()).add(response);
            }

            questionToRecipientToResponses.put(question, recipientToResponse);
            questionToGiverToResponses.put(question, giverToResponse);
        }
    }
  
}
