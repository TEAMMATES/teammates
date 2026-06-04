package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * SUT: {@link GetFeedbackSessionSubmittedGiverSetAction}.
 */
public class GetFeedbackSessionSubmittedGiverSetActionIT extends BaseActionIT<GetFeedbackSessionSubmittedGiverSetAction> {
    private DataBundle typicalBundle;

    @BeforeMethod
    protected void setUp() {
        typicalBundle = persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() {
        Instructor instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.getGoogleId();
        FeedbackSession fsa = typicalBundle.feedbackSessions.get("session1InCourse1");

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");
        verifyHttpParameterFailure();

        ______TS("Typical case");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa.getId().toString(),
        };

        GetFeedbackSessionSubmittedGiverSetAction pageAction = getAction(submissionParams);
        JsonResult result = getJsonResult(pageAction);

        FeedbackSessionSubmittedGiverSet output = (FeedbackSessionSubmittedGiverSet) result.getOutput();
        Set<UUID> expectedStudentGivers = Set.of(
                typicalBundle.students.get("student1InCourse1").getId(),
                typicalBundle.students.get("student2InCourse1").getId(),
                typicalBundle.students.get("student3InCourse1").getId());
        Set<UUID> expectedStudentNonGivers = typicalBundle.students.values()
                .stream()
                .filter(student -> student.getCourseId().equals(fsa.getCourseId()))
                .map(Student::getId)
                .filter(studentId -> !expectedStudentGivers.contains(studentId))
                .collect(Collectors.toSet());
        assertEquals(Set.of(
                typicalBundle.students.get("student1InCourse1").getId(),
                typicalBundle.students.get("student2InCourse1").getId(),
                typicalBundle.students.get("student3InCourse1").getId()),
                new HashSet<>(output.getStudentGivers()));
        Set<UUID> expectedInstructorNonGivers = typicalBundle.instructors.values()
                .stream()
                .filter(instructor -> instructor.getCourseId().equals(fsa.getCourseId()))
                .map(Instructor::getId)
                .collect(Collectors.toSet());
        assertEquals(Collections.emptySet(), output.getInstructorGivers());
        assertEquals(expectedStudentNonGivers, new HashSet<>(output.getStudentNonGivers()));
        assertEquals(expectedInstructorNonGivers, new HashSet<>(output.getInstructorNonGivers()));

        ______TS("Session with student questions only should not produce instructor non-givers");
        FeedbackSession fsb = typicalBundle.feedbackSessions.get("session2InTypicalCourse");
        String[] session2Params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsb.getId().toString(),
        };

        GetFeedbackSessionSubmittedGiverSetAction session2Action = getAction(session2Params);
        JsonResult session2Result = getJsonResult(session2Action);
        FeedbackSessionSubmittedGiverSet session2Output = (FeedbackSessionSubmittedGiverSet) session2Result.getOutput();

        Set<UUID> expectedSession2StudentGivers = Set.of(typicalBundle.students.get("student1InCourse1").getId());
        Set<UUID> expectedSession2StudentNonGivers = typicalBundle.students.values()
                .stream()
                .filter(student -> student.getCourseId().equals(fsb.getCourseId()))
                .map(Student::getId)
                .filter(studentId -> !expectedSession2StudentGivers.contains(studentId))
                .collect(Collectors.toSet());

        assertEquals(expectedSession2StudentGivers,
                new HashSet<>(session2Output.getStudentGivers()));
        assertEquals(expectedSession2StudentNonGivers,
                new HashSet<>(session2Output.getStudentNonGivers()));
        assertEquals(Collections.emptySet(), session2Output.getInstructorNonGivers());
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSession fsa = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ID, fsa.getId().toString(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(fsa.getCourse(), submissionParams);
    }
}
