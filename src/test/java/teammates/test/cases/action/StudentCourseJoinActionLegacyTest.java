package teammates.test.cases.action;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.core.StudentsLogic;
import teammates.ui.controller.StudentCourseJoinAction;

/**
 * To test legacy course join link for unregistered students.
 * SUT: {@link StudentCourseJoinAction}.
 */
public class StudentCourseJoinActionLegacyTest extends BaseActionTest {

    @BeforeClass
    public void classSetup() throws Exception {
        addUnregStudentToCourse1();
    }

    @AfterClass
    public void classTearDown() {
        StudentsLogic.inst().deleteStudentCascade("idOfTypicalCourse1", "student6InCourse1@gmail.tmt");
    }

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.STUDENT_COURSE_JOIN;
    }

    @Override
    protected StudentCourseJoinAction getAction(String... params) {
        return (StudentCourseJoinAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    protected void testExecuteAndPostProcess() throws Exception {
        // This is not required
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        StudentAttributes unregStudent1 = typicalBundle.students.get("student2InUnregisteredCourse");
        String key = StudentsLogic.inst().getStudentForEmail(unregStudent1.course, unregStudent1.email).key;
        String[] submissionParams = new String[] {
                Const.ParamsNames.REGKEY, StringHelper.encrypt(key)
        };

        verifyAccessibleWithoutLogin(submissionParams);
        verifyAccessibleForUnregisteredUsers(submissionParams);
        verifyAccessibleForStudents(submissionParams);
        verifyAccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);
    }
}
