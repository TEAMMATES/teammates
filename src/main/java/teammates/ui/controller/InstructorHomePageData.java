package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.CourseTable;
import teammates.ui.template.ElementTag;

public class InstructorHomePageData extends PageData {

    private static final int MAX_CLOSED_SESSION_STATS = 3;

    private int unarchivedCoursesCount;
    private List<CourseTable> courseTables;
    private String sortCriteria;

    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(List<CourseSummaryBundle> courseList, String sortCriteria,
            Map<String,InstructorAttributes> instructors, Map<String,Integer> numberOfPendingComments) {
        this.sortCriteria = sortCriteria;
        setUnarchivedCoursesCount(courseList, instructors);
        setCourseTables(courseList, instructors, numberOfPendingComments);
    }
    
    public String getSortCriteria() {
        return sortCriteria;
    }
    
    public int getUnarchivedCoursesCount() {
        return unarchivedCoursesCount;
    }
    
    public List<CourseTable> getCourseTables() {
        return courseTables;
    }
    
    private void setUnarchivedCoursesCount(List<CourseSummaryBundle> courses,
                                           Map<String, InstructorAttributes> instructors) {
        unarchivedCoursesCount = 0;
        for (CourseSummaryBundle courseDetails : courses) {
            InstructorAttributes instructor = instructors.get(courseDetails.course.id);
            boolean notArchived = instructor.isArchived == null || !instructor.isArchived;
            if (notArchived) {
                unarchivedCoursesCount++;
            }
        }
    }
    
    private void setCourseTables(List<CourseSummaryBundle> courses,
                                 Map<String, InstructorAttributes> instructors,
                                 Map<String, Integer> numberOfPendingComments) {
        courseTables = new ArrayList<CourseTable>(); 
        for (CourseSummaryBundle courseDetails : courses) {
            String courseId = courseDetails.course.id;
            InstructorAttributes instructor = instructors.get(courseId);
            int pendingComments = numberOfPendingComments.get(courseId);
            courseTables.add(createCourseTable(
                    courseDetails.course, instructor, courseDetails.feedbackSessions, pendingComments));
        }
    }
    
    private CourseTable createCourseTable(CourseAttributes course, InstructorAttributes instructor,
            List<FeedbackSessionAttributes> feedbackSessions, int pendingCommentsCount) {
        String courseId = course.id;
        return new CourseTable(course,
                               createCourseTableLinks(instructor, courseId, pendingCommentsCount),
                               createSessionRows(feedbackSessions, instructor, courseId));
    }
    
    private ElementTag createButton(String text, String className, String href, String tooltip) {
        return new ElementTag(text, "class", className, "href", href, "title", tooltip);
    }
    
    private void addAttributeIf(boolean shouldAdd, ElementTag button, String key, String value) {
        if (shouldAdd) {
            button.setAttribute(key, value);
        }
    }
    
    private String getInstructorFeedbacksPageLinkForCourse(String courseID) {
        String link = super.getInstructorFeedbacksPageLink();
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        return link;
    }
    
    private List<ElementTag> createCourseTableLinks(InstructorAttributes instructor, String courseId,
            int pendingCommentsForThisCourseCount) {
        String disabled = "disabled";
        String className = "btn btn-primary btn-xs btn-tm-actions course-";
        
        ElementTag enroll = createButton("Enroll",
                                         className + "enroll-for-test",
                                         getInstructorCourseEnrollLink(courseId),
                                         Const.Tooltips.COURSE_ENROLL);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                       enroll, disabled, disabled);
        
        ElementTag view = createButton("View",
                                       className + "view-for-test",
                                       getInstructorCourseDetailsLink(courseId),
                                       Const.Tooltips.COURSE_DETAILS);
        
        ElementTag edit = createButton("Edit",
                                       className + "edit-for-test",
                                       getInstructorCourseEditLink(courseId),
                                       Const.Tooltips.COURSE_EDIT);
        
        ElementTag add = createButton("Add Session",
                                      className + "add-eval-for-test",
                                      getInstructorFeedbacksPageLinkForCourse(courseId),
                                      Const.Tooltips.COURSE_ADD_FEEDBACKSESSION);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION),
                       add, disabled, disabled);
        
        ElementTag archive = createButton("Archive",
                                          className + "archive-for-test",
                                          getInstructorCourseArchiveLink(courseId, true, true),
                                          Const.Tooltips.COURSE_ARCHIVE);
        addAttributeIf(true, archive, "onclick", "return toggleArchiveCourseConfirmation('" + courseId + "')");
        
        ElementTag delete = createButton("Delete",
                                         className + "delete-for-test",
                                         getInstructorCourseDeleteLink(courseId, true),
                                         Const.Tooltips.COURSE_DELETE);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE),
                       delete, disabled, disabled);
        addAttributeIf(true, delete, "onclick", "return toggleDeleteCourseConfirmation('" + courseId + "')");
        
        if (pendingCommentsForThisCourseCount <= 0) {
            return Arrays.asList(enroll, view, edit,add, archive, delete);
        } else {
            String pendingGraphic = "<span class=\"badge\">" + pendingCommentsForThisCourseCount + "</span>"
                                    + "<span class=\"glyphicon glyphicon-comment\"></span>"
                                    + "<span class=\"glyphicon glyphicon-arrow-right\"></span>"
                                    + "<span class=\"glyphicon glyphicon-envelope\"></span>";
            ElementTag pending = createButton(
                    pendingGraphic,
                    className + "notify-pending-comments-for-test",
                    getInstructorClearPendingCommentsLink(courseId),
                    "Send email notification to recipients of " + pendingCommentsForThisCourseCount
                        + " pending " + (pendingCommentsForThisCourseCount > 1 ? "comments" : "comment"));
    
            return Arrays.asList(enroll, view, edit,add, archive, pending, delete);
        }
    }
    
    private List<Map<String, Object>> createSessionRows(List<FeedbackSessionAttributes> sessions,
            InstructorAttributes instructor, String courseId) {
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        int displayedStatsCount = 0;
        
        for (FeedbackSessionAttributes session : sessions) {
            Map<String, Object> columns = new HashMap<String, Object>();
            
            columns.put("name", sanitizeForHtml(session.feedbackSessionName));
            columns.put("tooltip", getInstructorHoverMessageForFeedbackSession(session));
            columns.put("status", getInstructorStatusForFeedbackSession(session));
            columns.put("href", getFeedbackSessionStatsLink(session.courseId, session.feedbackSessionName));
            
            if (session.isOpened() || session.isWaitingToOpen()) {
                columns.put("recent", " recent");
            } else if (displayedStatsCount < InstructorHomePageData.MAX_CLOSED_SESSION_STATS
                       && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                columns.put("recent", " recent");
                ++displayedStatsCount;
            }
            
            try {
                columns.put("actions", getInstructorFeedbackSessionActions(session, false, instructor,
                                               getCourseIdSectionNamesMap(sessions).get(courseId)));
            } catch (EntityDoesNotExistException e) {
                // nothing
            }
            
            rows.add(columns);
        }
        
        return rows;
    }
}
