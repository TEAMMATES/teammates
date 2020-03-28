package teammates.test.cases.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetCourseSectionNamesAction;

/**
 * Access Control Test: {@link GetCourseSectionNamesAction}.
 */
public class GetCourseSectionNamesActionTest extends BaseActionTest<GetCourseSectionNamesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_SECTIONS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() throws Exception {
        //TODO: Add execution test
    }

    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes typicalCourse1 = typicalBundle.courses.get("typicalCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, typicalCourse1.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
    }
}
