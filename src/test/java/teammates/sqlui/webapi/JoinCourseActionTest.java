package teammates.sqlui.webapi;

import org.testng.annotations.Test;

import teammates.ui.webapi.JoinCourseAction;

/**
 * SUT: {@link JoinCourseAction}.
 */
public class JoinCourseActionTest extends BaseActionTest<JoinCourseAction> {
    @Override
    protected String getActionUri() {
        return "/api/joinCourse";
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    public void test() {
        assert(true);
    }
}
