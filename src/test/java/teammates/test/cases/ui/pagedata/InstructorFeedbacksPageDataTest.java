package teammates.test.cases.ui.pagedata;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorFeedbacksPageData;
import teammates.ui.template.FeedbackSessionsTableRow;
import teammates.ui.template.FeedbackSessionsCopyFromModal;
import teammates.ui.template.FeedbackSessionsTable;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbacksPageDataTest extends BaseTestCase {

    private static Gson gson = Utils.getTeammatesGson();
    
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    
    private final int NUMBER_OF_HOURS_IN_DAY= 24;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testInitWithoutDefaultFormValues() throws Exception {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        
        ______TS("typical success case");
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(instructorAccount);
        
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);
        
        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);
        Map<String, List<String>> courseIdToSectionNameMap = getCourseIdToSectionNameMap(instructorsForUser, dataBundle.students.values());
        
        data.initWithoutDefaultFormValues(courses, null, fsList, courseInstructorMap, null, courseIdToSectionNameMap);
        
        ______TS("typical success case: test new fs form");
        // Test new fs form model
        FeedbackSessionsForm formModel = data.getNewFsForm();
        
        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Team peer evaluation session", formModel.getFeedbackSessionTypeOptions().get(1).getContent());
        assertEquals("selected", formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().get("selected"));
        assertEquals("", formModel.getFsEndDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsEndTimeOptions().size());
        assertEquals("", formModel.getFsName());
        
        Calendar currentDate = TimeHelper.now(0);
        String dateAsString = TimeHelper.formatDate(currentDate.getTime());
        
        assertEquals(dateAsString, formModel.getFsStartDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsStartTimeOptions().size());
        
        assertEquals(7, formModel.getGracePeriodOptions().size());
        
        int expectedDefaultGracePeriodOptionsIndex = 3;
        String defaultSelectedAttribute = formModel.getGracePeriodOptions().get(expectedDefaultGracePeriodOptionsIndex).getAttributes().get("selected");
        assertEquals("selected", defaultSelectedAttribute);
        
        assertEquals("Please answer all the given questions.", formModel.getInstructions());
        assertEquals("", formModel.getAdditionalSettings().getResponseVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getResponseVisibleTimeOptions().size());
        assertEquals("", formModel.getAdditionalSettings().getSessionVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getSessionVisibleTimeOptions().size());
        assertEquals(33, formModel.getTimezoneSelectField().size());
        
        assertTrue(formModel.getAdditionalSettings().isResponseVisiblePublishManuallyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleDateChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleImmediatelyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleNeverChecked());
        assertTrue(formModel.getAdditionalSettings().isResponseVisibleDateDisabled());
       
        assertTrue(formModel.getAdditionalSettings().isSessionVisibleAtOpenChecked());
        assertTrue(formModel.getAdditionalSettings().isSessionVisibleDateDisabled());
        assertFalse(formModel.getAdditionalSettings().isSessionVisibleDateButtonChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisiblePrivateChecked());
        
        ______TS("typical success case: session rows");
        FeedbackSessionsTable fsTableModel = data.getFsList();
        
        List<FeedbackSessionsTableRow> fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());

        String firstFsName = "Grace Period Session";
        assertEquals(firstFsName, fsRows.get(0).getName());
        String lastFsName = "First feedback session";
        assertEquals(lastFsName, fsRows.get(fsRows.size() - 1).getName());
        
        
        ______TS("typical success case: copy modal");
        FeedbackSessionsCopyFromModal copyModalModel = data.getCopyFromModal();
        
        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("" , copyModalModel.getFsName());
        assertEquals(6, copyModalModel.getExistingFeedbackSessions().size());
        
        
        
        ______TS("case with instructor with restricted permissions");
        AccountAttributes helperAccount = dataBundle.accounts.get("helperOfCourse1");
        
        InstructorFeedbacksPageData helperData = new InstructorFeedbacksPageData(helperAccount);
        
        Map<String, InstructorAttributes> helperCourseInstructorMap = new HashMap<String, InstructorAttributes>();
        instructors = getInstructorsForGoogleId(helperAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            helperCourseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForHelper = new ArrayList<InstructorAttributes>(helperCourseInstructorMap.values());
        List<CourseAttributes> helperCourses = getCoursesForInstructor(instructorsForHelper);
        
        List<FeedbackSessionAttributes> helperFsList = getFeedbackSessionsListForInstructor(instructorsForHelper);
        courseIdToSectionNameMap = getCourseIdToSectionNameMap(instructorsForHelper, dataBundle.students.values());
        
        helperData.initWithoutDefaultFormValues(helperCourses, null, helperFsList, helperCourseInstructorMap, null, courseIdToSectionNameMap);
        
        ______TS("case with instructor with restricted permissions: test new fs form");
        // Test new fs form model
        formModel = helperData.getNewFsForm();
        
        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals("No active courses!", formModel.getCoursesSelectField().get(0).getContent());
        
        assertFalse(formModel.isSubmitButtonDisabled());
        
        ______TS("case with instructor with restricted permissions: session rows");
        fsTableModel = helperData.getFsList();
        
        fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());
        
        ______TS("case with instructor with restricted permissions: copy modal");
        copyModalModel = helperData.getCopyFromModal();
        
        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("" , copyModalModel.getFsName());
        assertEquals(0, copyModalModel.getExistingFeedbackSessions().size());
        
        
        ______TS("case with highlighted session in session table");
        
        instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        
        data = new InstructorFeedbacksPageData(instructorAccount);
        
        courseInstructorMap = new HashMap<String, InstructorAttributes>();
        instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        courses = getCoursesForInstructor(instructorsForUser);
        
        fsList = getFeedbackSessionsListForInstructor(instructorsForUser);
        
        courseIdToSectionNameMap = getCourseIdToSectionNameMap(instructorsForUser, dataBundle.students.values());
        data.initWithoutDefaultFormValues(courses, "idOfTypicalCourse1", fsList, courseInstructorMap, "First feedback session", courseIdToSectionNameMap);
        
        List<FeedbackSessionsTableRow> sessionRows = data.getFsList().getExistingFeedbackSessions();
        boolean isFirstFeedbackSessionHighlighted = false;
        boolean isOtherFeedbackSessionHighlighted = false;
        for (FeedbackSessionsTableRow row : sessionRows) {
            if (row.getName().equals("First feedback session")) {
                isFirstFeedbackSessionHighlighted = row.getRowAttributes().getAttributes().get("class").matches(".*\\bwarning\\b.*");
            } else {
                if (row.getRowAttributes().getAttributes().get("class").matches(".*\\bwarning\\b.*")) {
                    isOtherFeedbackSessionHighlighted = true;
                }
            }
        }
        assertTrue(isFirstFeedbackSessionHighlighted);
        assertFalse(isOtherFeedbackSessionHighlighted);
        
    }
    
    
    @Test
    public void testInit() throws Exception {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        
        ______TS("typical success case with existing fs passed in");
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(instructorAccount);
        
        Map<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);
        
        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);
        
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        
        Map<String, List<String>> courseIdToSectionNameMap = getCourseIdToSectionNameMap(instructorsForUser, dataBundle.students.values());
        data.init(courses, null, fsList, courseInstructorMap, fsa, null, null, courseIdToSectionNameMap);
        
        ______TS("typical success case with existing fs passed in: test new fs form");
        // Test new fs form model
        FeedbackSessionsForm formModel = data.getNewFsForm();
        
        assertNull(formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Team peer evaluation session", formModel.getFeedbackSessionTypeOptions().get(1).getContent());
        assertEquals("selected", formModel.getFeedbackSessionTypeOptions().get(1).getAttributes().get("selected"));
        
        assertEquals("30/04/2017", formModel.getFsEndDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsEndTimeOptions().size());
        assertEquals("First feedback session", formModel.getFsName());
        
        assertEquals("01/04/2012", formModel.getFsStartDate());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getFsStartTimeOptions().size());
        
        assertEquals(7, formModel.getGracePeriodOptions().size());
        
        int expectedDefaultGracePeriodOptionsIndex = 2;
        String defaultSelectedAttribute = formModel.getGracePeriodOptions().get(expectedDefaultGracePeriodOptionsIndex).getAttributes().get("selected");
        assertEquals("selected", defaultSelectedAttribute);
        
        assertEquals("Please please fill in the following questions.", formModel.getInstructions());
        assertEquals("01/05/2017", formModel.getAdditionalSettings().getResponseVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getResponseVisibleTimeOptions().size());
        assertEquals("28/03/2012", formModel.getAdditionalSettings().getSessionVisibleDateValue());
        assertEquals(NUMBER_OF_HOURS_IN_DAY, formModel.getAdditionalSettings().getSessionVisibleTimeOptions().size());
        
        assertFalse(formModel.getAdditionalSettings().isResponseVisiblePublishManuallyChecked());
        assertTrue(formModel.getAdditionalSettings().isResponseVisibleDateChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleImmediatelyChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleNeverChecked());
        assertFalse(formModel.getAdditionalSettings().isResponseVisibleDateDisabled());
       
        assertFalse(formModel.getAdditionalSettings().isSessionVisibleAtOpenChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisibleDateDisabled());
        assertTrue(formModel.getAdditionalSettings().isSessionVisibleDateButtonChecked());
        assertFalse(formModel.getAdditionalSettings().isSessionVisiblePrivateChecked());
        
        ______TS("typical success case with existing fs passed in: session rows");
        FeedbackSessionsTable fsTableModel = data.getFsList();
        
        List<FeedbackSessionsTableRow> fsRows = fsTableModel.getExistingFeedbackSessions();
        assertEquals(6, fsRows.size());

        String firstFsName = "Grace Period Session";
        assertEquals(firstFsName, fsRows.get(0).getName());
        String lastFsName = "First feedback session";
        assertEquals(lastFsName, fsRows.get(fsRows.size() - 1).getName());
        
        
        ______TS("typical success case with existing fs passed in: copy modal");
        FeedbackSessionsCopyFromModal copyModalModel = data.getCopyFromModal();
        
        assertEquals(1, copyModalModel.getCoursesSelectField().size());
        assertEquals("First feedback session" , copyModalModel.getFsName());
        assertEquals(6, copyModalModel.getExistingFeedbackSessions().size());
    }
    
    @Test
    public void testInitWithoutHighlighting() throws Exception{

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor2OfCourse1");
        
        ______TS("typical success case with existing fs passed in");
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(instructorAccount);
        
        Map<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        List<CourseAttributes> courses = getCoursesForInstructor(instructorsForUser);
        
        List<FeedbackSessionAttributes> fsList = getFeedbackSessionsListForInstructor(instructorsForUser);
        
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("session1InCourse1");
        Map<String, List<String>> courseIdToSectionNameMap = getCourseIdToSectionNameMap(instructorsForUser, dataBundle.students.values());
        
        data.initWithoutHighlightedRow(courses, "idOfTypicalCourse1", fsList, courseInstructorMap, fsa, "STANDARD", courseIdToSectionNameMap);
        
        FeedbackSessionsForm formModel = data.getNewFsForm();
        
        assertEquals("idOfTypicalCourse1", formModel.getCourseId());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Session with your own questions", formModel.getFeedbackSessionTypeOptions().get(0).getContent());
        assertEquals("selected", formModel.getFeedbackSessionTypeOptions().get(0).getAttributes().get("selected"));

        FeedbackSessionsCopyFromModal modal = data.getCopyFromModal();
        assertEquals("First feedback session", modal.getFsName());
        
    }
    
    
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean isOmitArchived) {
        List<InstructorAttributes> instructors = new ArrayList<InstructorAttributes>(dataBundle.instructors.values());
        
        Iterator<InstructorAttributes> iter = instructors.iterator();
        while (iter.hasNext()) {
            InstructorAttributes instructor = iter.next();
            
            instructor.privileges = gson.fromJson(instructor.instructorPrivilegesAsText, InstructorPrivileges.class);
            
            boolean isGoogleIdSame = instructor.googleId != null 
                                     && instructor.googleId.equals(googleId);
            boolean isOmittedDueToArchiveStatus = isOmitArchived 
                                                  && (instructor.isArchived != null 
                                                      && instructor.isArchived);
            if (!isGoogleIdSame || isOmittedDueToArchiveStatus) {
                iter.remove();
            }
        }
        
        return instructors;
    }
    
    public List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = getSetOfCourseIdsFromInstructorAttributes(instructorsForUser);
        
        List<CourseAttributes> courses = new ArrayList<CourseAttributes>(dataBundle.courses.values());
        
        Iterator<CourseAttributes> iter = courses.iterator();
        while (iter.hasNext()) {
            CourseAttributes course = iter.next();
            if (!courseIdsOfUser.contains(course.id)) {
                iter.remove();
            }
        }
        
        return courses;
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = getSetOfCourseIdsFromInstructorAttributes(instructorsForUser);
        
        List<FeedbackSessionAttributes> feedbackSessions = new ArrayList<FeedbackSessionAttributes>(dataBundle.feedbackSessions.values());
        
        Iterator<FeedbackSessionAttributes> iter = feedbackSessions.iterator();
        while (iter.hasNext()) {
            FeedbackSessionAttributes fs = iter.next();
            if (!courseIdsOfUser.contains(fs.courseId)) {
                iter.remove();
            }
        }
        
        return feedbackSessions;
    }
    
    private Set<String> getSetOfCourseIdsFromInstructorAttributes(
                                    List<InstructorAttributes> instructorsForUser) {
        Set<String> courseIdsOfUser = new HashSet<String>();
        for (InstructorAttributes instructor : instructorsForUser) {
            courseIdsOfUser.add(instructor.courseId);
        }
        return courseIdsOfUser;
    }
        
    
    private Map<String, List<String>> getCourseIdToSectionNameMap(List<InstructorAttributes> instructorsForUser, 
                                                                  Collection<StudentAttributes> allStudents) {
        Set<String> courseIds = getSetOfCourseIdsFromInstructorAttributes(instructorsForUser);
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        
        for (StudentAttributes student : allStudents) {
            if (!courseIds.contains(student.course)) {
                continue;
            }
            
            if (!result.containsKey(student.course)) {
                List<String> sectionList = new ArrayList<String>();
                sectionList.add(student.section);
                result.put(student.course, sectionList);
            } else {
                if (!result.get(student.course).contains(student.section)) {
                    result.get(student.course).add(student.section);
                }
            }
        }
        
        return result;
        
    }
}
