package teammates.test.cases.ui.datatests;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.logic.core.AccountsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.ui.controller.InstructorFeedbacksPageData;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.FeedbackSessionsCopyFromModal;
import teammates.ui.template.FeedbackSessionsTable;
import teammates.ui.template.FeedbackSessionsForm;

public class InstructorFeedbacksPageDataTest extends BaseComponentTestCase {

    private Logic logic = new Logic();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    
    private final int numHoursInDay = 24;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        turnLoggingUp(AccountsLogic.class);
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testInitWithoutDefaultFormValues() throws Exception {

        AccountAttributes instructorAccount = dataBundle.accounts.get("instructor1OfCourse1");
        
        ______TS("typical success case");
        
        InstructorFeedbacksPageData data = new InstructorFeedbacksPageData(instructorAccount);
        
        HashMap<String, InstructorAttributes> courseInstructorMap = new HashMap<String, InstructorAttributes>();
        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        List<CourseAttributes> courses = logic.getCoursesForInstructor(instructorsForUser);
        
        List<FeedbackSessionAttributes> fsList = logic.getFeedbackSessionsListForInstructor(instructorsForUser);
        
        data.initWithoutDefaultFormValues(courses, null, fsList, courseInstructorMap, null);
        
        ______TS("typical success case: test new fs form");
        // Test new fs form model
        FeedbackSessionsForm formModel = data.getNewForm();
        
        assertNull(formModel.getCourseIdForNewSession());
        assertEquals(1, formModel.getCoursesSelectField().size());
        assertEquals(2, formModel.getFeedbackSessionTypeOptions().size());
        assertEquals("Team peer evaluation session", formModel.getFeedbackSessionTypeOptions().get(1).getContent());
        assertEquals("", formModel.getFsEndDate());
        assertEquals(numHoursInDay, formModel.getFsEndTimeOptions().size());
        assertEquals("", formModel.getFsName());
        
        Calendar currentDate = TimeHelper.now(0);
        String dateAsString = TimeHelper.formatDate(currentDate.getTime());
        
        assertEquals(dateAsString, formModel.getFsStartDate());
        assertEquals(numHoursInDay, formModel.getFsStartTimeOptions().size());
        
        assertEquals(7, formModel.getGracePeriodOptions().size());
        
        int expectedDefaultGracePeriodOptionsIndex = 3;
        String defaultSelectedAttribute = formModel.getGracePeriodOptions().get(expectedDefaultGracePeriodOptionsIndex).getAttributes().get("selected");
        assertEquals("selected", defaultSelectedAttribute);
        
        assertEquals("Please answer all the given questions.", formModel.getInstructions());
        assertEquals("", formModel.getResponseVisibleDateValue());
        assertEquals(numHoursInDay, formModel.getResponseVisibleTimeOptions().size());
        assertEquals("", formModel.getSessionVisibleDateValue());
        assertEquals(numHoursInDay, formModel.getSessionVisibleTimeOptions().size());
        assertEquals(33, formModel.getTimezoneSelectField().size());
        
        assertTrue(formModel.isResponseVisiblePublishManuallyChecked());
        assertFalse(formModel.isResponseVisibleDateChecked());
        assertFalse(formModel.isResponseVisibleImmediatelyChecked());
        assertFalse(formModel.isResponseVisibleNeverChecked());
        assertTrue(formModel.isResponseVisibleDisabled());
       
        assertTrue(formModel.isSessionVisibleAtOpenChecked());
        assertTrue(formModel.isSessionVisibleDateDisabled());
        assertFalse(formModel.isSessionVisibleDateButtonChecked());
        assertFalse(formModel.isSessionVisiblePrivateChecked());
        
        ______TS("typical success case: session rows");
        FeedbackSessionsTable fsTableModel = data.getFsList();
        
        List<FeedbackSessionRow> fsRows = fsTableModel.getExistingFeedbackSessions();
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
        
        HashMap<String, InstructorAttributes> helperCourseInstructorMap = new HashMap<String, InstructorAttributes>();
        instructors = logic.getInstructorsForGoogleId(helperAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            helperCourseInstructorMap.put(instructor.courseId, instructor);
        }
        
        List<InstructorAttributes> instructorsForHelper = new ArrayList<InstructorAttributes>(helperCourseInstructorMap.values());
        List<CourseAttributes> helperCourses = logic.getCoursesForInstructor(instructorsForHelper);
        
        List<FeedbackSessionAttributes> helperFsList = logic.getFeedbackSessionsListForInstructor(instructorsForHelper);
        
        helperData.initWithoutDefaultFormValues(helperCourses, null, helperFsList, helperCourseInstructorMap, null);
        
        ______TS("case with instructor with restricted permissions: test new fs form");
        // Test new fs form model
        formModel = helperData.getNewForm();
        
        assertNull(formModel.getCourseIdForNewSession());
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
        instructors = logic.getInstructorsForGoogleId(instructorAccount.googleId, true);
        for (InstructorAttributes instructor : instructors) {
            courseInstructorMap.put(instructor.courseId, instructor);
        }
        
        instructorsForUser = new ArrayList<InstructorAttributes>(courseInstructorMap.values());
        courses = logic.getCoursesForInstructor(instructorsForUser);
        
        fsList = logic.getFeedbackSessionsListForInstructor(instructorsForUser);
        
        data.initWithoutDefaultFormValues(courses, "idOfTypicalCourse1", fsList, courseInstructorMap, "First feedback session");
        
        List<FeedbackSessionRow> sessionRows = data.getFsList().getExistingFeedbackSessions();
        boolean isFirstFeedbackSessionHighlighted = false;
        boolean isOtherFeedbackSessionHighlighted = false;
        for (FeedbackSessionRow row : sessionRows) {
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
    
    
  
    
}
