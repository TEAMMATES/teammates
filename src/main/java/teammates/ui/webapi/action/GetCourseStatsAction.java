package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.ApiOutput;

/**
 * Action: gets the stats of a course for the instructor.
 */
public class GetCourseStatsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseDetailsBundle courseDetails;
        try {
            courseDetails = logic.getCourseDetails(courseId);
        } catch (EntityDoesNotExistException e) {
            return new JsonResult("No course with given id.", HttpStatus.SC_NOT_FOUND);
        }

        int sections = courseDetails.getStats().getSectionsTotal();
        int teams = courseDetails.getStats().getTeamsTotal();
        int students = courseDetails.getStats().getStudentsTotal();
        int unregistered = courseDetails.getStats().getUnregisteredTotal();

        CourseStats output = new CourseStats(sections, teams, students, unregistered);
        return new JsonResult(output);
    }

    /**
     * Output format for {@link GetCourseStatsAction}.
     */
    public static class CourseStats extends ApiOutput {

        private final int sectionsTotal;
        private final int teamsTotal;
        private final int studentsTotal;
        private final int unregisteredTotal;

        public CourseStats(int sections, int teams, int students, int unregistered) {
            this.sectionsTotal = sections;
            this.teamsTotal = teams;
            this.studentsTotal = students;
            this.unregisteredTotal = unregistered;
        }

        public int getSectionsTotal() {
            return sectionsTotal;
        }

        public int getTeamsTotal() {
            return teamsTotal;
        }

        public int getStudentsTotal() {
            return studentsTotal;
        }

        public int getUnregisteredTotal() {
            return unregisteredTotal;
        }
    }
}
