package teammates.e2e.cases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPage;
import teammates.e2e.util.TestProperties;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_RESULTS_PAGE}.
 */
public class InstructorFeedbackResultsPageE2ETest extends BaseE2ETestCase {
    private InstructorAttributes instructor;
    private String fileName;
    private StudentAttributes studentToEmail;

    private Collection<InstructorAttributes> instructors;
    private Collection<StudentAttributes> students;

    private AppUrl resultsUrl;
    private InstructorFeedbackResultsPage resultsPage;

    // Maps to organise responses
    private Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> questionToResponses;
    private Map<FeedbackQuestionAttributes, Map<String, List<FeedbackResponseAttributes>>> questionToGiverToResponses;
    private Map<FeedbackQuestionAttributes, Map<String, List<FeedbackResponseAttributes>>> questionToRecipientToResponses;

    // We either test all questions or just use qn2
    private FeedbackQuestionAttributes qn2;
    private List<FeedbackResponseAttributes> qn2Responses;
    private Map<String, List<FeedbackResponseAttributes>> qn2GiverResponses;
    private Map<String, List<FeedbackResponseAttributes>> qn2RecipientResponses;

    // For testing section filtering
    private String section;
    private List<FeedbackResponseAttributes> filteredQn2Responses;
    private Map<String, List<FeedbackResponseAttributes>> filteredQn2GiverResponses;
    private Map<String, List<FeedbackResponseAttributes>> filteredQn2RecipientResponses;

    // For testing missing responses
    private FeedbackResponseAttributes missingResponse;
    private Map<String, List<FeedbackResponseAttributes>> qn2GiverResponsesWithMissing;
    private Map<String, List<FeedbackResponseAttributes>> qn2RecipientResponsesWithMissing;

    // For testing comment
    private FeedbackResponseAttributes responseWithComment;
    private FeedbackResponseCommentAttributes comment;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackResultsPageE2ETest.json");
        studentToEmail = testData.students.get("Emily");
        studentToEmail.email = TestProperties.TEST_EMAIL;
        removeAndRestoreDataBundle(testData);

        instructor = testData.instructors.get("tm.e2e.IFRes.instr");
        FeedbackSessionAttributes fileSession = testData.feedbackSessions.get("Open Session 2");
        fileName = "/" + fileSession.getCourseId() + "_" + fileSession.getFeedbackSessionName() + "_result.csv";

        instructors = testData.instructors.values();
        students = testData.students.values();
    }

    @BeforeClass
    public void classSetup() {
        deleteDownloadsFile(fileName);

        CourseAttributes course = testData.courses.get("tm.e2e.IFRes.CS2104");
        FeedbackSessionAttributes feedbackSession = testData.feedbackSessions.get("Open Session");

        resultsUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withUserId(instructor.getGoogleId())
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());

        // -------------------------------------- Prepare responses -------------------------------------- //
        organiseResponses(course.getId());

        qn2 = testData.feedbackQuestions.get("qn2");
        qn2Responses = questionToResponses.get(qn2);
        qn2GiverResponses = questionToGiverToResponses.get(qn2);
        qn2RecipientResponses = questionToRecipientToResponses.get(qn2);

        section = testData.students.get("Alice").getSection();
        filteredQn2Responses = filterResponsesBySection(qn2Responses, section);
        filteredQn2GiverResponses = filterMapBySection(qn2GiverResponses, section);
        filteredQn2RecipientResponses = filterMapBySection(qn2RecipientResponses, section);

        StudentAttributes noResponseStudent = testData.students.get("Benny");
        StudentAttributes teammate = testData.students.get("Alice");
        missingResponse = getMissingResponse(qn2.getQuestionNumber(), noResponseStudent, teammate);
        qn2GiverResponsesWithMissing =
                addMissingResponseToMap(qn2GiverResponses, missingResponse, noResponseStudent.getEmail());
        qn2RecipientResponsesWithMissing =
                addMissingResponseToMap(qn2RecipientResponses, missingResponse, teammate.getEmail());

        responseWithComment = testData.feedbackResponses.get("qn2response1");
        comment = testData.feedbackResponseComments.get("qn2Comment2");
    }

    @Override
    public void testAll() {
        // not used; run individual test cases instead as the entire test cases take > 5 minutes to run
    }

    @Test
    public void testQuestionView() {
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("Question view: no missing responses");
        resultsPage.includeMissingResponses(false);

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry
                : questionToResponses.entrySet()) {
            resultsPage.verifyQnViewResponses(entry.getKey(), entry.getValue(), instructors, students);
        }
        resultsPage.verifyQnViewStats(qn2, qn2Responses, instructors, students);

        ______TS("Question view: filter by section");
        resultsPage.filterBySectionEither(section);

        resultsPage.verifyQnViewResponses(qn2, filteredQn2Responses, instructors, students);
        resultsPage.verifyQnViewStats(qn2, filteredQn2Responses, instructors, students);
        resultsPage.unfilterResponses();

        ______TS("Question view: with missing responses");
        qn2Responses.add(missingResponse);
        sortResponses(qn2Responses);
        resultsPage.includeMissingResponses(true);

        resultsPage.verifyQnViewResponses(qn2, qn2Responses, instructors, students);

        ______TS("Question view: hide statistics");
        resultsPage.includeStatistics(false);

        resultsPage.verifyQnViewStatsHidden(qn2);

        ______TS("Question view: verify comments");
        resultsPage.verifyQnViewComment(qn2, comment, responseWithComment, instructors, students);
    }

    @Test
    public void testGrqView() {
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("GRQ view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestionAttributes question : questionToResponses.keySet()) {
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
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("RGQ view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestionAttributes question : questionToResponses.keySet()) {
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
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("GQR view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestionAttributes question : questionToResponses.keySet()) {
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
            resultsPage.verifyGqrViewStatsHidden(qn2, giver, instructors, students, isGroupedByTeam);
        }

        ______TS("GQR view: verify comments");
        resultsPage.verifyGqrViewComment(qn2, comment, responseWithComment, instructors, students, false);
    }

    @Test
    public void testRqgView() {
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("RQG view: no missing responses");
        boolean isGroupedByTeam = true;
        resultsPage.includeStatistics(true);
        resultsPage.includeGroupingByTeam(true);
        resultsPage.includeMissingResponses(false);

        for (FeedbackQuestionAttributes question : questionToResponses.keySet()) {
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
        CourseAttributes course = testData.courses.get("tm.e2e.IFRes.CS2103");
        FeedbackSessionAttributes feedbackSession = testData.feedbackSessions.get("Open Session 2");

        AppUrl resultsUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_RESULTS_PAGE)
                .withUserId(instructor.getGoogleId())
                .withCourseId(course.getId())
                .withSessionName(feedbackSession.getFeedbackSessionName());
        resultsPage = loginAdminToPage(resultsUrl, InstructorFeedbackResultsPage.class);

        ______TS("verify loaded session details");
        resultsPage.verifySessionDetails(feedbackSession);

        ______TS("unpublish results");
        resultsPage.unpublishSessionResults();

        resultsPage.verifyStatusMessage("The feedback session has been unpublished.");
        verifySessionPublishedState(feedbackSession, false);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results unpublished"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]");

        ______TS("publish results");
        resultsPage.publishSessionResults();

        resultsPage.verifyStatusMessage("The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.");
        verifySessionPublishedState(feedbackSession, true);
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session results published"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]");

        ______TS("download results");
        resultsPage.downloadResults();

        List<String> expectedContent = Arrays.asList("Course," + course.getId(),
                "Session Name," + feedbackSession.getFeedbackSessionName(),
                "Question 1,What part of the product did this teammate contribute most to?");
        verifyDownloadedFile(fileName, expectedContent);

        ______TS("verify no response panel details");
        List<StudentAttributes> studentAttributes = getNotRespondedStudents(course.getId());
        studentAttributes.sort(Comparator.comparing(StudentAttributes::getName).reversed());
        resultsPage.sortNoResponseByName();
        resultsPage.verifyNoResponsePanelDetails(studentAttributes);

        ______TS("remind all who have not responded to any question");
        resultsPage.remindAllNonResponders();

        resultsPage.verifyStatusMessage("Reminder e-mails have been sent out to those students and instructors."
                + " Please allow up to 1 hour for all the notification emails to be sent out.");
        verifyEmailSent(studentToEmail.getEmail(), "TEAMMATES: Feedback session reminder"
                + " [Course: " + course.getName() + "][Feedback Session: "
                + feedbackSession.getFeedbackSessionName() + "]");
    }

    private void verifySessionPublishedState(FeedbackSessionAttributes feedbackSession, boolean state) {
        int retryLimit = 5;
        FeedbackSessionAttributes actual = getFeedbackSession(feedbackSession.getCourseId(),
                feedbackSession.getFeedbackSessionName());
        while (actual.isPublished() == state && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(1000);
            actual = getFeedbackSession(feedbackSession.getCourseId(),
                    feedbackSession.getFeedbackSessionName());
        }
        assertEquals(actual.isPublished(), state);
    }

    private List<FeedbackQuestionAttributes> getQuestionsByCourse(String courseId) {
        return testData.feedbackQuestions.values().stream()
                .filter(question -> question.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    private List<StudentAttributes> getNotRespondedStudents(String courseId) {
        Set<String> responders = testData.feedbackResponses.values().stream()
                .filter(response -> response.getCourseId().equals(courseId))
                .map(FeedbackResponseAttributes::getGiver)
                .collect(Collectors.toSet());

        return testData.students.values().stream()
                .filter(student -> !responders.contains(student.getEmail()) && student.getCourse().equals(courseId))
                .collect(Collectors.toList());
    }

    private List<FeedbackResponseAttributes> getResponsesByQuestion(String courseId, int qnNum) {
        List<FeedbackResponseAttributes> responses = testData.feedbackResponses.values().stream()
                .filter(response -> response.getCourseId().equals(courseId)
                        && response.getFeedbackQuestionId().equals(Integer.toString(qnNum)))
                .collect(Collectors.toList());
        sortResponses(responses);
        return responses;
    }

    private void sortResponses(List<FeedbackResponseAttributes> responses) {
        responses.sort((r1, r2) -> {
            if (r1.getGiver().equals(r2.getGiver())) {
                return r1.getRecipient().compareTo(r2.getRecipient());
            }
            return r1.getGiver().compareTo(r2.getGiver());
        });
    }

    private String getTeamName(FeedbackParticipantType type, String participant, Collection<StudentAttributes> students) {
        if (type.equals(FeedbackParticipantType.NONE)) {
            return "No Specific Team";
        } else if (type.equals(FeedbackParticipantType.TEAMS)) {
            return participant;
        } else if (type.equals(FeedbackParticipantType.INSTRUCTORS)) {
            return "Instructors";
        }
        String teamName = students.stream()
                .filter(student -> student.getEmail().equals(participant))
                .findFirst()
                .map(StudentAttributes::getTeam)
                .orElse(null);

        if (teamName == null) {
            throw new RuntimeException("cannot find section name");
        }

        return teamName;
    }

    private Map<String, List<FeedbackResponseAttributes>> getResponsesByTeam(FeedbackQuestionAttributes question,
                                                                             boolean isGiver) {
        Map<String, List<FeedbackResponseAttributes>> userToResponses;
        if (isGiver) {
            userToResponses = questionToGiverToResponses.get(question);
        } else {
            userToResponses = questionToRecipientToResponses.get(question);
        }

        Map<String, List<FeedbackResponseAttributes>> teamResponses = new HashMap<>();
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : userToResponses.entrySet()) {
            String user = entry.getKey();
            FeedbackParticipantType type = isGiver ? question.getGiverType() : question.getRecipientType();
            String team = getTeamName(type, user, students);
            List<FeedbackResponseAttributes> responses = entry.getValue();

            if (!teamResponses.containsKey(team)) {
                teamResponses.put(team, new ArrayList<>());
            }
            teamResponses.get(team).addAll(responses);
        }

        return teamResponses;
    }

    private Map<String, List<FeedbackResponseAttributes>> addMissingResponseToMap(
                                         Map<String, List<FeedbackResponseAttributes>> map,
                                         FeedbackResponseAttributes missingResponse, String key) {
        Map<String, List<FeedbackResponseAttributes>> copy = map.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().stream().collect(Collectors.toList())));
        if (!copy.containsKey(key)) {
            copy.put(key, new ArrayList<>());
        }
        copy.get(key).add(missingResponse);
        sortResponses(copy.get(key));
        return copy;
    }

    private Map<String, List<FeedbackResponseAttributes>> filterMapBySection(
            Map<String, List<FeedbackResponseAttributes>> userToResponses,
            String section) {
        Map<String, List<FeedbackResponseAttributes>> filtered = new HashMap<>();
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : userToResponses.entrySet()) {
            List<FeedbackResponseAttributes> filteredResponses = filterResponsesBySection(entry.getValue(), section);
            if (!filteredResponses.isEmpty()) {
                filtered.put(entry.getKey(), filteredResponses);
            }
        }
        return filtered;
    }

    private List<FeedbackResponseAttributes> filterResponsesBySection(List<FeedbackResponseAttributes> responses,
                                                                     String section) {
        return responses.stream()
                .filter(r1 -> r1.getGiverSection().equals(section) || r1.getRecipientSection().equals(section))
                .collect(Collectors.toList());
    }

    private FeedbackResponseAttributes getMissingResponse(int qnNum, StudentAttributes giver, StudentAttributes recipient) {
        return FeedbackResponseAttributes.builder(Integer.toString(qnNum), giver.getEmail(), recipient.getEmail()).build();
    }

    private void verifyGqrViewResponses(FeedbackQuestionAttributes question,
                                        Map<String, List<FeedbackResponseAttributes>> giverToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : giverToResponses.entrySet()) {
            resultsPage.verifyGqrViewResponses(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void verifyRqgViewResponses(FeedbackQuestionAttributes question,
                                        Map<String, List<FeedbackResponseAttributes>> recipientToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : recipientToResponses.entrySet()) {
            resultsPage.verifyRqgViewResponses(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void verifyGrqViewResponses(FeedbackQuestionAttributes question,
                                        Map<String, List<FeedbackResponseAttributes>> giverToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : giverToResponses.entrySet()) {
            resultsPage.verifyGrqViewResponses(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void verifyRgqViewResponses(FeedbackQuestionAttributes question,
                                        Map<String, List<FeedbackResponseAttributes>> recipientToResponses,
                                        boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : recipientToResponses.entrySet()) {
            resultsPage.verifyRgqViewResponses(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void verifyRqgViewStats(FeedbackQuestionAttributes question,
                                    Map<String, List<FeedbackResponseAttributes>> responses,
                                    boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : responses.entrySet()) {
            resultsPage.verifyRqgViewStats(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void verifyGqrViewStats(FeedbackQuestionAttributes question,
                                    Map<String, List<FeedbackResponseAttributes>> responses,
                                    boolean isGroupedByTeam) {
        for (Map.Entry<String, List<FeedbackResponseAttributes>> entry : responses.entrySet()) {
            resultsPage.verifyGqrViewStats(question, entry.getValue(), isGroupedByTeam, instructors, students);
        }
    }

    private void organiseResponses(String courseId) {
        List<FeedbackQuestionAttributes> questions = getQuestionsByCourse(courseId);
        questionToResponses = new HashMap<>();
        for (FeedbackQuestionAttributes question : questions) {
            List<FeedbackResponseAttributes> responses = getResponsesByQuestion(courseId, question.getQuestionNumber());
            questionToResponses.put(question, responses);
        }

        questionToGiverToResponses = new HashMap<>();
        questionToRecipientToResponses = new HashMap<>();

        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry
                : questionToResponses.entrySet()) {
            FeedbackQuestionAttributes question = entry.getKey();
            List<FeedbackResponseAttributes> responses = entry.getValue();

            Map<String, List<FeedbackResponseAttributes>> recipientToResponse = new HashMap<>();
            Map<String, List<FeedbackResponseAttributes>> giverToResponse = new HashMap<>();
            for (FeedbackResponseAttributes response : responses) {
                String recipient = response.getRecipient();
                String giver = response.getGiver();
                if (!recipientToResponse.containsKey(recipient)) {
                    recipientToResponse.put(recipient, new ArrayList<>());
                }
                if (!giverToResponse.containsKey(giver)) {
                    giverToResponse.put(giver, new ArrayList<>());
                }

                recipientToResponse.get(recipient).add(response);
                giverToResponse.get(giver).add(response);
            }

            questionToRecipientToResponses.put(question, recipientToResponse);
            questionToGiverToResponses.put(question, giverToResponse);
        }
    }
}
