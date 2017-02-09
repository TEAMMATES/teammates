package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.Query;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to go through either:
 * <ul>
 * <li> every feedback session, </li>
 * <li> feedback sessions with start date within a specified range, </li>
 * <li> or a specified feedback session, </li>
 * </ul>
 * and verifies that the non-respondents do not have a response in the feedback session. <br>
 *
 * <p>If isPreview is false, whenever an inconsistency is found, {@code logic.updateRespondents} will
 * be used to recompute the respondents' set.
 *
 */
public class RepairFeedbackSessionResponseRate extends RemoteApiClient {

    private Logic logic = new Logic();
    private FeedbackSessionsDb fsDb = new FeedbackSessionsDb();

    // if isPreview is true, then no writes will be done
    private boolean isPreview = true;

    // if numDays is set to > 0,
    // then feedback sessions with start date from (now - numDays) days to the current time
    // will be retrieved and checked
    private int numDays = 180;

    // If numDays is not set, and
    // and both courseId and feedbackSessionName is specified,
    // the feedback session specified will be operated upon.
    // If either of courseId or feedbackSessionName is null,
    // then all feedback sessions will be checked
    private String courseId;
    private String feedbackSessionName;

    private Map<String, Set<String>> emailsInCourse = new HashMap<>();

    public static void main(String[] args) throws IOException {
        RepairFeedbackSessionResponseRate migrator = new RepairFeedbackSessionResponseRate();
        migrator.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<FeedbackSessionAttributes> feedbackSessions;
        if (numDays > 0) {
            feedbackSessions = getFeedbackSessionsWithStartDateNoOlderThan(numDays);
        } else if (courseId == null || feedbackSessionName == null) {
            feedbackSessions = getAllFeedbackSessions();
        } else {
            feedbackSessions = new ArrayList<FeedbackSessionAttributes>();
            feedbackSessions.add(logic.getFeedbackSession(feedbackSessionName, courseId));
        }

        try {
            for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
                System.out.println(feedbackSession.getIdentificationString());

                Set<String> nonRespondents = getNonRespondentsForFeedbackSession(feedbackSession);
                findAndFixInconsistentNonRespondentList(feedbackSession, nonRespondents);
            }
        } catch (EntityDoesNotExistException | InvalidParametersException e) {
            e.printStackTrace();
        }
    }

    private void findAndFixInconsistentNonRespondentList(FeedbackSessionAttributes feedbackSession,
                                    Set<String> nonRespondents) throws EntityDoesNotExistException,
                                    InvalidParametersException {
        boolean isRepairRequired = false;
        for (String nonRespondentEmail : nonRespondents) {
            boolean isRespondentWithResponses = logic.hasGiverRespondedForSession(
                                                        nonRespondentEmail,
                                                        feedbackSession.getFeedbackSessionName(),
                                                        feedbackSession.getCourseId());
            if (isRespondentWithResponses) {
                System.out.println("Inconsistent data for " + feedbackSession.getIdentificationString()
                                 + nonRespondentEmail);
                isRepairRequired = true;
            }
        }

        if (!isPreview && isRepairRequired) {
            System.out.println("fixing " + feedbackSession.getIdentificationString());
            logic.updateRespondents(feedbackSession.getFeedbackSessionName(), feedbackSession.getCourseId());
        }
    }

    private Set<String> getNonRespondentsForFeedbackSession(FeedbackSessionAttributes feedbackSession) {

        // obtain the respondents first
        Set<String> respondingStudentsEmail = feedbackSession.getRespondingStudentList();
        Set<String> respondingInstructorsEmail = feedbackSession.getRespondingInstructorList();

        Set<String> respondents = new HashSet<>(respondingInstructorsEmail);
        respondents.addAll(respondingStudentsEmail);

        Set<String> nonRespondentsEmails;
        // obtain emails of every student and instructor in the course
        if (emailsInCourse.containsKey(feedbackSession.getCourseId())) {
            nonRespondentsEmails = emailsInCourse.get(feedbackSession.getCourseId());
        } else {
            List<InstructorAttributes> allInstructors =
                                            logic.getInstructorsForCourse(feedbackSession.getCourseId());
            List<StudentAttributes> allStudents =
                                            logic.getStudentsForCourse(feedbackSession.getCourseId());
            List<EntityAttributes> allPossibleRespondents = new ArrayList<>();
            allPossibleRespondents.addAll(allInstructors);
            allPossibleRespondents.addAll(allStudents);

            nonRespondentsEmails = new HashSet<String>();
            for (EntityAttributes possibleRespondent : allPossibleRespondents) {
                if (possibleRespondent instanceof StudentAttributes) {
                    StudentAttributes student = (StudentAttributes) possibleRespondent;
                    nonRespondentsEmails.add(student.email);
                } else if (possibleRespondent instanceof InstructorAttributes) {
                    InstructorAttributes instructor = (InstructorAttributes) possibleRespondent;
                    nonRespondentsEmails.add(instructor.email);
                }
            }

            emailsInCourse.put(feedbackSession.getCourseId(), nonRespondentsEmails);
        }

        // non-respondents = all students and instructors - respondents
        nonRespondentsEmails.removeAll(respondents);
        return nonRespondentsEmails;
    }

    @SuppressWarnings("deprecation")
    private List<FeedbackSessionAttributes> getAllFeedbackSessions() {
        return fsDb.getAllFeedbackSessions();
    }

    /**
     * Returns a list of feedback sessions with start time from (now - numDays) and now.
     */
    private List<FeedbackSessionAttributes> getFeedbackSessionsWithStartDateNoOlderThan(int numDays) {
        Calendar startCal = Calendar.getInstance();
        startCal.add(Calendar.DAY_OF_YEAR, -1 * numDays);

        Date now = Calendar.getInstance().getTime();

        return getFeedbackSessionsWithStartTimeWithinRange(startCal.getTime(), now);
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsWithStartTimeWithinRange(
                                                    Date startDate, Date endDate) {
        List<FeedbackSessionAttributes> result = new ArrayList<>();

        Query query = PM.newQuery("SELECT FROM teammates.storage.entity.FeedbackSession "
                                + "WHERE this.startTime >= rangeStart && this.startTime < rangeEnd "
                                + "PARAMETERS java.util.Date rangeStart, "
                                + "java.util.Date rangeEnd");

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        Date curStart = startCal.getTime();
        Date curEnd = endCal.getTime();

        @SuppressWarnings("unchecked")
        List<FeedbackSession> feedbackSessions = (List<FeedbackSession>) query.execute(curStart, curEnd);
        for (FeedbackSession feedbackSession : feedbackSessions) {
            FeedbackSessionAttributes feedbackSessionAttributes = new FeedbackSessionAttributes(feedbackSession);
            result.add(feedbackSessionAttributes);
        }

        return result;
    }
}
