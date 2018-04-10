package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.ui.pagedata.AdminSearchPageData;

public class AdminSearchPageAction extends Action {

    private static final String OPEN_CLOSE_DATES_SESSION_TEMPLATE = "[%s - %s]";

    private Map<String, String> tempCourseIdToInstituteMap = new HashMap<>();
    private Map<String, String> tempCourseIdToInstructorGoogleIdMap = new HashMap<>();

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        String searchKey = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        String searchButtonHit = getRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_BUTTON_HIT);

        AdminSearchPageData data = new AdminSearchPageData(account, sessionToken);

        if (searchKey == null || searchKey.trim().isEmpty()) {

            if (searchButtonHit == null) {
                statusToAdmin = "AdminSearchPage Page Load";
            } else {
                statusToUser.add(new StatusMessage("Search key cannot be empty", StatusMessageColor.WARNING));
                statusToAdmin = "Invalid Search: Search key cannot be empty";
                isError = true;
            }
            return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }

        data.searchKey = SanitizationHelper.sanitizeForHtml(searchKey);

        data.studentResultBundle = logic.searchStudentsInWholeSystem(searchKey);

        data = putFeedbackSessionLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentHomePageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentRecordsPageLinkIntoMap(data.studentResultBundle.studentList, data);
        data = putStudentInstituteIntoMap(data.studentResultBundle.studentList, data);

        data.instructorResultBundle = logic.searchInstructorsInWholeSystem(searchKey);
        data = putInstructorInstituteIntoMap(data.instructorResultBundle.instructorList, data);
        data = putInstructorHomePageLinkIntoMap(data.instructorResultBundle.instructorList, data);
        data = putInstructorCourseJoinLinkIntoMap(data.instructorResultBundle.instructorList, data);

        data = putCourseNameIntoMap(data.studentResultBundle.studentList,
                                    data.instructorResultBundle.instructorList,
                                    data);

        int numOfResults = data.studentResultBundle.numberOfResults
                           + data.instructorResultBundle.numberOfResults;

        if (numOfResults > 0) {
            statusToUser.add(new StatusMessage("Total results found: " + numOfResults, StatusMessageColor.INFO));
            statusToAdmin = "Search Key: " + data.searchKey + "<br>" + "Total results found: " + numOfResults;
            isError = false;
        } else {
            statusToUser.add(new StatusMessage("No result found, please try again", StatusMessageColor.WARNING));
            statusToAdmin = "Search Key: " + data.searchKey + "<br>" + "No result found";
            isError = true;
        }

        data.init();
        return createShowPageResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }

    private AdminSearchPageData putCourseNameIntoMap(List<StudentAttributes> students,
                                                     List<InstructorAttributes> instructors,
                                                     AdminSearchPageData data) {
        for (StudentAttributes student : students) {
            if (student.course != null && !data.courseIdToCourseNameMap.containsKey(student.course)) {
                CourseAttributes course = logic.getCourse(student.course);
                if (course != null) {
                    //TODO: [CourseAttribute] remove desanitization after data migration
                    data.courseIdToCourseNameMap.put(
                            student.course, SanitizationHelper.desanitizeIfHtmlSanitized(course.getName()));
                }
            }
        }

        for (InstructorAttributes instructor : instructors) {
            if (instructor.courseId != null && !data.courseIdToCourseNameMap.containsKey(instructor.courseId)) {
                CourseAttributes course = logic.getCourse(instructor.courseId);
                if (course != null) {
                    //TODO: [CourseAttribute] remove desanitization after data migration
                    data.courseIdToCourseNameMap.put(
                            instructor.courseId, SanitizationHelper.desanitizeIfHtmlSanitized(course.getName()));
                }
            }
        }

        return data;
    }

    private AdminSearchPageData putInstructorCourseJoinLinkIntoMap(List<InstructorAttributes> instructors,
                                                                   AdminSearchPageData data) {

        for (InstructorAttributes instructor : instructors) {

            String googleIdOfAlreadyRegisteredInstructor = findAvailableInstructorGoogleIdForCourse(instructor.courseId);

            if (!googleIdOfAlreadyRegisteredInstructor.isEmpty()) {
                String joinLinkWithoutInstitute = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                                .withRegistrationKey(StringHelper.encrypt(instructor.key))
                                                .toAbsoluteString();
                data.instructorCourseJoinLinkMap.put(instructor.getIdentificationString(),
                                                     joinLinkWithoutInstitute);
            }

        }

        return data;
    }

    private AdminSearchPageData putInstructorInstituteIntoMap(List<InstructorAttributes> instructors,
                                                              AdminSearchPageData data) {
        for (InstructorAttributes instructor : instructors) {

            if (tempCourseIdToInstituteMap.get(instructor.courseId) != null) {
                data.instructorInstituteMap.put(instructor.getIdentificationString(),
                                                tempCourseIdToInstituteMap.get(instructor.courseId));
                continue;
            }

            String googleId = findAvailableInstructorGoogleIdForCourse(instructor.courseId);

            AccountAttributes account = logic.getAccount(googleId);
            if (account == null) {
                continue;
            }

            String institute = account.institute.trim().isEmpty() ? "None" : account.institute;

            tempCourseIdToInstituteMap.put(instructor.courseId, institute);
            data.instructorInstituteMap.put(instructor.getIdentificationString(), institute);
        }

        return data;
    }

    private AdminSearchPageData putInstructorHomePageLinkIntoMap(List<InstructorAttributes> instructors,
                                                                 AdminSearchPageData data) {

        for (InstructorAttributes instructor : instructors) {

            if (instructor.googleId == null) {
                continue;
            }

            String curLink = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE,
                                                        Const.ParamsNames.USER_ID,
                                                        instructor.googleId);

            data.instructorHomePageLinkMap.put(instructor.googleId, curLink);
        }

        return data;
    }

    private AdminSearchPageData putStudentInstituteIntoMap(List<StudentAttributes> students, AdminSearchPageData data) {
        for (StudentAttributes student : students) {

            if (tempCourseIdToInstituteMap.get(student.course) != null) {
                data.studentInstituteMap.put(student.getIdentificationString(),
                                             tempCourseIdToInstituteMap.get(student.course));
                continue;
            }

            String instructorForCourseGoogleId = findAvailableInstructorGoogleIdForCourse(student.course);

            AccountAttributes account = logic.getAccount(instructorForCourseGoogleId);
            if (account == null) {
                continue;
            }

            String institute = account.institute.trim().isEmpty() ? "None" : account.institute;

            tempCourseIdToInstituteMap.put(student.course, institute);

            data.studentInstituteMap.put(student.getIdentificationString(), institute);
        }

        return data;
    }

    private AdminSearchPageData putStudentHomePageLinkIntoMap(List<StudentAttributes> students, AdminSearchPageData data) {

        for (StudentAttributes student : students) {

            if (student.googleId == null) {
                continue;
            }

            String curLink = Url.addParamToUrl(Const.ActionURIs.STUDENT_HOME_PAGE,
                                                        Const.ParamsNames.USER_ID,
                                                        student.googleId);

            data.studentIdToHomePageLinkMap.put(student.googleId, curLink);
        }

        return data;
    }

    private AdminSearchPageData putStudentRecordsPageLinkIntoMap(List<StudentAttributes> students,
                                                                 AdminSearchPageData data) {

        for (StudentAttributes student : students) {

            if (student.course == null || student.email == null) {
                continue;
            }

            String curLink = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE,
                                                        Const.ParamsNames.COURSE_ID,
                                                        student.course);
            curLink = Url.addParamToUrl(curLink, Const.ParamsNames.STUDENT_EMAIL, student.email);
            String availableGoogleId = findAvailableInstructorGoogleIdForCourse(student.course);

            if (!availableGoogleId.isEmpty()) {
                curLink = Url.addParamToUrl(curLink, Const.ParamsNames.USER_ID, availableGoogleId);
                data.studentRecordsPageLinkMap.put(student.getIdentificationString(), curLink);
            }
        }

        return data;
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

        if (tempCourseIdToInstructorGoogleIdMap.get(courseId) != null) {
            return tempCourseIdToInstructorGoogleIdMap.get(courseId);
        }

        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);

        if (instructorList.isEmpty()) {
            return "";
        }

        for (InstructorAttributes instructor : instructorList) {

            if (instructor.isRegistered() && instructor.hasCoownerPrivileges()) {
                tempCourseIdToInstructorGoogleIdMap.put(courseId, instructor.googleId);
                return instructor.googleId;
            }
        }

        for (InstructorAttributes instructor : instructorList) {

            if (instructor.isRegistered()
                    && instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {

                tempCourseIdToInstructorGoogleIdMap.put(courseId, instructor.googleId);
                return instructor.googleId;
            }
        }

        return "";
    }

    private AdminSearchPageData putFeedbackSessionLinkIntoMap(List<StudentAttributes> students,
                                                              AdminSearchPageData rawData) {

        AdminSearchPageData processedData = rawData;

        for (StudentAttributes student : students) {
            List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(student.course);

            for (FeedbackSessionAttributes fsa : feedbackSessions) {
                processedData = extractDataFromFeedbackSession(fsa, processedData, student);
            }
        }

        return processedData;

    }

    private AdminSearchPageData extractDataFromFeedbackSession(FeedbackSessionAttributes fsa,
                                                               AdminSearchPageData data,
                                                               StudentAttributes student) {

        String submitUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE)
                               .withCourseId(student.course)
                               .withSessionName(fsa.getFeedbackSessionName())
                               .withRegistrationKey(StringHelper.encrypt(student.key))
                               .withStudentEmail(student.email)
                               .toAbsoluteString();

        String openCloseDateFragment = generateOpenCloseDateInfo(fsa.getStartTimeString(), fsa.getEndTimeString());

        if (fsa.isOpened()) {
            if (data.studentOpenFeedbackSessionLinksMap.get(student.getIdentificationString()) == null) {
                List<String> submitUrlList = new ArrayList<>();
                submitUrlList.add(submitUrl);
                data.studentOpenFeedbackSessionLinksMap.put(student.getIdentificationString(), submitUrlList);
            } else {
                data.studentOpenFeedbackSessionLinksMap.get(student.getIdentificationString()).add(submitUrl);
            }

            data.feedbackSessionLinkToNameMap.put(submitUrl, fsa.getFeedbackSessionName() + " "
                    + openCloseDateFragment);

        } else {
            if (data.studentUnOpenedFeedbackSessionLinksMap.get(student.getIdentificationString()) == null) {
                List<String> submitUrlList = new ArrayList<>();
                submitUrlList.add(submitUrl);
                data.studentUnOpenedFeedbackSessionLinksMap.put(student.getIdentificationString(), submitUrlList);
            } else {
                data.studentUnOpenedFeedbackSessionLinksMap.get(student.getIdentificationString()).add(submitUrl);
            }

            data.feedbackSessionLinkToNameMap.put(submitUrl, fsa.getFeedbackSessionName() + " (Currently Not Open) "
                    + openCloseDateFragment);
        }

        String viewResultUrl = Config.getAppUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                   .withCourseId(student.course)
                                   .withSessionName(fsa.getFeedbackSessionName())
                                   .withRegistrationKey(StringHelper.encrypt(student.key))
                                   .withStudentEmail(student.email)
                                   .toAbsoluteString();

        if (fsa.isPublished()) {
            if (data.studentPublishedFeedbackSessionLinksMap.get(student.getIdentificationString()) == null) {
                List<String> viewResultUrlList = new ArrayList<>();
                viewResultUrlList.add(viewResultUrl);
                data.studentPublishedFeedbackSessionLinksMap.put(student.getIdentificationString(), viewResultUrlList);
            } else {
                data.studentPublishedFeedbackSessionLinksMap.get(student.getIdentificationString()).add(viewResultUrl);
            }

            data.feedbackSessionLinkToNameMap.put(viewResultUrl, fsa.getFeedbackSessionName() + " (Published) "
                    + openCloseDateFragment);
        }
        return data;
    }

    private String generateOpenCloseDateInfo(String startTime, String endTime) {
        return String.format(OPEN_CLOSE_DATES_SESSION_TEMPLATE, startTime, endTime);
    }

}
