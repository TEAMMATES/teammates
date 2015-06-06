package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.ui.template.CourseTable;
import teammates.ui.template.CourseTableLink;
import teammates.ui.template.CourseTableSessionRow;

public class InstructorHomePageData extends PageData {
    
    public InstructorHomePageData(AccountAttributes account) {
        super(account);
    }
    
    public HashMap<String, InstructorAttributes> instructors;
    public List<CourseSummaryBundle> courses;
    public String sortCriteria;
    public HashMap<String, Integer> numberOfPendingComments;
    public static final int MAX_CLOSED_SESSION_STATS = 3;
    
    public String getInstructorFeedbacksPageLinkForCourse(String courseID) {
        String link = super.getInstructorFeedbacksPageLink();
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        return link;
    }
    
    public String getSortCriteria() {
        return sortCriteria;
    }

    public void setSortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
    }
    
    public int getUnarchivedCoursesCount() {
        int unarchivedCoursesCount = 0;
        for (CourseSummaryBundle courseDetails : courses) {
            InstructorAttributes instructor = instructors.get(courseDetails.course.id);
            boolean notArchived = instructor.isArchived == null || !instructor.isArchived;
            if (notArchived) {
                unarchivedCoursesCount++;
            }
        }
        return unarchivedCoursesCount;
    }
    
    public List<CourseTable> getCourseTables() {
        List<CourseTable> courseTables = new ArrayList<CourseTable>(); 
        for (CourseSummaryBundle courseDetails : courses) {
            courseTables.add(createCourseTable(courseDetails));
        }
        return courseTables;
    }
    
    public CourseTable createCourseTable(CourseSummaryBundle courseDetails) {
        String courseId = courseDetails.course.id;
        InstructorAttributes instructor = instructors.get(courseId);
        return new CourseTable(courseDetails.course,
                               createCourseTableLinks(instructor, courseId),
                               createSessionRows(courseDetails.feedbackSessions, instructor, courseId));
    }
    
    private CourseTableLink createButton(String text, String href, String tooltip) {
        return new CourseTableLink(text, "href", href, "title", tooltip);
    }
    
    private void addAttributeIf(boolean shouldAdd, CourseTableLink button, String key, String value) {
        if (shouldAdd) {
            button.getAttributes().put(key, value);
        }
    }
    
    private List<CourseTableLink> createCourseTableLinks(InstructorAttributes instructor, String courseId) {
        String disabled = "disabled";
        
        CourseTableLink enroll = createButton("Enroll",
                                              getInstructorCourseEnrollLink(courseId),
                                              Const.Tooltips.COURSE_ENROLL);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT),
                       enroll, disabled, disabled);
        
        CourseTableLink view = createButton("View",
                                            getInstructorCourseDetailsLink(courseId),
                                            Const.Tooltips.COURSE_DETAILS);
        
        CourseTableLink edit = createButton("Edit",
                                            getInstructorCourseEditLink(courseId),
                                            Const.Tooltips.COURSE_EDIT);
        
        CourseTableLink add = createButton("Add Session",
                                           getInstructorFeedbacksPageLinkForCourse(courseId),
                                           Const.Tooltips.COURSE_ADD_FEEDBACKSESSION);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION),
                       add, disabled, disabled);
        
        CourseTableLink archive = createButton("Archive",
                                               getInstructorCourseArchiveLink(courseId, true, true),
                                               Const.Tooltips.COURSE_ARCHIVE);
        addAttributeIf(true, archive, "onclick", "return toggleArchiveCourseConfirmation('" + courseId + "')");
        
        CourseTableLink delete = createButton("Delete",
                                              getInstructorCourseDeleteLink(courseId, true),
                                              Const.Tooltips.COURSE_DELETE);
        addAttributeIf(!instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE),
                       delete, disabled, disabled);
        addAttributeIf(true, delete, "onclick", "return toggleDeleteCourseConfirmation('" + courseId + "')");
        
        int numberOfPendingCommentsForThisCourse = numberOfPendingComments.get(courseId);
        if (numberOfPendingCommentsForThisCourse <= 0) {
            return Arrays.asList(enroll, view, edit,add, archive, delete);
        } else {
            String pendingGraphic = "<span class=\"badge\">" + numberOfPendingCommentsForThisCourse + "</span>"
                                    + "<span class=\"glyphicon glyphicon-comment\"></span>"
                                    + "<span class=\"glyphicon glyphicon-arrow-right\"></span>"
                                    + "<span class=\"glyphicon glyphicon-envelope\"></span>";
            CourseTableLink pending = createButton(
                    pendingGraphic,
                    getInstructorClearPendingCommentsLink(courseId),
                    "Send email notification to recipients of " + numberOfPendingCommentsForThisCourse
                        + " pending " + (numberOfPendingCommentsForThisCourse > 1 ? "comments" : "comment"));
    
            return Arrays.asList(enroll, view, edit,add, archive, pending, delete);
        }
    }
    
    private List<CourseTableSessionRow> createSessionRows(List<FeedbackSessionAttributes> sessions,
            InstructorAttributes instructor, String courseId) {
        List<CourseTableSessionRow> rows = new ArrayList<CourseTableSessionRow>();
        int displayedStatsCount = 0;
        
        for (FeedbackSessionAttributes session : sessions) {
            String name = PageData.sanitizeForHtml(session.feedbackSessionName);
            String tooltip = PageData.getInstructorHoverMessageForFeedbackSession(session);
            String status = PageData.getInstructorStatusForFeedbackSession(session);
            String href = getFeedbackSessionStatsLink(session.courseId, session.feedbackSessionName);
            
            String recent = "";
            if (session.isOpened() || session.isWaitingToOpen()) {
                recent = " recent";
            } else if (displayedStatsCount < InstructorHomePageData.MAX_CLOSED_SESSION_STATS
                       && !TimeHelper.isOlderThanAYear(session.createdTime)) {
                recent = " recent";
                ++displayedStatsCount;
            }
            
            String actions = "";
            try {
                actions = getInstructorFeedbackSessionActions(session, false, instructor,
                        getCourseIdSectionNamesMap(sessions).get(courseId));
            } catch (EntityDoesNotExistException e) {
                // nothing
            }
            
            rows.add(new CourseTableSessionRow(name, tooltip, status, href, recent, actions));
        }
        
        return rows;
    }
}
