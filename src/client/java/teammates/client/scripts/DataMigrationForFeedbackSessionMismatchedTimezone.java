package teammates.client.scripts;

import java.time.Instant;
import java.time.ZoneId;

import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.storage.entity.FeedbackSession;

/**
 * Script to set feedback sessions whose timezone does not match their corresponding course's.
 */
public class DataMigrationForFeedbackSessionMismatchedTimezone extends DataMigrationEntitiesBaseScript<FeedbackSession> {

    private final CoursesLogic coursesLogic = CoursesLogic.inst();
    private String currentCourseId;
    private CourseAttributes currentCourse;

    public static void main(String[] args) {
        new DataMigrationForFeedbackSessionMismatchedTimezone().doOperationRemotely();
    }

    @Override
    protected Query<FeedbackSession> getFilterQuery() {
        return ofy().load().type(FeedbackSession.class)
                .order("courseId");
    }

    @Override
    protected boolean isPreview() {
        return true;
    }

    @Override
    protected boolean isMigrationNeeded(FeedbackSession session) {
        if (!session.getCourseId().equals(currentCourseId)) {
            currentCourseId = session.getCourseId();
            currentCourse = coursesLogic.getCourse(currentCourseId);
        }

        return !session.getTimeZone().equals(currentCourse.getTimeZone());
    }

    @Override
    protected void migrateEntity(FeedbackSession session) {
        Instant now = Instant.now();
        int offsetOld = ZoneId.of(session.getTimeZone()).getRules().getOffset(now).getTotalSeconds();
        ZoneId courseTimeZone = ZoneId.of(currentCourse.getTimeZone());
        int offsetNew = courseTimeZone.getRules().getOffset(now).getTotalSeconds();
        long offsetDiffMillis = (offsetOld - offsetNew) * 1000L;

        session.setTimeZone(courseTimeZone.getId());

        if (!TimeHelper.isSpecialTime(session.getStartTime())) {
            session.setStartTime(session.getStartTime().plusMillis(offsetDiffMillis));
        }

        if (!TimeHelper.isSpecialTime(session.getEndTime())) {
            session.setEndTime(session.getEndTime().plusMillis(offsetDiffMillis));
        }

        if (!TimeHelper.isSpecialTime(session.getSessionVisibleFromTime())) {
            session.setSessionVisibleFromTime(session.getSessionVisibleFromTime().plusMillis(offsetDiffMillis));
        }

        if (!TimeHelper.isSpecialTime(session.getResultsVisibleFromTime())) {
            session.setResultsVisibleFromTime(session.getResultsVisibleFromTime().plusMillis(offsetDiffMillis));
        }

        saveEntityDeferred(session);
    }

}
