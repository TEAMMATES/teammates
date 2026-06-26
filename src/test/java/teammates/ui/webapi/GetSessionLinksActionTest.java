package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.CourseJoinKey;
import teammates.common.datatransfer.FeedbackSessionSubmissionStatus;
import teammates.common.datatransfer.SessionKeyType;
import teammates.common.datatransfer.SessionResultLink;
import teammates.common.datatransfer.SessionSubmissionLink;
import teammates.common.util.Const;
import teammates.common.util.KeyUtil;
import teammates.common.util.LinksUtil;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.SessionLinksData;

/**
 * Tests for {@link GetSessionLinksAction}.
 */
public class GetSessionLinksActionTest extends BaseActionTest<GetSessionLinksAction, SessionLinksData> {

    private static final String DUMMY_UUID = UUID.fromString("5d17a2a8-3e2a-40a9-b9e2-3e4a3f6a8680").toString();

    @Test(groups = GroupNames.ACTION)
    public void getSessionLinksAction_adminForStudent_returnsStudentLinks() throws Exception {
        var adminAccount = given.account("admin", a -> a.admin());
        var student = given.student("student", s -> s.defaultCourse());
        var awaitingSession = given.feedbackSession("awaiting-session", fs -> fs.defaultCourse().waitingToOpen());
        var openSession = given.feedbackSession("open-session", fs -> fs.defaultCourse().opened());
        var closedSession = given.feedbackSession("closed-session", fs -> fs.defaultCourse().closed());
        var publishedSession = given.feedbackSession("published-session", fs -> fs.defaultCourse().published());
        persistGivenData(given);

        SessionLinksData result = execute(getRequest(student.id().toString(), adminAccount.id()));

        assertCourseJoinLink(result.getCourseJoinLink(), student.id(), student.regKey());
        assertEquals(4, result.getSubmissionLinks().size());
        assertEquals(1, result.getResultsLinks().size());

        Map<UUID, SessionSubmissionLink> submissionLinksById = result.getSubmissionLinks().stream()
                .collect(Collectors.toMap(SessionSubmissionLink::feedbackSessionId, Function.identity()));
        assertEquals(FeedbackSessionSubmissionStatus.VISIBLE_NOT_OPEN,
                submissionLinksById.get(awaitingSession.id()).submissionStatus());
        assertEquals(FeedbackSessionSubmissionStatus.OPEN,
                submissionLinksById.get(openSession.id()).submissionStatus());
        assertEquals(FeedbackSessionSubmissionStatus.CLOSED,
                submissionLinksById.get(closedSession.id()).submissionStatus());
        assertStudentSessionLink(submissionLinksById.get(openSession.id()).url(), openSession.id(),
                student.id(), student.regKey(), SessionKeyType.SUBMISSION);

        SessionResultLink resultsLink = result.getResultsLinks().get(0);
        assertEquals(publishedSession.id(), resultsLink.feedbackSessionId());
        assertStudentSessionLink(resultsLink.url(), publishedSession.id(), student.id(),
                student.regKey(), SessionKeyType.RESULTS);
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionLinksAction_adminForInstructor_returnsInstructorLinks() throws Exception {
        var adminAccount = given.account("admin", a -> a.admin());
        var instructor = given.instructor("instructor", i -> i.defaultCourse());
        var openSession = given.feedbackSession("open-session", fs -> fs.defaultCourse().opened());
        var publishedSession = given.feedbackSession("published-session", fs -> fs.defaultCourse().published());
        persistGivenData(given);

        SessionLinksData result = execute(getRequest(instructor.id().toString(), adminAccount.id()));

        assertCourseJoinLink(result.getCourseJoinLink(), instructor.id(), instructor.regKey());
        assertTrue(result.getSubmissionLinks().stream().anyMatch(
                link -> openSession.id().equals(link.feedbackSessionId())
                        && LinksUtil.getInstructorSessionSubmitUrl(openSession.id()).equals(link.url())));
        assertTrue(result.getResultsLinks().stream().anyMatch(
                link -> publishedSession.id().equals(link.feedbackSessionId())
                        && LinksUtil.getInstructorSessionResultsUrl(publishedSession.id()).equals(link.url())));
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionLinksAction_nonAdmin_throwsUnauthorizedAccessException() {
        var regularAccount = given.account("regular");
        var student = given.student("student");
        persistGivenData(given);

        assertActionThrows(UnauthorizedAccessException.class, getRequest(student.id().toString(), regularAccount.id()));
    }

    @Test(groups = GroupNames.ACTION)
    public void getSessionLinksAction_unknownUser_throwsEntityNotFoundException() {
        var adminAccount = given.account("admin", a -> a.admin());
        persistGivenData(given);

        assertActionThrows(EntityNotFoundException.class, getRequest(DUMMY_UUID, adminAccount.id()));
    }

    private RequestContext getRequest(String userId, UUID accountId) {
        return new RequestContext()
                .withParam(Const.ParamsNames.USER_ID, userId)
                .withAccountAuth(accountId);
    }

    private void assertCourseJoinLink(String url, UUID userId, String regKey) throws Exception {
        URI uri = URI.create(url);
        String query = uri.getQuery();
        String encryptedKey = query.substring("key=".length(), query.indexOf("&entityType="));
        CourseJoinKey joinKey = KeyUtil.decryptCourseJoinKey(encryptedKey);
        assertEquals(userId, joinKey.userId());
        assertEquals(regKey, joinKey.regKey());
    }

    private void assertStudentSessionLink(String url, UUID feedbackSessionId, UUID studentId,
            String regKey, SessionKeyType type) throws Exception {
        URI uri = URI.create(url);
        String key = uri.getQuery().substring("key=".length());
        var sessionKey = KeyUtil.decryptSessionKey(key);
        assertEquals(feedbackSessionId, sessionKey.feedbackSessionId());
        assertEquals(studentId, sessionKey.userId());
        assertEquals(regKey, sessionKey.regKey());
        assertEquals(type, sessionKey.type());
    }
}
