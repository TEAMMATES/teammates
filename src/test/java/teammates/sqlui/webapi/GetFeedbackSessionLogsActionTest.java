package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.FeedbackSessionLogData;
import teammates.ui.output.FeedbackSessionLogEntryData;
import teammates.ui.output.FeedbackSessionLogsData;
import teammates.ui.webapi.GetFeedbackSessionLogsAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetFeedbackSessionLogsAction}.
 */
public class GetFeedbackSessionLogsActionTest extends BaseActionTest<GetFeedbackSessionLogsAction> {

    private Course course;

    private Student student1;
    private Student student2;

    private FeedbackSession fs1;

    private long startTime;
    private long endTime;

    private String googleId = "google-id";

    @Override
    String getActionUri() {
        return Const.ResourceURIs.SESSION_LOGS;
    }

    @Override
    String getRequestMethod() {
        return GET;
    }

    @BeforeMethod
    void setUp() {
        FeedbackSession fs2;
        endTime = Instant.now().toEpochMilli();
        startTime = endTime - (Const.LOGS_RETENTION_PERIOD.toDays() - 1) * 24 * 60 * 60 * 1000;

        course = getTypicalCourse();

        student1 = getTypicalStudent();
        student1.setEmail("student1@teammates.tmt");
        student1.setTeam(getTypicalTeam());

        student2 = getTypicalStudent();
        student2.setEmail("student2@teammates.tmt");
        student2.setTeam(getTypicalTeam());

        fs1 = getTypicalFeedbackSessionForCourse(course);
        fs1.setName("fs1");
        fs1.setCreatedAt(Instant.now());

        fs2 = getTypicalFeedbackSessionForCourse(course);
        fs2.setName("fs2");
        fs2.setCreatedAt(Instant.now());

        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getFeedbackSession(fs1.getId())).thenReturn(fs1);
        when(mockLogic.getStudent(student1.getId())).thenReturn(student1);
        when(mockLogic.getStudent(student2.getId())).thenReturn(student2);

        List<FeedbackSession> feedbackSessions = new ArrayList<>();
        feedbackSessions.add(fs1);
        feedbackSessions.add(fs2);
        when(mockLogic.getFeedbackSessionsForCourse(course.getId())).thenReturn(feedbackSessions);

        FeedbackSessionLog student1Session1Log1 = new FeedbackSessionLog(student1, fs1, FeedbackSessionLogType.ACCESS,
                Instant.ofEpochMilli(startTime));
        FeedbackSessionLog student1Session2Log1 = new FeedbackSessionLog(student1, fs2, FeedbackSessionLogType.ACCESS,
                Instant.ofEpochMilli(startTime + 1000));
        FeedbackSessionLog student1Session2Log2 = new FeedbackSessionLog(student1, fs2,
                FeedbackSessionLogType.SUBMISSION, Instant.ofEpochMilli(startTime + 2000));
        FeedbackSessionLog student2Session1Log1 = new FeedbackSessionLog(student2, fs1, FeedbackSessionLogType.ACCESS,
                Instant.ofEpochMilli(startTime + 3000));
        FeedbackSessionLog student2Session1Log2 = new FeedbackSessionLog(student2, fs1,
                FeedbackSessionLogType.SUBMISSION, Instant.ofEpochMilli(startTime + 4000));

        List<FeedbackSessionLog> allLogsInCourse = new ArrayList<>();
        allLogsInCourse.add(student1Session1Log1);
        allLogsInCourse.add(student1Session2Log1);
        allLogsInCourse.add(student1Session2Log2);
        allLogsInCourse.add(student2Session1Log1);
        allLogsInCourse.add(student2Session1Log2);
        when(mockLogic.getOrderedFeedbackSessionLogs(course.getId(), null, null, Instant.ofEpochMilli(startTime),
                Instant.ofEpochMilli(endTime))).thenReturn(allLogsInCourse);

        List<FeedbackSessionLog> student1Logs = new ArrayList<>();
        student1Logs.add(student1Session1Log1);
        student1Logs.add(student1Session2Log1);
        student1Logs.add(student1Session2Log2);
        when(mockLogic.getOrderedFeedbackSessionLogs(course.getId(), student1.getId(), null,
                Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime))).thenReturn(student1Logs);

        List<FeedbackSessionLog> fs1Logs = new ArrayList<>();
        fs1Logs.add(student1Session1Log1);
        fs1Logs.add(student2Session1Log1);
        fs1Logs.add(student2Session1Log2);
        when(mockLogic.getOrderedFeedbackSessionLogs(course.getId(), null, fs1.getId(),
                Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime))).thenReturn(fs1Logs);

        List<FeedbackSessionLog> student1Fs1Logs = new ArrayList<>();
        student1Fs1Logs.add(student1Session1Log1);
        when(mockLogic.getOrderedFeedbackSessionLogs(course.getId(), student1.getId(), fs1.getId(),
                Instant.ofEpochMilli(startTime), Instant.ofEpochMilli(endTime))).thenReturn(student1Fs1Logs);
    }

    @Test
    protected void testExecute() {
        JsonResult actionOutput;

        ______TS("Failure case: not enough parameters");
        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, course.getId());

        verifyHttpParameterFailure(
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime));
        verifyHttpParameterFailure(
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime));

        ______TS("Failure case: invalid course id");
        String[] paramsInvalid1 = {
                Const.ParamsNames.COURSE_ID, "fake-course-id",
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyEntityNotFound(paramsInvalid1);

        ______TS("Failure case: invalid student id");
        String[] paramsInvalid2 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_SQL_ID, "00000000-0000-0000-0000-000000000000",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyEntityNotFound(paramsInvalid2);

        ______TS("Failure case: invalid start or end times");
        String[] paramsInvalid3 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, "abc",
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        verifyHttpParameterFailure(paramsInvalid3);

        String[] paramsInvalid4 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, " ",
        };
        verifyHttpParameterFailure(paramsInvalid4);

        ______TS("Success case: should group by feedback session");
        String[] paramsSuccessful1 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };

        actionOutput = getJsonResult(getAction(paramsSuccessful1));

        FeedbackSessionLogsData fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        List<FeedbackSessionLogData> fsLogs = fslData.getFeedbackSessionLogs();

        // Course has 2 feedback sessions
        assertEquals(fsLogs.size(), 2);

        List<FeedbackSessionLogEntryData> fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();
        List<FeedbackSessionLogEntryData> fsLogEntries2 = fsLogs.get(1).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 3);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(1).getStudentData().getEmail(), student2.getEmail());
        assertEquals(fsLogEntries1.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(2).getStudentData().getEmail(), student2.getEmail());
        assertEquals(fsLogEntries1.get(2).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        assertEquals(fsLogEntries2.size(), 2);
        assertEquals(fsLogEntries2.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries2.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries2.get(1).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries2.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should accept optional student id");
        String[] paramsSuccessful2 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful2));
        fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        fsLogs = fslData.getFeedbackSessionLogs();

        assertEquals(fsLogs.size(), 2);

        fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();
        fsLogEntries2 = fsLogs.get(1).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 1);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);

        assertEquals(fsLogEntries2.size(), 2);
        assertEquals(fsLogEntries2.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries2.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries2.get(1).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries2.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should accept optional feedback session");
        String[] paramsSuccessful3 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful3));
        fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        fsLogs = fslData.getFeedbackSessionLogs();

        assertEquals(fsLogs.size(), 2);
        assertEquals(fsLogs.get(1).getFeedbackSessionLogEntries().size(), 0);

        fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 3);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(1).getStudentData().getEmail(), student2.getEmail());
        assertEquals(fsLogEntries1.get(1).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);
        assertEquals(fsLogEntries1.get(2).getStudentData().getEmail(), student2.getEmail());
        assertEquals(fsLogEntries1.get(2).getFeedbackSessionLogType(), FeedbackSessionLogType.SUBMISSION);

        ______TS("Success case: should accept all optional params");
        String[] paramsSuccessful4 = {
                Const.ParamsNames.COURSE_ID, course.getId(),
                Const.ParamsNames.STUDENT_SQL_ID, student1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_ID, fs1.getId().toString(),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_STARTTIME, String.valueOf(startTime),
                Const.ParamsNames.FEEDBACK_SESSION_LOG_ENDTIME, String.valueOf(endTime),
        };
        actionOutput = getJsonResult(getAction(paramsSuccessful4));
        fslData = (FeedbackSessionLogsData) actionOutput.getOutput();
        fsLogs = fslData.getFeedbackSessionLogs();

        assertEquals(fsLogs.size(), 2);
        assertEquals(fsLogs.get(1).getFeedbackSessionLogEntries().size(), 0);

        fsLogEntries1 = fsLogs.get(0).getFeedbackSessionLogEntries();

        assertEquals(fsLogEntries1.size(), 1);
        assertEquals(fsLogEntries1.get(0).getStudentData().getEmail(), student1.getEmail());
        assertEquals(fsLogEntries1.get(0).getFeedbackSessionLogType(), FeedbackSessionLogType.ACCESS);

        // TODO: if we restrict the range from start to end time, it should be tested
        // here as well
    }

    @Test
    void testSpecificAccessControl_instructorWithInvalidPermission_cannotAccess() {

        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, new InstructorPrivileges());

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCannotAccess(params);
    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, true);
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, true);
        instructorPrivileges.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        loginAsStudent(googleId);
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}
