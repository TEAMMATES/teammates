package teammates.test.cases.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.test.cases.BaseTestCase;
import teammates.ui.controller.InstructorCourseEditPageData;

public class InstructorCourseEditPageDataTest extends BaseTestCase {
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
    }
    
    @Test
    public void testAll() {
        ______TS("test typical case");
        
        AccountAttributes account = dataBundle.accounts.get("instructor1OfCourse1");
        InstructorCourseEditPageData pageData = new InstructorCourseEditPageData(account);
        
        CourseAttributes course = dataBundle.courses.get("typicalCourse1");
        
        List<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
        instructorList.add(dataBundle.instructors.get("instructor1OfCourse1"));
        instructorList.add(dataBundle.instructors.get("instructor2OfCourse1"));
        instructorList.add(dataBundle.instructors.get("helperOfCourse1"));
        
        InstructorAttributes currentInstructor = dataBundle.instructors.get("instructor1OfCourse1");
        
        int offset = -1;
        
        List<String> sectionNames = new ArrayList<String>();
        sectionNames.add("Section 1");
        sectionNames.add("Section 2");
        
        List<String> feedbackSessionNames = new ArrayList<String>();
        feedbackSessionNames.add("First feedback session");
        feedbackSessionNames.add("Second feedback session");
        feedbackSessionNames.add("Grace Period Session");
        feedbackSessionNames.add("Closed Session");
        feedbackSessionNames.add("Empty session");
        feedbackSessionNames.add("non visible session");
        
        pageData.init(course, instructorList, currentInstructor, offset, sectionNames, feedbackSessionNames);
        
        assertNotNull(pageData.getDeleteCourseButton());
        assertNotNull(pageData.getAddInstructorButton());
        
        assertNotNull(pageData.getInstructorPanelList());
        assertEquals(instructorList.size(), pageData.getInstructorPanelList().size());
        assertEquals(4, pageData.getInstructorPanelList().get(0).getPermissionInputGroup1().size());
        assertEquals(4, pageData.getInstructorPanelList().get(0).getPermissionInputGroup2().size());
        assertEquals(3, pageData.getInstructorPanelList().get(0).getPermissionInputGroup3().size());
        assertEquals(sectionNames.size(), pageData.getInstructorPanelList().get(0).getSectionRows().size());
        assertEquals(feedbackSessionNames.size(), pageData.getInstructorPanelList().get(0).getSectionRows().get(0).getFeedbackSessions().size());
        
        assertNotNull(pageData.getAddInstructorPanel());
        assertEquals(4, pageData.getAddInstructorPanel().getPermissionInputGroup1().size());
        assertEquals(4, pageData.getAddInstructorPanel().getPermissionInputGroup2().size());
        assertEquals(3, pageData.getAddInstructorPanel().getPermissionInputGroup3().size());
        assertEquals(sectionNames.size(), pageData.getAddInstructorPanel().getSectionRows().size());
        assertEquals(feedbackSessionNames.size(), pageData.getAddInstructorPanel().getSectionRows().get(0).getFeedbackSessions().size());
        
    }
}
