package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.StatusMessages;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorHomeCourseAjaxPageData;
import teammates.ui.pagedata.InstructorHomePageData;

public class InstructorHomePageAction extends Action {
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        if (!account.isInstructor && isPersistenceIssue()) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE,
                                               StatusMessageColor.WARNING));
            statusToAdmin = "instructorHome " + Const.StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE;
            return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, new InstructorHomePageData(account, sessionToken));
        }

        gateKeeper.verifyInstructorPrivileges(account);

        String courseToLoad = getRequestParamValue(Const.ParamsNames.COURSE_TO_LOAD);
        return courseToLoad == null ? loadPage() : loadCourse(courseToLoad);
    }

    private ActionResult loadCourse(String courseToLoad) throws EntityDoesNotExistException {
        int index = Integer.parseInt(getRequestParamValue("index"));

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseToLoad, account.googleId);

        CourseSummaryBundle course = logic.getCourseSummaryWithFeedbackSessions(instructor);
        FeedbackSessionAttributes.sortFeedbackSessionsByCreationTimeDescending(course.feedbackSessions);

        InstructorHomeCourseAjaxPageData data = new InstructorHomeCourseAjaxPageData(account, sessionToken);
        data.init(index, course, instructor);

        statusToAdmin = "instructorHome Course Load:<br>" + courseToLoad;

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME_AJAX_COURSE_TABLE, data);
    }

    private ActionResult loadPage() {
        boolean shouldOmitArchived = true;
        Map<String, CourseSummaryBundle> courses = logic.getCourseSummariesWithoutStatsForInstructor(
                                                                 account.googleId, shouldOmitArchived);

        ArrayList<CourseSummaryBundle> courseList = new ArrayList<>(courses.values());

        String sortCriteria = getSortCriteria();
        sortCourse(courseList, sortCriteria);

        InstructorHomePageData data = new InstructorHomePageData(account, sessionToken);
        data.init(courseList, sortCriteria);

        if (logic.isNewInstructor(account.googleId)) {
            statusToUser.add(new StatusMessage(StatusMessages.HINT_FOR_NEW_INSTRUCTOR, StatusMessageColor.INFO));
        }
        statusToAdmin = "instructorHome Page Load<br>" + "Total Courses: " + courseList.size();

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_HOME, data);
    }

    private String getSortCriteria() {
        String sortCriteria = getRequestParamValue(Const.ParamsNames.COURSE_SORTING_CRITERIA);
        if (sortCriteria == null) {
            sortCriteria = Const.DEFAULT_SORT_CRITERIA;
        }

        return sortCriteria;
    }

    private void sortCourse(List<CourseSummaryBundle> courseList, String sortCriteria) {
        switch (sortCriteria) {
        case Const.SORT_BY_COURSE_ID:
            CourseSummaryBundle.sortSummarizedCoursesByCourseId(courseList);
            break;
        case Const.SORT_BY_COURSE_NAME:
            CourseSummaryBundle.sortSummarizedCoursesByCourseName(courseList);
            break;
        case Const.SORT_BY_COURSE_CREATION_DATE:
            CourseSummaryBundle.sortSummarizedCoursesByCreationDate(courseList);
            break;
        default:
            Assumption.fail("Invalid course sorting criteria.");
            break;
        }
    }
}
