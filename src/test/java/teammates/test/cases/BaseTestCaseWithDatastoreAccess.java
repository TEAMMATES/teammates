package teammates.test.cases;

import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;

/**
 * Base class for all test cases which are allowed to access the Datastore via {@link BackDoor}.
 */
public abstract class BaseTestCaseWithDatastoreAccess extends BaseTestCase {
    
    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;
    private static final int OPERATION_RETRY_COUNT = 5;
    private static final int OPERATION_RETRY_DELAY_IN_MS = 1000;
    
    protected void verifyPresentInDatastore(DataBundle data) {
        Map<String, AccountAttributes> accounts = data.accounts;
        for (AccountAttributes account : accounts.values()) {
            verifyPresentInDatastore(account);
        }
        
        Map<String, InstructorAttributes> instructors = data.instructors;
        for (InstructorAttributes instructor : instructors.values()) {
            verifyPresentInDatastore(instructor);
        }
        
        Map<String, CourseAttributes> courses = data.courses;
        for (CourseAttributes course : courses.values()) {
            verifyPresentInDatastore(course);
        }
        
        Map<String, StudentAttributes> students = data.students;
        for (StudentAttributes student : students.values()) {
            verifyPresentInDatastore(student);
        }
    }
    
    private EntityAttributes getEntity(EntityAttributes expected) {
        if (expected instanceof AccountAttributes) {
            return getAccount((AccountAttributes) expected);
            
        } else if (expected instanceof CommentAttributes) {
            return getComment((CommentAttributes) expected);
            
        } else if (expected instanceof CourseAttributes) {
            return getCourse((CourseAttributes) expected);
            
        } else if (expected instanceof FeedbackQuestionAttributes) {
            return getFeedbackQuestion((FeedbackQuestionAttributes) expected);
            
        } else if (expected instanceof FeedbackResponseCommentAttributes) {
            return getFeedbackResponseComment((FeedbackResponseCommentAttributes) expected);
            
        } else if (expected instanceof FeedbackResponseAttributes) {
            return getFeedbackResponse((FeedbackResponseAttributes) expected);
            
        } else if (expected instanceof FeedbackSessionAttributes) {
            return getFeedbackSession((FeedbackSessionAttributes) expected);
            
        } else if (expected instanceof InstructorAttributes) {
            return getInstructor((InstructorAttributes) expected);
            
        } else if (expected instanceof StudentAttributes) {
            return getStudent((StudentAttributes) expected);
            
        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }
    
    protected void verifyAbsentInDatastore(EntityAttributes entity) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes actual = getEntity(entity);
        while (actual != null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(entity);
        }
        assertNull(actual);
    }
    
    protected void verifyPresentInDatastore(EntityAttributes expected) {
        int retryLimit = VERIFICATION_RETRY_COUNT;
        EntityAttributes actual = getEntity(expected);
        while (actual == null && retryLimit > 0) {
            retryLimit--;
            ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
            actual = getEntity(expected);
        }
        verifyEquals(expected, actual);
    }
    
    private void verifyEquals(EntityAttributes expected, EntityAttributes actual) {
        if (expected instanceof AccountAttributes) {
            AccountAttributes expectedAccount = ((AccountAttributes) expected).getCopy();
            AccountAttributes actualAccount = (AccountAttributes) actual;
            equalizeIrrelevantData(expectedAccount, actualAccount);
            assertEquals(JsonUtils.toJson(expectedAccount), JsonUtils.toJson(actualAccount));
            
        } else if (expected instanceof CommentAttributes) {
            CommentAttributes expectedComment = (CommentAttributes) expected;
            CommentAttributes actualComment = (CommentAttributes) actual;
            assertEquals(expectedComment.courseId, actualComment.courseId);
            assertEquals(expectedComment.giverEmail, actualComment.giverEmail);
            assertEquals(expectedComment.recipients, actualComment.recipients);
            assertEquals(expectedComment.commentText, actualComment.commentText);
            
        } else if (expected instanceof CourseAttributes) {
            CourseAttributes expectedCourse = (CourseAttributes) expected;
            CourseAttributes actualCourse = (CourseAttributes) actual;
            equalizeIrrelevantData(expectedCourse, actualCourse);
            assertEquals(JsonUtils.toJson(expectedCourse), JsonUtils.toJson(actualCourse));
            
        } else if (expected instanceof FeedbackQuestionAttributes) {
            FeedbackQuestionAttributes expectedFq = (FeedbackQuestionAttributes) expected;
            FeedbackQuestionAttributes actualFq = (FeedbackQuestionAttributes) actual;
            equalizeIrrelevantData(expectedFq, actualFq);
            assertEquals(JsonUtils.toJson(expectedFq), JsonUtils.toJson(actualFq));
            
        } else if (expected instanceof FeedbackResponseCommentAttributes) {
            FeedbackResponseCommentAttributes expectedFrc = (FeedbackResponseCommentAttributes) expected;
            FeedbackResponseCommentAttributes actualFrc = (FeedbackResponseCommentAttributes) actual;
            assertEquals(expectedFrc.courseId, actualFrc.courseId);
            assertEquals(expectedFrc.giverEmail, actualFrc.giverEmail);
            assertEquals(expectedFrc.feedbackSessionName, actualFrc.feedbackSessionName);
            assertEquals(expectedFrc.commentText, actualFrc.commentText);
            
        } else if (expected instanceof FeedbackResponseAttributes) {
            FeedbackResponseAttributes expectedFr = (FeedbackResponseAttributes) expected;
            FeedbackResponseAttributes actualFr = (FeedbackResponseAttributes) actual;
            equalizeIrrelevantData(expectedFr, actualFr);
            assertEquals(JsonUtils.toJson(expectedFr), JsonUtils.toJson(actualFr));
            
        } else if (expected instanceof FeedbackSessionAttributes) {
            FeedbackSessionAttributes expectedFs = ((FeedbackSessionAttributes) expected).getCopy();
            FeedbackSessionAttributes actualFs = (FeedbackSessionAttributes) actual;
            equalizeIrrelevantData(expectedFs, actualFs);
            assertEquals(JsonUtils.toJson(expectedFs), JsonUtils.toJson(actualFs));
            
        } else if (expected instanceof InstructorAttributes) {
            InstructorAttributes expectedInstructor = ((InstructorAttributes) expected).getCopy();
            InstructorAttributes actualInstructor = (InstructorAttributes) actual;
            equalizeIrrelevantData(expectedInstructor, actualInstructor);
            assertTrue(expectedInstructor.isEqualToAnotherInstructor(actualInstructor));
            
        } else if (expected instanceof StudentAttributes) {
            StudentAttributes expectedStudent = ((StudentAttributes) expected).getCopy();
            StudentAttributes actualStudent = (StudentAttributes) actual;
            equalizeIrrelevantData(expectedStudent, actualStudent);
            assertEquals(JsonUtils.toJson(expectedStudent), JsonUtils.toJson(actualStudent));
            
        } else {
            throw new RuntimeException("Unknown entity type!");
        }
    }
    
    protected AccountAttributes getAccount(AccountAttributes account) {
        return BackDoor.getAccount(account.googleId);
    }
    
    private void equalizeIrrelevantData(AccountAttributes expected, AccountAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.createdAt = actual.createdAt;
        
        if (actual.studentProfile == null) {
            expected.studentProfile = null;
        } else {
            if (expected.studentProfile == null) {
                expected.studentProfile = new StudentProfileAttributes();
                expected.studentProfile.googleId = actual.googleId;
            }
            expected.studentProfile.modifiedDate = actual.studentProfile.modifiedDate;
        }
    }
    
    protected CommentAttributes getComment(CommentAttributes comment) {
        throw new UnsupportedOperationException("Method not used");
    }
    
    protected CourseAttributes getCourse(CourseAttributes course) {
        return BackDoor.getCourse(course.getId());
    }
    
    private void equalizeIrrelevantData(CourseAttributes expected, CourseAttributes actual) {
        // Ignore time field as it is stamped at the time of creation in testing
        expected.createdAt = actual.createdAt;
    }
    
    protected FeedbackQuestionAttributes getFeedbackQuestion(FeedbackQuestionAttributes fq) {
        return BackDoor.getFeedbackQuestion(fq.courseId, fq.feedbackSessionName, fq.questionNumber);
    }
    
    private void equalizeIrrelevantData(FeedbackQuestionAttributes expected, FeedbackQuestionAttributes actual) {
        expected.setId(actual.getId());
    }
    
    protected FeedbackResponseCommentAttributes getFeedbackResponseComment(FeedbackResponseCommentAttributes frc) {
        throw new UnsupportedOperationException("Method not used");
    }
    
    protected FeedbackResponseAttributes getFeedbackResponse(FeedbackResponseAttributes fr) {
        return BackDoor.getFeedbackResponse(fr.feedbackQuestionId, fr.giver, fr.recipient);
    }
    
    private void equalizeIrrelevantData(FeedbackResponseAttributes expected, FeedbackResponseAttributes actual) {
        expected.setId(actual.getId());
    }
    
    protected FeedbackSessionAttributes getFeedbackSession(FeedbackSessionAttributes fs) {
        return BackDoor.getFeedbackSession(fs.getCourseId(), fs.getFeedbackSessionName());
    }
    
    private void equalizeIrrelevantData(FeedbackSessionAttributes expected, FeedbackSessionAttributes actual) {
        expected.setRespondingInstructorList(actual.getRespondingInstructorList());
        expected.setRespondingStudentList(actual.getRespondingStudentList());
    }
    
    protected InstructorAttributes getInstructor(InstructorAttributes instructor) {
        return BackDoor.getInstructorByEmail(instructor.email, instructor.courseId);
    }
    
    private void equalizeIrrelevantData(InstructorAttributes expected, InstructorAttributes actual) {
        // pretend keys match because the key is generated only before storing into database
        if (actual.key != null) {
            expected.key = actual.key;
        }
    }
    
    protected StudentAttributes getStudent(StudentAttributes student) {
        return BackDoor.getStudent(student.course, student.email);
    }
    
    private void equalizeIrrelevantData(StudentAttributes expected, StudentAttributes actual) {
        // For these fields, we consider null and "" equivalent.
        if (expected.googleId == null && actual.googleId.isEmpty()) {
            expected.googleId = "";
        }
        if (expected.team == null && actual.team.isEmpty()) {
            expected.team = "";
        }
        if (expected.comments == null && actual.comments.isEmpty()) {
            expected.comments = "";
        }
        
        // pretend keys match because the key is generated only before storing into database
        if (actual.key != null) {
            expected.key = actual.key;
        }
        
        expected.lastName = StringHelper.splitName(expected.name)[1];
    }
    
    protected void removeAndRestoreDataBundle(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        String backDoorOperationStatus = doRemoveAndRestoreDataBundle(testData);
        while (!backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS) && retryLimit > 0) {
            retryLimit--;
            print("Re-trying removeAndRestoreDataBundle - " + backDoorOperationStatus);
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            backDoorOperationStatus = doRemoveAndRestoreDataBundle(testData);
        }
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }
    
    protected String doRemoveAndRestoreDataBundle(DataBundle testData) {
        return BackDoor.removeAndRestoreDataBundle(testData);
    }
    
    protected void putDocuments(DataBundle testData) {
        int retryLimit = OPERATION_RETRY_COUNT;
        String backDoorOperationStatus = doPutDocuments(testData);
        while (!backDoorOperationStatus.equals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS) && retryLimit > 0) {
            retryLimit--;
            print("Re-trying putDocuments - " + backDoorOperationStatus);
            ThreadHelper.waitFor(OPERATION_RETRY_DELAY_IN_MS);
            backDoorOperationStatus = doPutDocuments(testData);
        }
        assertEquals(Const.StatusCodes.BACKDOOR_STATUS_SUCCESS, backDoorOperationStatus);
    }
    
    protected String doPutDocuments(DataBundle testData) {
        return BackDoor.putDocuments(testData);
    }
    
}
