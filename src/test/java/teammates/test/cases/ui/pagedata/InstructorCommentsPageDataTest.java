package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.test.util.TestHelper;
import teammates.ui.controller.InstructorCommentsPageData;
import teammates.ui.controller.PageData;
import teammates.ui.template.CommentRow;
import teammates.ui.template.InstructorCommentsCommentRow;
import teammates.ui.template.InstructorCommentsForStudentsTable;
import teammates.ui.template.VisibilityCheckboxes;

public class InstructorCommentsPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static CourseAttributes course1;
    private static CourseAttributes course2;
    private static InstructorAttributes instructor1;
    private static InstructorAttributes instructor2;
    
    @BeforeClass
    public void classSetUp() throws Exception {
        printTestClassHeader();
        course1 = dataBundle.courses.get("typicalCourse1");
        course2 = dataBundle.courses.get("typicalCourse2");
        instructor1 = dataBundle.instructors.get("instructor3OfCourse1");
        instructor2 = dataBundle.instructors.get("instructor1OfCourse1");
    }
    
    @Test
    public void testAll() {
        
        ______TS("typical success case");
        
        AccountAttributes account = dataBundle.accounts.get("instructor3");      
        InstructorCommentsPageData data = new InstructorCommentsPageData(account);
        
        boolean isViewingDraft = false;
        boolean isDisplayArchive = false;
        String courseId = course1.id;
        String courseName = course1.name;
        List<String> coursePaginationList = Arrays.asList(course1.id, course2.id);
        Map<String, List<CommentAttributes>> comments = new TreeMap<String, List<CommentAttributes>>();
        Map<String, List<Boolean>> commentModifyPermissions = new TreeMap<String, List<Boolean>>();
        
        CourseRoster roster = new CourseRoster(getStudentsInCourse(courseId), getInstructorsInCourse(courseId));
        List<FeedbackSessionAttributes> feedbackSessions = getFeedbackSessionsForCourse(courseId);
        int numberOfPendingComments = 0;
        
        // Setup instructor comments
        String giverEmail = instructor1.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        giverEmail = instructor2.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        /******************** Assertions for pageData data ********************/
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertTrue(coursePaginationList.equals(data.getCoursePaginationList()));
        
        Map<String, List<CommentAttributes>> actualComments = data.getComments();
        Map<String, List<CommentAttributes>> expectedComments = comments;
        
        List<String> actualGivers = new ArrayList<String>();
        actualGivers.addAll(actualComments.keySet());
        List<String> expectedGivers = new ArrayList<String>();
        expectedGivers.addAll(expectedComments.keySet());
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedGivers, actualGivers));
        for (String email : expectedGivers) {
            assertEquals(expectedComments.get(email), actualComments.get(email));
        }

        assertEquals(feedbackSessions, data.getFeedbackSessions());
        String expectedNextPageLink = data.getInstructorCommentsLink() + "&courseid=" + course2.id;
        String expectedPreviousPageLink = "javascript:;";
        
        assertEquals(expectedNextPageLink, data.getNextPageLink());
        assertEquals(expectedPreviousPageLink, data.getPreviousPageLink());
        
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        
        /******************** Assertions for data structures ********************/
        List<InstructorCommentsForStudentsTable> expectedCommentsForStudentsTables =
                getCommentsForStudentsTables(courseId, commentModifyPermissions, roster, comments, data);
        List<InstructorCommentsForStudentsTable> actualCommentsForStudentsTables =
                data.getCommentsForStudentsTables();
        
        assertEquals(expectedCommentsForStudentsTables.size(), actualCommentsForStudentsTables.size());
        for(int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            assertTrue(isCommentsForStudentsTablesEqual(
                               expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i)));
        }
        
        ______TS("instructor is in second course page");
        
        courseId = course2.id;
        courseName = course2.name;
        
        comments = new TreeMap<String, List<CommentAttributes>>();
        commentModifyPermissions = new TreeMap<String, List<Boolean>>();
        
        roster = new CourseRoster(getStudentsInCourse(courseId), getInstructorsInCourse(courseId));
        feedbackSessions = getFeedbackSessionsForCourse(courseId);
        
        // Setup instructor comments
        giverEmail = instructor1.email;
        setInstructorComments(giverEmail, instructor1.email, courseId, comments, commentModifyPermissions);
        
        data.init(isViewingDraft, isDisplayArchive, courseId, courseName, coursePaginationList, 
                  comments, commentModifyPermissions, roster, feedbackSessions, numberOfPendingComments);
        
        expectedNextPageLink = "javascript:;";
        expectedPreviousPageLink = data.getInstructorCommentsLink() + "&courseid=" + course1.id;
        assertEquals(data.getNextPageLink(), expectedNextPageLink);
        assertEquals(data.getPreviousPageLink(), expectedPreviousPageLink);
        
        assertEquals(data.getNumberOfPendingComments(), numberOfPendingComments);
        
        /******************** Assertions for pageData data ********************/
        assertFalse(data.isDisplayArchive());
        assertFalse(data.isViewingDraft());
        assertEquals(courseId, data.getCourseId());
        assertEquals(courseName, data.getCourseName());
        assertEquals(coursePaginationList, data.getCoursePaginationList());
        
        actualComments = data.getComments();
        expectedComments = comments;
        
        actualGivers = new ArrayList<String>();
        actualGivers.addAll(actualComments.keySet());
        expectedGivers = new ArrayList<String>();
        expectedGivers.addAll(expectedComments.keySet());
        
        assertTrue(TestHelper.isSameContentIgnoreOrder(expectedGivers, actualGivers));
        for (String email : expectedGivers) {
            assertEquals(expectedComments.get(email), actualComments.get(email));
        }
        assertEquals(feedbackSessions, data.getFeedbackSessions());
        expectedNextPageLink = "javascript:;";
        expectedPreviousPageLink = data.getInstructorCommentsLink() + "&courseid=" + course1.id;
        assertEquals(data.getNextPageLink(), expectedNextPageLink);
        assertEquals(data.getPreviousPageLink(), expectedPreviousPageLink);
        
        assertEquals(numberOfPendingComments, data.getNumberOfPendingComments());
        
        /******************** Assertions for data structures ********************/
        expectedCommentsForStudentsTables =
                getCommentsForStudentsTables(courseId, commentModifyPermissions, roster, comments, data);
        actualCommentsForStudentsTables =
                data.getCommentsForStudentsTables();
        
        assertEquals(expectedCommentsForStudentsTables.size(), actualCommentsForStudentsTables.size());
        for(int i = 0; i < expectedCommentsForStudentsTables.size(); i++) {
            assertTrue(isCommentsForStudentsTablesEqual(
                               expectedCommentsForStudentsTables.get(i), actualCommentsForStudentsTables.get(i)));
        }
    }

    private List<CommentRow> createCommentRows(
            String courseId, String giverEmail, String giverName,
            Map<String, List<Boolean>> commentModifyPermissions, CourseRoster roster, 
            Map<String, List<CommentAttributes>> comments, InstructorCommentsPageData data) {
        
        List<CommentRow> rows = new ArrayList<CommentRow>();
        List<CommentAttributes> commentsForGiver = comments.get(giverEmail);
        for (int i = 0; i < commentsForGiver.size(); i++) {            
            String recipientDetails = 
                    getRecipientNames(data, courseId, commentsForGiver.get(i).recipients, roster);
            String creationTime = 
                    Const.SystemParams.COMMENTS_SIMPLE_DATE_FORMATTER.format(commentsForGiver.get(i).createdAt);          
            Boolean isInstructorAllowedToModifyCommentInSection = commentModifyPermissions.get(giverEmail).get(i);
            String typeOfPeopleCanViewComment = data.getTypeOfPeopleCanViewComment(commentsForGiver.get(i));
            String editedAt = commentsForGiver.get(i).getEditedAtText(giverName.equals("Anonymous"));
            String showCommentsTo = data.getShowCommentsToForComment(commentsForGiver.get(i));
            String showGiverNameTo = data.getShowGiverNameToForComment(commentsForGiver.get(i));
            String showRecipientNameTo = data.getShowRecipientNameToForComment(commentsForGiver.get(i));
            VisibilityCheckboxes visibilityCheckboxes = new VisibilityCheckboxes(commentsForGiver.get(i));
            
            rows.add(new InstructorCommentsCommentRow(giverEmail, commentsForGiver.get(i), recipientDetails, creationTime, 
                     isInstructorAllowedToModifyCommentInSection, typeOfPeopleCanViewComment, editedAt,
                     visibilityCheckboxes, showCommentsTo, showGiverNameTo, showRecipientNameTo));
        }       
        return rows;
    }

    private List<CommentAttributes> getCommentsForGiverInCourse(String giverEmail, String courseId) {
        List<CommentAttributes> commentsForGiverInCourseList = new ArrayList<CommentAttributes>();
        for (CommentAttributes comment : dataBundle.comments.values()) {
            if (comment.giverEmail.equals(giverEmail) && comment.courseId.equals(courseId)) {
                commentsForGiverInCourseList.add(comment);
            }
        }
        return commentsForGiverInCourseList;
    }

    private List<InstructorCommentsForStudentsTable> getCommentsForStudentsTables(
            String courseId, Map<String, List<Boolean>> commentModifyPermissions, CourseRoster roster, 
            Map<String, List<CommentAttributes>> comments, InstructorCommentsPageData data) {
        Map<String, String> giverEmailToGiverNameMap = getGiverEmailToGiverNameMap(comments, roster);
        List<InstructorCommentsForStudentsTable> commentsForStudentsTables = 
                new ArrayList<InstructorCommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            commentsForStudentsTables
                    .add(new InstructorCommentsForStudentsTable(giverEmail, giverName, 
                                 createCommentRows(courseId, giverEmail, giverName,
                                                   commentModifyPermissions, roster, comments, data)));
        }
        return commentsForStudentsTables;
    }

    private List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        List<FeedbackSessionAttributes> feedbackSessionsInCourse = new ArrayList<FeedbackSessionAttributes>();
        for (FeedbackSessionAttributes feedbackSession : dataBundle.feedbackSessions.values()) {
            if (feedbackSession.courseId.equals(courseId)) {
                feedbackSessionsInCourse.add(feedbackSession);
            }
        }
        return feedbackSessionsInCourse;
    }

    private Map<String, String> getGiverEmailToGiverNameMap(
            Map<String, List<CommentAttributes>> comments, CourseRoster roster) {
    
        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        for (String giverEmail : comments.keySet()) {
    
            InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
            String giverDisplay = giverEmail;
            if (giverEmail.equals(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                giverDisplay = "You";
            } else if (instructor != null) {
                String title = instructor.displayedName;
                giverDisplay = title + " " + instructor.name;
            }
    
            giverEmailToGiverNameMap.put(giverEmail, giverDisplay);
        }
        return giverEmailToGiverNameMap;
    }

    private List<InstructorAttributes> getInstructorsInCourse(String courseId) {
        List<InstructorAttributes> instructorsInCourse = new ArrayList<InstructorAttributes>();
        for (InstructorAttributes instructor : dataBundle.instructors.values()) {
            if (instructor.courseId.equals(courseId)) {
                instructorsInCourse.add(instructor);
            }
        }
        return instructorsInCourse;
    }
    
    private String getRecipientNames(PageData data, String courseId, Set<String> recipients, CourseRoster roster) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (courseId.equals(recipient)) { 
                namesStringBuilder.append("<b>All students in this course</b>, ");
            } else if (student != null) {
                if (recipients.size() == 1) {
                    namesStringBuilder.append("<b>" + student.name + " (" + student.team + ", " + student.email + ")</b>, ");
                } else {
                    namesStringBuilder.append("<b>" + student.name + "</b>" + ", ");
                }
            } else {
                namesStringBuilder.append("<b>" + recipient + "</b>" + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return data.removeEndComma(namesString);
    }

    private List<StudentAttributes> getStudentsInCourse(String courseId) {
        List<StudentAttributes> studentsInCourse = new ArrayList<StudentAttributes>();
        for (StudentAttributes student : dataBundle.students.values()) {
            if (student.course.equals(courseId)) {
                studentsInCourse.add(student);
            }
        }
        return studentsInCourse;
    }
    
    private void setInstructorComments(
            String giverEmail, String currentInstructorEmail, String courseId, 
            Map<String, List<CommentAttributes>> comments,
            Map<String, List<Boolean>> commentModifyPermissions) {
        List<CommentAttributes> commentsForGiverList;
        List<Boolean> canModifyCommentList = new ArrayList<Boolean>();
        commentsForGiverList = getCommentsForGiverInCourse(giverEmail, courseId);
        for(int i = 0; i < commentsForGiverList.size(); i++) {
            canModifyCommentList.add(true);
        }
        String key = giverEmail;
        if (giverEmail.equals(currentInstructorEmail)) {
            key = InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST;
        }
        commentModifyPermissions.put(key, canModifyCommentList);
        comments.put(key, commentsForGiverList);   
    }
    
    private boolean isCommentsForStudentsTablesEqual(
            InstructorCommentsForStudentsTable expected, InstructorCommentsForStudentsTable actual) {
        boolean result = expected.getGiverEmail().equals(actual.getGiverEmail());
        result = result && expected.getGiverName().equals(actual.getGiverName());
        List<CommentRow> expectedCommentRows = expected.getRows();
        List<CommentRow> actualCommentRows = actual.getRows();
        result = result && expectedCommentRows.size() == actualCommentRows.size();
        for(int i = 0; i < expectedCommentRows.size() && result; i++) {
            InstructorCommentsCommentRow expectedInstructorCommentsCommentRow =
                    (InstructorCommentsCommentRow) expectedCommentRows.get(i);
            InstructorCommentsCommentRow actualInstructorCommentsCommentRow =
                    (InstructorCommentsCommentRow) actualCommentRows.get(i);
            result = result && isCommentRowsEqual(
                                       expectedInstructorCommentsCommentRow, actualInstructorCommentsCommentRow);
        }
        return result;
    }
    
    private boolean isCommentRowsEqual(InstructorCommentsCommentRow expected, InstructorCommentsCommentRow actual) {
        boolean result = expected.isInstructorAllowedToModifyCommentInSection() 
                         == actual.isInstructorAllowedToModifyCommentInSection();
        result = result && expected.getTypeOfPeopleCanViewComment().equals(actual.getTypeOfPeopleCanViewComment());
        result = result && expected.getEditedAt().equals(actual.getEditedAt());
        result = result && isVisibilityCheckboxesEqual(
                                   expected.getVisibilityCheckboxes(), actual.getVisibilityCheckboxes());
        result = result && expected.getShowCommentsTo().equals(actual.getShowCommentsTo());
        result = result && expected.getShowGiverNameTo().equals(actual.getShowGiverNameTo());
        result = result && expected.getShowRecipientNameTo().equals(actual.getShowRecipientNameTo());
        return result;
    }
    
    private boolean isVisibilityCheckboxesEqual(VisibilityCheckboxes expected, VisibilityCheckboxes actual) {
        boolean result = true;
        List<Boolean> expectedVisibilitySettingsForRecipient = expected.getVisibilitySettingsForRecipient();
        List<Boolean> expectedVisibilitySettingsForRecipientTeam = expected.getVisibilitySettingsForRecipientTeam();
        List<Boolean> expectedVisibilitySettingsForRecipientSection = expected.getVisibilitySettingsForRecipientSection();
        List<Boolean> expectedVisibilitySettingsForCourseStudents = expected.getVisibilitySettingsForCourseStudents();
        List<Boolean> expectedVisibilitySettingsForInstructors = expected.getVisibilitySettingsForInstructors();
        
        List<Boolean> actualVisibilitySettingsForRecipient = actual.getVisibilitySettingsForRecipient();
        List<Boolean> actualVisibilitySettingsForRecipientTeam = actual.getVisibilitySettingsForRecipientTeam();
        List<Boolean> actualVisibilitySettingsForRecipientSection = actual.getVisibilitySettingsForRecipientSection();
        List<Boolean> actualVisibilitySettingsForCourseStudents = actual.getVisibilitySettingsForCourseStudents();
        List<Boolean> actualVisibilitySettingsForInstructors = actual.getVisibilitySettingsForInstructors();
        int typesOfVisibilitySettings = 3;
        for (int i = 0; i < typesOfVisibilitySettings; i++) {
            result = result && expectedVisibilitySettingsForRecipient
                                       .get(i).equals(actualVisibilitySettingsForRecipient.get(i));
            result = result && expectedVisibilitySettingsForRecipientTeam
                                       .get(i).equals(actualVisibilitySettingsForRecipientTeam.get(i));
            result = result && expectedVisibilitySettingsForRecipientSection
                                       .get(i).equals(actualVisibilitySettingsForRecipientSection.get(i));
            result = result && expectedVisibilitySettingsForCourseStudents
                                       .get(i).equals(actualVisibilitySettingsForCourseStudents.get(i));
            result = result && expectedVisibilitySettingsForInstructors
                                       .get(i).equals(actualVisibilitySettingsForInstructors.get(i));
        }
        return result;
    }
}
