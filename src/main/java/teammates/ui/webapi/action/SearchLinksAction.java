package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.SearchLinksInstructorData;
import teammates.ui.webapi.output.SearchLinksResult;
import teammates.ui.webapi.output.SearchLinksStudentData;

/**
 * Searches for Links.
 */
public class SearchLinksAction extends Action {

    private Set<String> courseIds = new HashSet<>();
    private Map<String, String> courseIdToInstructorGoogleIdMap = new HashMap<>();
    private Map<String, String> courseIdToInstituteMap = new HashMap<>();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admin can get links
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        List<StudentAttributes> students = logic.searchStudentsInWholeSystem(searchKey).studentList;
        List<InstructorAttributes> instructors = logic.searchInstructorsInWholeSystem(searchKey).instructorList;

        populateCourseIds(students, instructors);
        populateCourseIdToInstituteMap();

        List<SearchLinksStudentData> studentsBundle = getStudentsBundle(students);
        List<SearchLinksInstructorData> instructorsBundle = getInstructorsBundle(instructors);
        SearchLinksResult links = new SearchLinksResult(studentsBundle, instructorsBundle);

        return new JsonResult(links);
    }

    private void populateCourseIds(List<StudentAttributes> students, List<InstructorAttributes> instructors) {
        for (StudentAttributes student : students) {
            if (student.course != null) {
                courseIds.add(student.course);
            }
        }
        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId != null) {
                courseIds.add(instructor.courseId);
            }
        }
    }

    private void populateCourseIdToInstituteMap() {
        for (String courseId : courseIds) {
            String instructorForCourseGoogleId = findAvailableInstructorGoogleIdForCourse(courseId);
            AccountAttributes account = logic.getAccount(instructorForCourseGoogleId);
            if (account == null) {
                continue;
            }

            String institute = StringHelper.isEmpty(account.institute) ? "None" : account.institute;
            courseIdToInstituteMap.put(courseId, institute);
        }
    }

    /**
     * Finds the googleId of a registered instructor with co-owner privileges.
     * If there is no such instructor, finds the googleId of a registered
     * instructor with the privilege to modify instructors.
     *
     * @param courseId
     *            the ID of the course
     * @return the googleId of a suitable instructor if found, otherwise an
     *         empty string
     */
    private String findAvailableInstructorGoogleIdForCourse(String courseId) {
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

        for (InstructorAttributes instructor : instructorList) {
            if (instructor.isRegistered()
                    && (instructor.hasCoownerPrivileges()
                    || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR))) {
                courseIdToInstructorGoogleIdMap.put(courseId, instructor.googleId);
                return instructor.googleId;
            }

        }

        return "";
    }

    private List<SearchLinksStudentData> getStudentsBundle(List<StudentAttributes> students) {
        List<SearchLinksStudentData> studentsBundle = new ArrayList<>();

        for (StudentAttributes student : students) {
            if (student.email == null) {
                continue;
            }

            SearchLinksStudentData studentData = new SearchLinksStudentData();

            if (student.course != null
                    && !StringHelper.isEmpty(courseIdToInstructorGoogleIdMap.get(student.course))) {

                studentData.setRecordsPageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                        .withCourseId(student.course)
                        .withStudentEmail(student.email)
                        .withUserId(courseIdToInstructorGoogleIdMap.get(student.course))
                        .toAbsoluteString());
            }

            if (student.googleId != null) {
                studentData.setManageAccountLink(Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                        .withInstructorId(student.googleId)
                        .toString());
                studentData.setHomePageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.STUDENT_HOME_PAGE)
                        .withUserId(student.getGoogleId())
                        .toString());
            }

            studentData.setEmail(student.email);
            studentData.setCourseJoinLink(Config.getFrontEndAppUrl(student.getRegistrationUrl()).toAbsoluteString());
            studentsBundle.add(studentData);
        }

        return studentsBundle;
    }

    private List<SearchLinksInstructorData> getInstructorsBundle(List<InstructorAttributes> instructors) {
        List<SearchLinksInstructorData> instructorsBundle = new ArrayList<>();

        for (InstructorAttributes instructor : instructors) {
            if (instructor.email == null) {
                continue;
            }

            SearchLinksInstructorData instructorData = new SearchLinksInstructorData();

            instructorData.setEmail(instructor.email);
            instructorData.setManageAccountLink(Config.getFrontEndAppUrl(Const.WebPageURIs.ADMIN_ACCOUNTS_PAGE)
                    .withInstructorId(instructor.googleId)
                    .toString());
            instructorData.setHomePageLink(Config.getFrontEndAppUrl(Const.WebPageURIs.INSTRUCTOR_HOME_PAGE)
                    .withUserId(instructor.googleId)
                    .toAbsoluteString());
            instructorData.setCourseJoinLink(Config.getFrontEndAppUrl(Const.WebPageURIs.JOIN_PAGE)
                    .withRegistrationKey(StringHelper.encrypt(instructor.key))
                    .withParam(Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR)
                    .toAbsoluteString());
            instructorsBundle.add(instructorData);
        }

        return instructorsBundle;
    }
}
